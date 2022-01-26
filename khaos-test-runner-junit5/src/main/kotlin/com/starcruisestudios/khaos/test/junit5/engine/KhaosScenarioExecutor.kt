/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.api.ScenarioResult
import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosScenarioTestDescriptor
import com.starcruisestudios.khaos.test.junit5.engine.execution.KhaosScenarioExecution
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.TestTag
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
        request.engineExecutionListener.executionStarted(testDescriptor)
        testDescriptor.testLogger.printBanner(testDescriptor.displayName, testDescriptor.tags)

        val result = KhaosScenarioExecution().execute(
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

    private fun Logger.printBanner(displayName: String, tags: Set<TestTag>) {
        info("----------------------------------------")
        if (tags.isNotEmpty()) {
            info("| SCENARIO:")
            tags.forEach {
                info("|   [${it.name}]")
            }
            info("| $displayName")
        } else {
            info("| SCENARIO: $displayName")
        }
        info("----------------------------------------")
    }

    private fun Logger.printResult(result: ScenarioResult) {
        info("----------------------------------------")
        info("| Scenario Result: $result")
        info("----------------------------------------")
    }
}
