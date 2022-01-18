/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.descriptors

import com.starcruisestudios.khaos.test.junit5.util.childId
import org.junit.platform.engine.TestDescriptor

/**
 * Factory used to construct instances of the [KhaosScenarioTestDescriptor] class.
 */
internal object KhaosScenarioTestDescriptorFactory {
    private const val SCENARIO_SEGMENT_TYPE = "scenario"

    /**
     * Uses the given [props] to construct a new [KhaosScenarioTestDescriptor]
     * instance that is a child of the provided [parent].
     */
    fun build(props: KhaosScenarioProps, parent: TestDescriptor): KhaosScenarioTestDescriptor {
        val scenarioTestId = parent.childId(SCENARIO_SEGMENT_TYPE, props.scenarioName)
        val scenarioDescriptor = KhaosScenarioTestDescriptor(
            props.setUp,
            props.cleanUp,
            props.scenarioImplementation,
            props.featureDescriptor.testLogger,
            props.scenarioName,
            scenarioTestId
        )
        scenarioDescriptor.setParent(parent)

        // No other tests nested inside of the scenario to discover.

        return scenarioDescriptor
    }
}
