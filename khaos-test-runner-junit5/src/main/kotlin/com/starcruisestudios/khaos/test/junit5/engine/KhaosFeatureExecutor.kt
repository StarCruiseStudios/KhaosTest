/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosFeatureTestDescriptor
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult

/**
 * [KhaosExecutor] implementation that will execute tests described by an
 * [KhaosFeatureTestDescriptor].
 */
internal object KhaosFeatureExecutor : KhaosExecutor<KhaosFeatureTestDescriptor> {
    override fun executeDescriptor(
        request: ExecutionRequest,
        testDescriptor: KhaosFeatureTestDescriptor,
        executor: KhaosExecutorCollection
    ) {
        request.engineExecutionListener.executionStarted(testDescriptor)
        testDescriptor.children
            .forEach { childDescriptor: TestDescriptor ->
                executor.execute(request, childDescriptor)
            }
        request.engineExecutionListener.executionFinished(testDescriptor, TestExecutionResult.successful())
    }
}
