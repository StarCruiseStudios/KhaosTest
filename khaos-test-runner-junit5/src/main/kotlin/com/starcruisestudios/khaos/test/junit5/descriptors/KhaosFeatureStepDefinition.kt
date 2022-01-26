/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.descriptors

import com.starcruisestudios.khaos.test.api.FeatureBuilder
import com.starcruisestudios.khaos.test.api.GivenStepBuilder
import com.starcruisestudios.khaos.test.api.ScenarioBuilder
import com.starcruisestudios.khaos.test.api.ScenarioCleanUpBuilder
import com.starcruisestudios.khaos.test.api.ScenarioDefinitionBuilder
import com.starcruisestudios.khaos.test.api.ThenStepBuilder

/**
 * Internal implementation of the [FeatureBuilder] interface that is used to
 * build the steps that execute the set up, clean up, and scenarios within a
 * feature.
 */
internal class KhaosFeatureStepDefinition : FeatureBuilder {
    /**
     * A list of tags associated with the feature.
     */
    val tags = mutableListOf<String>()

    /**
     * Steps that are used to set up a feature.
     */
    val setUpFeatureSteps = mutableListOf<GivenStepBuilder.() -> Unit>()

    /**
     * Steps that are used to clean up a feature.
     */
    val cleanUpFeatureSteps = mutableListOf<ThenStepBuilder.() -> Unit>()

    /**
     * Steps that are used to set up a scenario.
     */
    val setUpEachScenarioSteps = mutableListOf<GivenStepBuilder.() -> Unit>()

    /**
     * Steps that are used to clean up a scenario.
     */
    val cleanUpEachScenarioSteps = mutableListOf<ThenStepBuilder.() -> Unit>()

    /**
     * Step definitions for the scenarios that are a part of the feature.
     */
    val scenarioDefinitions = mutableMapOf<String, ScenarioProperties>()

    override fun SetUpFeature(definition: GivenStepBuilder.() -> Unit) {
        setUpFeatureSteps.add(definition)
    }

    override fun CleanUpFeature(definition: ThenStepBuilder.() -> Unit) {
        cleanUpFeatureSteps.add(definition)
    }

    override fun SetUpEachScenario(definition: GivenStepBuilder.() -> Unit) {
        setUpEachScenarioSteps.add(definition)
    }

    override fun CleanUpEachScenario(definition: ThenStepBuilder.() -> Unit) {
        cleanUpEachScenarioSteps.add(definition)
    }

    override fun Tagged(vararg tags: String): ScenarioDefinitionBuilder {
        return TaggedScenarioBuilder(tags.asList())
    }

    override fun Scenario(scenarioName: String, definition: ScenarioBuilder.() -> Unit) : ScenarioCleanUpBuilder {
        val newScenario = ScenarioProperties(definition)
        scenarioDefinitions[scenarioName] = newScenario
        return ScenarioCleanUpBuilderImpl(newScenario)
    }

    private inner class TaggedScenarioBuilder(private val tags: List<String>) : ScenarioDefinitionBuilder {
        override fun Scenario(scenarioName: String, definition: ScenarioBuilder.() -> Unit): ScenarioCleanUpBuilder {
            val newScenario = ScenarioProperties(definition, tags = tags)
            this@KhaosFeatureStepDefinition.scenarioDefinitions[scenarioName] = newScenario
            return ScenarioCleanUpBuilderImpl(newScenario)
        }
    }

    private class ScenarioCleanUpBuilderImpl(private val scenario: ScenarioProperties) : ScenarioCleanUpBuilder {
        override fun CleanUp(definition: ThenStepBuilder.() -> Unit) {
            scenario.cleanUp = definition
        }
    }

    internal data class ScenarioProperties(
        val definition: ScenarioBuilder.() -> Unit,
        val tags: List<String> = emptyList()
    ) {
        var cleanUp: (ThenStepBuilder.() -> Unit)? = null
    }
}
