/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.descriptors

import com.starcruisestudios.khaos.test.api.GivenStepBuilder
import com.starcruisestudios.khaos.test.api.ScenarioBuilder
import com.starcruisestudios.khaos.test.api.ThenStepBuilder

/**
 * Defines the properties used to describe a test Scenario.
 *
 * @property scenarioName The name of the scenario.
 * @property tags The tags associated with the scenario.
 * @property setUp The steps used to define the scenario setup.
 * @property cleanUp The steps used to define the scenario cleanup.
 * @property scenarioImplementation The test code that defines the
 *   implementation of the test scenario.
 */
internal data class KhaosScenarioProps(
    val scenarioName: String,
    val tags: List<String>,
    val setUp: List<GivenStepBuilder.() -> Unit>,
    val cleanUp: List<ThenStepBuilder.() -> Unit>,
    val scenarioImplementation: ScenarioBuilder.() -> Unit
)
