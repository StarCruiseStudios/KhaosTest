/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.descriptors

/**
 * Defines the properties used to describe a test Feature.
 *
 * @property featureName The name of the feature.
 * @property featureSteps The steps used to define the behavior of the feature
 *   and its scenarios.
 * @property specDescriptor The test descriptor of the specification this
 *   feature is associated with.
 */
internal data class KhaosFeatureProps(
    val featureName: String,
    val featureSteps: KhaosFeatureStepDefinition,
    val specDescriptor: KhaosSpecTestDescriptor
)
