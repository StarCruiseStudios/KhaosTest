/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

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
        request.engineExecutionListener.executionStarted(testDescriptor)
        testDescriptor.testLogger.printBanner(testDescriptor.displayName)

        // TODO: Execute the scenario.

        request.engineExecutionListener.executionFinished(testDescriptor, TestExecutionResult.successful())
    }

    private fun Logger.printBanner(displayName: String) {
        info("----------------------------------------")
        info("SCENARIO: $displayName")
        info("----------------------------------------")
    }
}
