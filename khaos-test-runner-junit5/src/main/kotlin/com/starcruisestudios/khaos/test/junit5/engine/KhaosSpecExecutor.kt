/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.api.ScenarioResult
import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosLogContext
import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosSpecTestDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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
            val specificationInstance = testDescriptor.specificationInstance
            val specificationLogContext = logContext.getChild(specificationInstance.logAdapter)
            val writer = specificationInstance.formatProvider.buildWriter(specificationLogContext)
            coroutineScope {
                writer.printSpecBanner(testDescriptor.displayName)

                testDescriptor.children
                    .forEach { childDescriptor: TestDescriptor ->
                        val childLogContext = specificationLogContext.getChild(specificationInstance.logAdapter)
                        launch(Dispatchers.Default) {
                            executor.execute(request, childDescriptor, childLogContext)
                        }
                    }
            }

            return@executeContainer ScenarioResult.PASSED
        }
    }
}
