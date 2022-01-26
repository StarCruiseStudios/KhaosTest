/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.api.ScenarioResult
import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosFeatureTestDescriptor
import com.starcruisestudios.khaos.test.junit5.engine.execution.KhaosFeatureExecution
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
        if (testDescriptor.children.isEmpty()) {
            val reason = "Feature ${testDescriptor.displayName} did not contain any scenarios."
            request.engineExecutionListener.executionSkipped(testDescriptor, reason)
            return
        }

        request.engineExecutionListener.executionStarted(testDescriptor)
        testDescriptor.writer.printFeatureBanner(
            testDescriptor.displayName,
            testDescriptor.tags.map { it.name })

        testDescriptor.setUpFeatureSteps
        val result = KhaosFeatureExecution().execute(
            testDescriptor.writer,
            testDescriptor.setUpFeatureSteps,
            testDescriptor.cleanUpFeatureSteps
        ) {
            testDescriptor.children
                .forEach { childDescriptor: TestDescriptor ->
                    executor.execute(request, childDescriptor)
                }
        }

        val testExecutionResult = when (result) {
            is ScenarioResult.PASSED -> TestExecutionResult.successful()
            is ScenarioResult.PENDING -> TestExecutionResult.successful() // TODO: pass or fail based on config.
            is ScenarioResult.ERROR -> TestExecutionResult.failed(result.exception)
            is ScenarioResult.FAILED -> TestExecutionResult.failed(result.exception)
        }
        request.engineExecutionListener.executionFinished(testDescriptor, testExecutionResult)
    }
}
