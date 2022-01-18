/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.api.KhaosSpecification
import com.starcruisestudios.khaos.test.junit5.util.childId
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource

/**
 * Discovery engine that will discover tests on the classpath that extend the
 * [KhaosSpecification] interface.
 */
object KhaosSpecificationDiscoveryEngine : TestDiscoveryEngine {

    private const val SPECIFICATION_SEGMENT_TYPE = "specification"

    /**
     * Discovers tests on the classpath that extend the [KhaosSpecification]
     * interface.
     */
    override fun discover(discoveryRequest: EngineDiscoveryRequest, parent: TestDescriptor) {
        // Find all KhaosSpecifications on the classpath.
        discoveryRequest.getSelectorsByType(ClassSelector::class.java)
            .map { it.javaClass }
            .filter(KhaosSpecification::class.java::isAssignableFrom)
            .forEach { javaClass ->
                // TODO: Define an actual TestDescriptor class.
                val testDescriptor = object : AbstractTestDescriptor(
                    parent.childId(SPECIFICATION_SEGMENT_TYPE, javaClass.name),
                    javaClass.simpleName,
                    ClassSource.from(javaClass)
                ) {
                    override fun getType(): TestDescriptor.Type  = TestDescriptor.Type.TEST
                }

                parent.addChild(testDescriptor)
            }
    }
}