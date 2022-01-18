/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.descriptors

import com.starcruisestudios.khaos.test.api.GivenBuilder
import com.starcruisestudios.khaos.test.api.ScenarioBuilder
import com.starcruisestudios.khaos.test.api.ThenBuilder
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.slf4j.Logger

/**
 * [TestDescriptor] for a Scenario that has been discovered by the test engine.
 *
 * A scenario is the bottom most element in the khaos test heirarchy, and
 * represents a single complete test case within a feature.
 *
 * @property setUp A list of steps that should be executed before the scenario.
 *   These steps are used to set up the environment for scenario execution.
 * @property cleanUp A list of steps that should be executed after the
 *   scenario. These steps are used to clean up after scenario execution.
 * @property scenarioImplementation The executable code that defines the
 *   scenario's behavior.
 * @property testLogger The [Logger] instance used to log messages while
 *   executing the scenario.
 * @param displayName The name of the test displayed by the test platform.
 * @param uniqueId The unique ID of the scenario test.
 */
internal class KhaosScenarioTestDescriptor(
    val setUp: List<GivenBuilder.() -> Unit>,
    val cleanUp: List<ThenBuilder.() -> Unit>,
    val scenarioImplementation: ScenarioBuilder.() -> Unit,
    val testLogger: Logger,
    displayName: String,
    uniqueId: UniqueId
) : AbstractTestDescriptor(uniqueId, displayName, null) {
    override fun getType(): TestDescriptor.Type = TestDescriptor.Type.TEST
}
