/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.descriptors

/**
 * Defines the properties used to describe a test specification.
 *
 * @property testClass The test class that defines the specification's features.
 */
internal data class KhaosSpecProps(
    val testClass: Class<*>
)
