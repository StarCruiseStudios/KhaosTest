/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.descriptors

import com.starcruisestudios.khaos.test.api.FeatureBuilder
import com.starcruisestudios.khaos.test.api.GivenBuilder
import com.starcruisestudios.khaos.test.api.ScenarioBuilder
import com.starcruisestudios.khaos.test.api.ThenBuilder

/**
 * Internal implementation of the [FeatureBuilder] interface that is used to
 * build the steps that execute the set up, clean up, and scenarios within a
 * feature.
 */
internal class KhaosFeatureStepDefinition : FeatureBuilder {
    /**
     * Steps that are used to set up a feature.
     */
    val setUpFeatureSteps = mutableListOf<GivenBuilder.() -> Unit>()

    /**
     * Steps that are used to clean up a feature.
     */
    val cleanUpFeatureSteps = mutableListOf<ThenBuilder.() -> Unit>()

    /**
     * Steps that are used to set up a scenario.
     */
    val setUpEachScenarioSteps = mutableListOf<GivenBuilder.() -> Unit>()

    /**
     * Steps that are used to clean up a scenario.
     */
    val cleanUpEachScenarioSteps = mutableListOf<ThenBuilder.() -> Unit>()

    /**
     * Step definitions for the scenarios that are a part of the feature.
     */
    val scenarioDefinitions = mutableMapOf<String, ScenarioBuilder.() -> Unit>()

    override fun SetUpFeature(definition: GivenBuilder.() -> Unit) {
        setUpFeatureSteps.add(definition)
    }

    override fun CleanUpFeature(definition: ThenBuilder.() -> Unit) {
        cleanUpFeatureSteps.add(definition)
    }

    override fun SetUpEachScenario(definition: GivenBuilder.() -> Unit) {
        setUpEachScenarioSteps.add(definition)
    }

    override fun CleanUpEachScenario(definition: ThenBuilder.() -> Unit) {
        cleanUpEachScenarioSteps.add(definition)
    }

    override fun Scenario(scenarioName: String, definition: ScenarioBuilder.() -> Unit) {
        scenarioDefinitions[scenarioName] = definition
    }
}
