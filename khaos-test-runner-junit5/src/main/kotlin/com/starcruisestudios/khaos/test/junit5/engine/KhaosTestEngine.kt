/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.lang.withEach
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor

/**
 * A [TestEngine] implementation that facilitates discovery and execution of
 * tests using the Khaos Test engine.
 */
class KhaosTestEngine : TestEngine {
    companion object {
        /**
         * The Unique ID used to identify the Khaos Test engine.
         */
        const val ID: String = "khaos-test"
    }

    override fun getId() = ID

    override fun discover(discoveryRequest: EngineDiscoveryRequest, uniqueId: UniqueId): TestDescriptor {
        val engineDescriptor = EngineDescriptor(uniqueId, "Khaos Test")

        withEach<TestDiscoveryEngine>(
            KhaosSpecificationDiscoveryEngine
        ) {
            discover(discoveryRequest, engineDescriptor)
        }

        return engineDescriptor
    }

    override fun execute(request: ExecutionRequest) {
        val root = request.rootTestDescriptor
        // Execute the root descriptor.
        request.engineExecutionListener.executionStarted(root)
        root.children
            .forEach { descriptor: TestDescriptor ->
                // Execute the child test descriptors.
                request.engineExecutionListener.executionStarted(descriptor)
                request.engineExecutionListener.executionFinished(descriptor, TestExecutionResult.successful())
            }
        request.engineExecutionListener.executionFinished(root, TestExecutionResult.successful())
    }
}
