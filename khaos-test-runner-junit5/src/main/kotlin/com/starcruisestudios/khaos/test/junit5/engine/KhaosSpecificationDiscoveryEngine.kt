/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.api.KhaosSpecification
import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosSpecProps
import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosSpecTestDescriptorFactory
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.discovery.ClassSelector

/**
 * Discovery engine that will discover tests on the classpath that extend the
 * [KhaosSpecification] interface.
 */
object KhaosSpecificationDiscoveryEngine : TestDiscoveryEngine {
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
                val props = KhaosSpecProps(javaClass)
                val testDescriptor = KhaosSpecTestDescriptorFactory.build(props, parent)
                parent.addChild(testDescriptor)
            }
    }
}
