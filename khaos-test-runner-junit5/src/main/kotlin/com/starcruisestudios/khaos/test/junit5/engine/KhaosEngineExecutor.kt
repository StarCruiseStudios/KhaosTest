/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.api.KhaosSlf4jLogAdapter
import com.starcruisestudios.khaos.test.api.ScenarioResult
import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosLogContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.support.descriptor.EngineDescriptor

/**
 * [KhaosExecutor] implementation that will execute tests described by an
 * [EngineDescriptor].
 */
internal object KhaosEngineExecutor : KhaosExecutor<EngineDescriptor> {
    private val logger = KotlinLogging.logger(javaClass.name)
    private val logAdapter = KhaosSlf4jLogAdapter(logger)

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
                runEachChild(testDescriptor, logContext) { childDescriptor, childContext ->
                    executor.execute(request, childDescriptor, childContext)
                    withContext(loggingCoroutineContext) {
                        childContext.flush()
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
        runEachChild(testDescriptor, logContext) { childDescriptor, childContext ->
            executor.execute(request, childDescriptor, childContext)
            childContext.flush()
        }
    }

    private inline fun runEachChild(
        testDescriptor: EngineDescriptor,
        logContext: KhaosLogContext,
        block: (TestDescriptor, KhaosLogContext) -> Unit
    ) {
        testDescriptor.children.forEach { childDescriptor: TestDescriptor ->
            val childContext = logContext.getChild(logAdapter)
            block(childDescriptor, childContext)
        }
    }
}
