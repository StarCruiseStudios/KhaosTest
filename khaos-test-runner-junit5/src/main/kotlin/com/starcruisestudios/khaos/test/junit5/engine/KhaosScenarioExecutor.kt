/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.junit5.descriptors.KhaosScenarioTestDescriptor
import com.starcruisestudios.khaos.test.junit5.engine.execution.KhaosScenarioExecution
import org.junit.platform.engine.ExecutionRequest

/**
 * [KhaosExecutor] implementation that will execute tests described by an
 * [KhaosScenarioTestDescriptor].
 */
internal object KhaosScenarioExecutor : KhaosExecutor<KhaosScenarioTestDescriptor> {
    override suspend fun executeDescriptor(
        request: ExecutionRequest,
        testDescriptor: KhaosScenarioTestDescriptor,
        executor: KhaosExecutorCollection
    ) {
        KhaosTestExecutor.executeTest(request, testDescriptor) {
            val specificationInstance = testDescriptor
                .featureTestDescriptor
                .specTestDescriptor
                .specificationInstance
            val writer = specificationInstance.formatProvider.buildWriter(
                specificationInstance.logAdapter)
            writer.printScenarioBanner(
                testDescriptor.displayName,
                testDescriptor.tags.map { it.name })

            val result = KhaosScenarioExecution().execute(
                writer,
                testDescriptor.setUp,
                testDescriptor.cleanUp,
                testDescriptor.scenarioImplementation)

            writer.printScenarioResultBanner(result)
            return@executeTest result
        }
    }
}
