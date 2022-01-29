/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.api

/**
 * Marker class used to define a feature that is discoverable within a test
 * specification and uses the given [tags], the [buildFeature] block is used to
 * define the feature's behavior. Construct using the [Feature] function inside
 * of a [KhaosSpecification].
 */
class FeatureDefinition internal constructor(
    val tags: List<String>,
    val buildFeature: FeatureBuilder.() -> Unit)
