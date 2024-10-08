/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.api.ScenarioResult
import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosLogContext
import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosSpecTestDescriptor
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor

/**
 * [KhaosExecutor] implementation that will execute tests described by an
 * [KhaosSpecTestDescriptor].
 */
internal object KhaosSpecExecutor : KhaosExecutor<KhaosSpecTestDescriptor> {
    private val emptyContainerMessage:  (KhaosSpecTestDescriptor) -> String = {
        "Specification ${it.displayName} did not contain any features."
    }

    override suspend fun executeDescriptor(
        request: ExecutionRequest,
        testDescriptor: KhaosSpecTestDescriptor,
        executor: KhaosExecutorCollection,
        logContext: KhaosLogContext
    ) {
        KhaosTestExecutor.executeContainer(request, testDescriptor, emptyContainerMessage) {
            val writer = testDescriptor.specificationInstance.formatProvider.buildWriter(logContext)

            writer.printSpecBanner(testDescriptor.displayName)

            testDescriptor.children.forEach { childDescriptor: TestDescriptor ->
                executor.execute(request, childDescriptor, logContext)
            }

            logContext.flush()
            return@executeContainer ScenarioResult.PASSED
        }
    }
}
