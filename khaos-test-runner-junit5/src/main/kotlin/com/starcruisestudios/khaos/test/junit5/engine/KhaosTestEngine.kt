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
        execute(request, root)
    }

    private fun execute(request: ExecutionRequest, testDescriptor: TestDescriptor) {
        when (testDescriptor.type) {
            TestDescriptor.Type.CONTAINER -> executeContainer(request, testDescriptor)
            TestDescriptor.Type.TEST -> executeTest(request, testDescriptor)
            else -> TODO("This shouldn't happen")
        }
    }

    private fun executeContainer(request: ExecutionRequest, testDescriptor: TestDescriptor) {
        request.engineExecutionListener.executionStarted(testDescriptor)
        testDescriptor.children
            .forEach { childDescriptor: TestDescriptor ->
                execute(request, childDescriptor)
            }
        request.engineExecutionListener.executionFinished(testDescriptor, TestExecutionResult.successful())
    }

    private fun executeTest(request: ExecutionRequest, testDescriptor: TestDescriptor) {
        request.engineExecutionListener.executionStarted(testDescriptor)
        request.engineExecutionListener.executionFinished(testDescriptor, TestExecutionResult.successful())
    }
}
