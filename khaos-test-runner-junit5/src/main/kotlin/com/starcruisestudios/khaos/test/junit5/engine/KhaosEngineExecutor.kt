/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.api.KhaosSlf4jLogAdapter
import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosLogContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.support.descriptor.EngineDescriptor

/**
 * [KhaosExecutor] implementation that will execute tests described by an
 * [EngineDescriptor].
 */
internal object KhaosEngineExecutor : KhaosExecutor<EngineDescriptor> {
    private val logger = KotlinLogging.logger(javaClass.name)

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun executeDescriptor(
        request: ExecutionRequest,
        testDescriptor: EngineDescriptor,
        executor: KhaosExecutorCollection,
        logContext: KhaosLogContext
    ) {
        if (testDescriptor.children.isEmpty()) {
            val reason = "No test specifications found."
            request.engineExecutionListener.executionSkipped(testDescriptor, reason)
            return
        }

        val logAdapter = KhaosSlf4jLogAdapter(logger)
        newSingleThreadContext("KhaosTestLogging").use { loggingCoroutineContext ->
            request.engineExecutionListener.executionStarted(testDescriptor)
            coroutineScope {
                testDescriptor.children
                    .forEach { childDescriptor: TestDescriptor ->
                        val childContext = logContext.getChild(logAdapter)
                        launch(Dispatchers.Default) {
                            executor.execute(request, childDescriptor, childContext)
                            withContext(loggingCoroutineContext) {
                                childContext.flush()
                            }
                        }
                    }
            }
        }

        request.engineExecutionListener.executionFinished(testDescriptor, TestExecutionResult.successful())
    }
}
