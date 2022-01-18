/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.descriptors

import com.starcruisestudios.khaos.test.api.FeatureDefinition
import com.starcruisestudios.khaos.test.api.KhaosSpecification
import com.starcruisestudios.khaos.test.junit5.util.childId
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties

/**
 * Factory used to construct instances of the [KhaosSpecTestDescriptor] class.
 */
internal object KhaosSpecTestDescriptorFactory {
    private const val SPECIFICATION_SEGMENT_TYPE = "specification"

    /**
     * Uses the given [props] to construct a new [KhaosSpecTestDescriptor]
     * instance that is a child of the provided [parent].
     */
    fun build(props: KhaosSpecProps, parent: TestDescriptor): KhaosSpecTestDescriptor {
        val specificationInstance = getSpecificationInstance(props.testClass)
        val specificationTestId = parent.childId(SPECIFICATION_SEGMENT_TYPE, props.testClass.name)

        val testDescriptor = KhaosSpecTestDescriptor(
            props.testClass,
            specificationInstance.testLogger,
            props.testClass.name,
            specificationTestId)
        testDescriptor.setParent(parent)

        // Find all properties that return `FeatureDefinition`s that define
        // the features of a specification.
        specificationInstance::class.memberProperties
            .filter { it.returnType == FeatureDefinition::class.createType() }
            .forEach { feature ->
                // TODO: Define an actual TestDescriptor class.
                val childDescriptor = object : AbstractTestDescriptor(
                    specificationTestId.append("feature", feature.name),
                    feature.name,
                    null
                ) {
                    override fun getType(): TestDescriptor.Type = TestDescriptor.Type.TEST
                }
                testDescriptor.addChild(childDescriptor)
            }

        return testDescriptor
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
