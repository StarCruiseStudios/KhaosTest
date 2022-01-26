/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosSpecTestDescriptor
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult

/**
 * [KhaosExecutor] implementation that will execute tests described by an
 * [KhaosSpecTestDescriptor].
 */
internal object KhaosSpecExecutor : KhaosExecutor<KhaosSpecTestDescriptor> {
    override fun executeDescriptor(
        request: ExecutionRequest,
        testDescriptor: KhaosSpecTestDescriptor,
        executor: KhaosExecutorCollection
    ) {
        if (testDescriptor.children.isEmpty()) {
            val reason = "Specification ${testDescriptor.displayName} did not contain any features."
            request.engineExecutionListener.executionSkipped(testDescriptor, reason)
            return
        }

        request.engineExecutionListener.executionStarted(testDescriptor)
        testDescriptor.writer.printSpecBanner(testDescriptor.displayName)

        testDescriptor.children
            .forEach { childDescriptor: TestDescriptor ->
                executor.execute(request, childDescriptor)
            }
        request.engineExecutionListener.executionFinished(testDescriptor, TestExecutionResult.successful())
    }
}
