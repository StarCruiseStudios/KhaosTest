/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosFeatureTestDescriptor
import com.starcruisestudios.khaos.test.junit5.engine.execution.KhaosFeatureExecution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor

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
        executor: KhaosExecutorCollection
    ) {
        KhaosTestExecutor.executeContainer(request, testDescriptor, emptyContainerMessage) {
            testDescriptor.writer.printFeatureBanner(
                testDescriptor.displayName,
                testDescriptor.tags.map { it.name })

            testDescriptor.setUpFeatureSteps
            val result = KhaosFeatureExecution().execute(
                testDescriptor.writer,
                testDescriptor.setUpFeatureSteps,
                testDescriptor.cleanUpFeatureSteps
            ) {
                coroutineScope {
                    testDescriptor.children
                        .forEach { childDescriptor: TestDescriptor ->
                            launch(Dispatchers.Default) {
                                executor.execute(request, childDescriptor)
                            }
                        }
                }
            }

            return@executeContainer result
        }
    }
}
