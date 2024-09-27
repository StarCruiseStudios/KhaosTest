/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosLogContext
import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosFeatureTestDescriptor
import com.starcruisestudios.khaos.test.junit5.engine.execution.KhaosFeatureExecution
import org.junit.platform.engine.ExecutionRequest

/**
 * [KhaosExecutor] implementation that will execute tests described by an
 * [KhaosFeatureTestDescriptor].
 */
internal object KhaosFeatureExecutor : KhaosExecutor<KhaosFeatureTestDescriptor> {
    private val emptyContainerMessage:  (KhaosFeatureTestDescriptor) -> String = {
        "Feature ${it.displayName} did not contain any scenarios."
    }

    override suspend fun executeDescriptor(
        request: ExecutionRequest,
        testDescriptor: KhaosFeatureTestDescriptor,
        executor: KhaosExecutorCollection,
        logContext: KhaosLogContext
    ) {
        KhaosTestExecutor.executeContainer(request, testDescriptor, emptyContainerMessage) {
            val specificationInstance = testDescriptor
                .specTestDescriptor
                .specificationInstance
            val writer = specificationInstance.formatProvider.buildWriter(logContext)
            writer.printFeatureBanner(testDescriptor.displayName, testDescriptor.tags.map { it.name })

            val result = KhaosFeatureExecution().execute(
                writer,
                testDescriptor.setUpFeatureSteps,
                testDescriptor.cleanUpFeatureSteps
            ) {
                testDescriptor.children.forEach { childDescriptor ->
                    executor.execute(request, childDescriptor, logContext)
                }
            }

            logContext.flush()
            return@executeContainer result
        }
    }
}
