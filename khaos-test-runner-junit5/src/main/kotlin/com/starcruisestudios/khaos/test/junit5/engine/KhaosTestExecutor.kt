/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.api.ScenarioResult
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult

/**
 * Contains the generalized logic for executing and reporting a test or
 * container.
 */
internal object KhaosTestExecutor {
    /**
     * Runs a container [execution] defined by the given [request] and
     * [testDescriptor] and reports the result to the test framework, skipping
     * execution if the container is empty providing the given [emptyMessage].
     */
    inline fun <T : TestDescriptor> executeContainer(
        request: ExecutionRequest,
        testDescriptor: T,
        emptyMessage: (T) -> String,
        execution: () -> ScenarioResult
    ) {
        if (testDescriptor.children.isEmpty()) {
            val reason = emptyMessage(testDescriptor)
            request.engineExecutionListener.executionSkipped(testDescriptor, reason)
            return
        }

        executeTest(request, testDescriptor, execution)
    }

    /**
     * Runs a test [execution] defined by the given [request] and
     * [testDescriptor] and reports the result to the test framework.
     */
    inline fun <T : TestDescriptor> executeTest(
        request: ExecutionRequest,
        testDescriptor: T,
        execution: () -> ScenarioResult
    ) {
        request.engineExecutionListener.executionStarted(testDescriptor)
        val testExecutionResult = handleResult(request, execution())
        request.engineExecutionListener.executionFinished(testDescriptor, testExecutionResult)
    }

    /**
     * Handles the [result] of executing the given [request] and converts it to the correct
     * [TestExecutionResult] for reporting.
     */
    fun handleResult(request: ExecutionRequest, result: ScenarioResult): TestExecutionResult {
        return when (result) {
            is ScenarioResult.PASSED -> TestExecutionResult.successful()
            is ScenarioResult.PENDING -> {
                val config = request.configurationParameters.khaosParameters()
                if (config.failOnPending) {
                    TestExecutionResult.failed(result.exception)
                } else {
                    TestExecutionResult.successful()
                }
            }
            is ScenarioResult.ERROR -> TestExecutionResult.failed(result.exception)
            is ScenarioResult.FAILED -> TestExecutionResult.failed(result.exception)
        }
    }
}
