/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.support.descriptor.EngineDescriptor

/**
 * [KhaosExecutor] implementation that will execute tests described by an
 * [EngineDescriptor].
 */
internal object KhaosEngineExecutor : KhaosExecutor<EngineDescriptor> {
    override fun executeDescriptor(
        request: ExecutionRequest,
        testDescriptor: EngineDescriptor,
        executor: KhaosExecutorCollection
    ) {
        if (testDescriptor.children.isEmpty()) {
            val reason = "No test specifications found."
            request.engineExecutionListener.executionSkipped(testDescriptor, reason)
            return
        }

        request.engineExecutionListener.executionStarted(testDescriptor)
        testDescriptor.children
            .forEach { childDescriptor: TestDescriptor ->
                executor.execute(request, childDescriptor)
            }
        request.engineExecutionListener.executionFinished(testDescriptor, TestExecutionResult.successful())
    }
}
