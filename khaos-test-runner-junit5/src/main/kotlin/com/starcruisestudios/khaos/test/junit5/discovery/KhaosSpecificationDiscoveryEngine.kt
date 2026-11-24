/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.discovery

import com.starcruisestudios.khaos.test.api.KhaosSpecification
import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosSpecProps
import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosSpecTestDescriptorFactory
import org.junit.platform.commons.support.ReflectionSupport
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.discovery.ClasspathRootSelector
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.discovery.PackageSelector
import org.junit.platform.engine.discovery.UniqueIdSelector
import java.lang.reflect.Modifier
import kotlin.reflect.full.createInstance

/**
 * Discovery engine that will discover tests on the classpath that extend the
 * [KhaosSpecification] interface.
 *
 * Supports class, method, package, classpath root and unique ID selectors so
 * that IDE runners (which select tests in all of those ways) can find specs.
 */
object KhaosSpecificationDiscoveryEngine : TestDiscoveryEngine {
    private const val SPECIFICATION_SEGMENT_TYPE = "specification"

    /**
     * Discovers tests on the classpath that extend the [KhaosSpecification]
     * interface.
     */
    override fun discover(discoveryRequest: EngineDiscoveryRequest, parent: TestDescriptor) {
        // Specs that should be included in their entirety.
        val fullSpecs = LinkedHashSet<Class<*>>()
        // Specs that should only be included down to the selected unique IDs.
        val restrictedSpecs = LinkedHashMap<Class<*>, MutableList<UniqueId>>()

        discoveryRequest.getSelectorsByType(ClassSelector::class.java).forEach {
            addIfSpecification(fullSpecs, it.getJavaClass())
        }

        // Running a single test: run the containing specification.
        discoveryRequest.getSelectorsByType(MethodSelector::class.java).forEach {
            addIfSpecification(fullSpecs, it.getJavaClass())
        }

        discoveryRequest.getSelectorsByType(PackageSelector::class.java).forEach { selector ->
            ReflectionSupport.findAllClassesInPackage(selector.packageName, ::isScannableSpecification) { true }
                .forEach { fullSpecs.add(it) }
        }

        discoveryRequest.getSelectorsByType(ClasspathRootSelector::class.java).forEach { selector ->
            ReflectionSupport.findAllClassesInClasspathRoot(selector.classpathRoot, ::isScannableSpecification) { true }
                .forEach { fullSpecs.add(it) }
        }

        discoveryRequest.getSelectorsByType(UniqueIdSelector::class.java).forEach { selector ->
            val uniqueId = selector.uniqueId
            if (uniqueId.engineId != parent.uniqueId.engineId) {
                return@forEach
            }

            val specSegment = uniqueId.segments.firstOrNull { it.type == SPECIFICATION_SEGMENT_TYPE }
            if (specSegment == null) {
                // The engine itself was selected, so there is nothing to
                // narrow down; a bare engine ID selects no specific specs.
                return@forEach
            }

            val specClass = ReflectionSupport.tryToLoadClass(specSegment.value).toOptional().orElse(null)
            if (specClass != null && isSpecification(specClass)) {
                restrictedSpecs.getOrPut(specClass) { mutableListOf() }.add(uniqueId)
            }
        }

        fullSpecs.forEach { buildSpec(it, parent) }
        restrictedSpecs
            .filterKeys { it !in fullSpecs }
            .forEach { (specClass, selectedIds) ->
                val specDescriptor = buildSpec(specClass, parent)
                prune(specDescriptor, selectedIds)
            }
    }

    private fun buildSpec(specClass: Class<*>, parent: TestDescriptor): TestDescriptor {
        val specificationInstance = getSpecificationInstance(specClass)
        val props = KhaosSpecProps(specificationInstance, specClass)
        val specDescriptor = KhaosSpecTestDescriptorFactory.build(props, parent)
        parent.addChild(specDescriptor)
        return specDescriptor
    }

    /**
     * Removes every descendant of [descriptor] that is neither an ancestor nor
     * a descendant of one of the [selectedIds].
     */
    private fun prune(descriptor: TestDescriptor, selectedIds: List<UniqueId>) {
        descriptor.children.toList().forEach { child ->
            val keep = selectedIds.any {
                it.hasPrefix(child.uniqueId) || child.uniqueId.hasPrefix(it)
            }
            if (keep) {
                prune(child, selectedIds)
            } else {
                child.removeFromHierarchy()
            }
        }
    }

    private fun addIfSpecification(target: MutableSet<Class<*>>, javaClass: Class<*>) {
        if (isSpecification(javaClass)) {
            target.add(javaClass)
        }
    }

    private fun isSpecification(javaClass: Class<*>): Boolean {
        return KhaosSpecification::class.java.isAssignableFrom(javaClass) &&
                !javaClass.isInterface &&
                !Modifier.isAbstract(javaClass.modifiers)
    }

    /** Classes found by scanning must also be instantiable without an outer instance. */
    private fun isScannableSpecification(javaClass: Class<*>): Boolean {
        val isInner = javaClass.isMemberClass && !Modifier.isStatic(javaClass.modifiers)
        val isLocalOrAnonymous = javaClass.isLocalClass || javaClass.isAnonymousClass
        return isSpecification(javaClass) && !isInner && !isLocalOrAnonymous
    }

    private fun getSpecificationInstance(testClass: Class<*>): KhaosSpecification {
        // Type that implements KhaosSpecification can be an object or a class,
        // and needs to be initialized differently depending on which it is.
        val objectInstance = testClass.kotlin.objectInstance
        return if (objectInstance != null) {
            objectInstance as KhaosSpecification
        } else {
            testClass.kotlin.createInstance() as KhaosSpecification
        }
    }
}
