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
        testDescriptor.writer.printScenarioBanner(
            testDescriptor.displayName,
            testDescriptor.tags.map { it.name })

        val result = KhaosScenarioExecution().execute(
            testDescriptor.writer,
            testDescriptor.setUp,
            testDescriptor.cleanUp,
            testDescriptor.scenarioImplementation)

        testDescriptor.writer.printScenarioResultBanner(result)

        val testExecutionResult = when (result) {
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
        request.engineExecutionListener.executionFinished(testDescriptor, testExecutionResult)
    }
}
