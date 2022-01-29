/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.api.KhaosSlf4jLogAdapter
import com.starcruisestudios.khaos.test.junit5.descriptors.BufferedLogContext
import com.starcruisestudios.khaos.test.junit5.descriptors.DelegatingLogContext
import com.starcruisestudios.khaos.test.junit5.discovery.KhaosSpecificationDiscoveryEngine
import com.starcruisestudios.khaos.test.junit5.discovery.TestDiscoveryEngine
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import java.util.concurrent.TimeUnit

/**
 * A [TestEngine] implementation that facilitates discovery and execution of
 * tests using the Khaos Test engine.
 */
class KhaosTestEngine : TestEngine {
    private val logger = KotlinLogging.logger(javaClass.name)

    companion object {
        /**
         * The Unique ID used to identify the Khaos Test engine.
         */
        const val ID: String = "khaos-test"
    }

    private val executor = KhaosExecutorCollection.build {
        withExecutor(KhaosEngineExecutor)
        withExecutor(KhaosSpecExecutor)
        withExecutor(KhaosFeatureExecutor)
        withExecutor(KhaosScenarioExecutor)
    }

    private val discoveryEngines = listOf<TestDiscoveryEngine>(
        KhaosSpecificationDiscoveryEngine
    )

    override fun getId() = ID

    override fun discover(discoveryRequest: EngineDiscoveryRequest, uniqueId: UniqueId): TestDescriptor {
        val engineDescriptor = EngineDescriptor(uniqueId, "Khaos Test")
        discoveryEngines.forEach {
            it.discover(discoveryRequest, engineDescriptor)
        }

        return engineDescriptor
    }

    override fun execute(request: ExecutionRequest) = runBlocking {
        val startTime = System.nanoTime()

        val root = request.rootTestDescriptor
        val rootLogAdapter = KhaosSlf4jLogAdapter(logger)
        val rootLogContext = if (request.configurationParameters.khaosParameters().parallel) {
            rootLogAdapter.info("Executing tests in parallel")
            BufferedLogContext(rootLogAdapter)
        } else {
            rootLogAdapter.info("Executing tests sequentially")
            DelegatingLogContext(rootLogAdapter)
        }

        executor.execute(request, root, rootLogContext)
        rootLogContext.flush()

        val endTime = System.nanoTime()

        val totalTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime)
        logger.info("Total run time: $totalTime ms")
    }
}
