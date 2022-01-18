/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.descriptors

import com.starcruisestudios.khaos.test.junit5.util.childId
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor

/**
 * Factory used to construct instances of the [KhaosFeatureTestDescriptor] class.
 */
internal object KhaosFeatureTestDescriptorFactory {
    private const val FEATURE_SEGMENT_TYPE = "feature"

    /**
     * Uses the given [props] to construct a new [KhaosFeatureTestDescriptor]
     * instance that is a child of the provided [parent].
     */
    fun build(props: KhaosFeatureProps, parent: TestDescriptor): KhaosFeatureTestDescriptor {
        val featureTestId = parent.childId(FEATURE_SEGMENT_TYPE, props.featureName)
        val featureDescriptor = KhaosFeatureTestDescriptor(
            props.featureSteps.setUpFeatureSteps,
            props.featureSteps.cleanUpFeatureSteps,
            props.specDescriptor.testLogger,
            props.featureName,
            featureTestId
        )
        featureDescriptor.setParent(parent)

        // Find all scenarios defined by the feature DSL.
        props.featureSteps.scenarioDefinitions.forEach { (scenarioName, scenarioDefinition) ->
            // TODO: Define an actual TestDescriptor class.
            val scenarioDescriptor = object : AbstractTestDescriptor(
                featureTestId.append("scenario", scenarioName),
                scenarioName,
                null
            ) {
                override fun getType(): TestDescriptor.Type = TestDescriptor.Type.TEST
            }
            featureDescriptor.addChild(scenarioDescriptor)
        }

        return featureDescriptor
    }
}
