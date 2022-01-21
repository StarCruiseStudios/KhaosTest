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
import org.junit.platform.engine.TestTag
import org.slf4j.Logger

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
        testDescriptor.testLogger.printBanner(testDescriptor.displayName, testDescriptor.tags)

        // TODO: Execute feature set up steps.

        testDescriptor.children
            .forEach { childDescriptor: TestDescriptor ->
                executor.execute(request, childDescriptor)
            }

        // TODO: Execute feature clean up steps.

        request.engineExecutionListener.executionFinished(testDescriptor, TestExecutionResult.successful())
    }

    private fun Logger.printBanner(displayName: String, tags: Set<TestTag>) {
        info("============================================================")
        if (tags.isNotEmpty()) {
            tags.forEach {
                info("||   [${it.name}]")
            }
        }
        info("|| FEATURE: $displayName")
        info("============================================================")
    }
}
