/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.api.KhaosLogAdapter
import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosLogContext
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
        executor: KhaosExecutorCollection,
        logContext: KhaosLogContext
    ) {
        KhaosTestExecutor.executeContainer(request, testDescriptor, emptyContainerMessage) {
            val specificationInstance = testDescriptor
                .specTestDescriptor
                .specificationInstance
            val writer = specificationInstance.formatProvider.buildWriter(logContext)
            writer.printFeatureBanner(
                testDescriptor.displayName,
                testDescriptor.tags.map { it.name })

            testDescriptor.setUpFeatureSteps
            val result = KhaosFeatureExecution().execute(
                writer,
                testDescriptor.setUpFeatureSteps,
                testDescriptor.cleanUpFeatureSteps
            ) {
                if (request.configurationParameters.khaosParameters().parallel) {
                    runParallel(request, testDescriptor, executor, logContext, specificationInstance.logAdapter)
                } else {
                    runSequential(request, testDescriptor, executor, logContext, specificationInstance.logAdapter)
                }
            }

            return@executeContainer result
        }
    }

    private suspend inline fun runParallel(
        request: ExecutionRequest,
        testDescriptor: KhaosFeatureTestDescriptor,
        executor: KhaosExecutorCollection,
        featureLogContext: KhaosLogContext,
        specificationLogAdapter: KhaosLogAdapter
    ) {
        coroutineScope {
            runEachChild(testDescriptor, featureLogContext, specificationLogAdapter) { childDescriptor, childContext ->
                launch(Dispatchers.Default) {
                    executor.execute(request, childDescriptor, childContext)
                }
            }
        }
    }

    private suspend inline fun runSequential(
        request: ExecutionRequest,
        testDescriptor: KhaosFeatureTestDescriptor,
        executor: KhaosExecutorCollection,
        featureLogContext: KhaosLogContext,
        specificationLogAdapter: KhaosLogAdapter
    ) {
        runEachChild(testDescriptor, featureLogContext, specificationLogAdapter) { childDescriptor, childContext ->
            executor.execute(request, childDescriptor, childContext)
        }
    }

    private inline fun runEachChild(
        testDescriptor: KhaosFeatureTestDescriptor,
        featureLogContext: KhaosLogContext,
        specificationLogAdapter: KhaosLogAdapter,
        block: (TestDescriptor, KhaosLogContext) -> Unit
    ) {
        testDescriptor.children.forEach { childDescriptor: TestDescriptor ->
            val childLogContext = featureLogContext.getChild(specificationLogAdapter)
            block(childDescriptor, childLogContext)
        }
    }
}
