/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.api.ScenarioResult
import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosLogContext
import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosSpecTestDescriptor
import kotlinx.coroutines.*
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.support.descriptor.EngineDescriptor

/**
 * [KhaosExecutor] implementation that will execute tests described by an
 * [EngineDescriptor].
 */
internal object KhaosEngineExecutor : KhaosExecutor<EngineDescriptor> {

    override suspend fun executeDescriptor(
        request: ExecutionRequest,
        testDescriptor: EngineDescriptor,
        executor: KhaosExecutorCollection,
        logContext: KhaosLogContext
    ) {
        KhaosTestExecutor.executeContainer(request, testDescriptor, { "No test specifications found." }) {
            if (request.configurationParameters.khaosParameters().parallel) {
                runParallel(request, testDescriptor, executor, logContext)
            } else {
                runSequential(request, testDescriptor, executor, logContext)
            }

            return@executeContainer ScenarioResult.PASSED
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend inline fun runParallel(
        request: ExecutionRequest,
        testDescriptor: EngineDescriptor,
        executor: KhaosExecutorCollection,
        logContext: KhaosLogContext
    ) {
        newSingleThreadContext("KhaosTestLogging").use { loggingCoroutineContext ->
            coroutineScope {
                testDescriptor.children
                    .filterIsInstance<KhaosSpecTestDescriptor>()
                    .forEach { childDescriptor ->
                        val childLogContext = logContext.getChild(childDescriptor.specificationInstance.logAdapter)
                        executor.execute(request, childDescriptor, childLogContext)
                        withContext(loggingCoroutineContext) {
                            logContext.flush()
                        }
                    }
            }
        }
    }

    private suspend inline fun runSequential(
        request: ExecutionRequest,
        testDescriptor: EngineDescriptor,
        executor: KhaosExecutorCollection,
        logContext: KhaosLogContext
    ) {
        testDescriptor.children
            .filterIsInstance<KhaosSpecTestDescriptor>()
            .forEach { childDescriptor ->
                val childLogContext = logContext.getChild(childDescriptor.specificationInstance.logAdapter)
                executor.execute(request, childDescriptor, childLogContext)
                childLogContext.flush()
            }
    }
}
