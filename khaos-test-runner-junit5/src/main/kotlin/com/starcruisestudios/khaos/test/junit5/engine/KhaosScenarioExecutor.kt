/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.api.ScenarioResult
import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosScenarioTestDescriptor
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestExecutionResult
import org.slf4j.Logger

/**
 * [KhaosExecutor] implementation that will execute tests described by an
 * [KhaosScenarioTestDescriptor].
 */
internal object KhaosScenarioExecutor : KhaosExecutor<KhaosScenarioTestDescriptor> {
    override fun executeDescriptor(
        request: ExecutionRequest,
        testDescriptor: KhaosScenarioTestDescriptor,
        executor: KhaosExecutorCollection
    ) {
        // TODO: Skip based on tags.
        request.engineExecutionListener.executionStarted(testDescriptor)
        testDescriptor.testLogger.printBanner(testDescriptor.displayName)

        val result = KhaosStepExecution().execute(
            testDescriptor.testLogger,
            testDescriptor.setUp,
            testDescriptor.cleanUp,
            testDescriptor.scenarioImplementation)

        testDescriptor.testLogger.printResult(result)

        val testExecutionResult = when (result) {
            is ScenarioResult.PASSED -> TestExecutionResult.successful()
            is ScenarioResult.PENDING -> TestExecutionResult.successful() // TODO: pass or fail based on config.
            is ScenarioResult.ERROR -> TestExecutionResult.failed(result.exception)
            is ScenarioResult.FAILED -> TestExecutionResult.failed(result.exception)
        }
        request.engineExecutionListener.executionFinished(testDescriptor, testExecutionResult)
    }

    private fun Logger.printBanner(displayName: String) {
        info("----------------------------------------")
        info("SCENARIO: $displayName")
        info("----------------------------------------")
    }

    private fun Logger.printResult(result: ScenarioResult) {
        info("        Scenario Result: $result")
    }
}
