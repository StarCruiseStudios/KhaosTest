/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.descriptors

import com.starcruisestudios.khaos.test.junit5.util.childId

/**
 * Factory used to construct instances of the [KhaosScenarioTestDescriptor] class.
 */
internal object KhaosScenarioTestDescriptorFactory {
    private const val SCENARIO_SEGMENT_TYPE = "scenario"

    /**
     * Uses the given [props] to construct a new [KhaosScenarioTestDescriptor]
     * instance that is a child of the provided [parent].
     */
    fun build(props: KhaosScenarioProps, parent: KhaosFeatureTestDescriptor): KhaosScenarioTestDescriptor {
        val scenarioTestId = parent.childId(SCENARIO_SEGMENT_TYPE, props.scenarioName)
        val tags:MutableList<String> = props.tags.toMutableList()
        tags.addAll(parent.tags.map { it.name })

        val scenarioDescriptor = KhaosScenarioTestDescriptor(
            tags,
            props.setUp,
            props.cleanUp,
            props.scenarioImplementation,
            parent,
            props.scenarioName,
            scenarioTestId
        )
        scenarioDescriptor.setParent(parent)

        // No other tests nested inside of the scenario to discover.

        return scenarioDescriptor
    }
}
