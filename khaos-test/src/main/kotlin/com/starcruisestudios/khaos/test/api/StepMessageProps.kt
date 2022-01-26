/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.api

/**
 * Defines the properties that can be recorded as a part of a test step.
 *
 * @property description A description of the action that took place in the
 *   test step.
 * @property result The result of executing the test step.
 * @property expected The optional expected value provided to a validation test
 *   step.
 * @property evaluated The optional value that was evaluated as a result of the
 *   action that took place in the test step.
 */
data class StepMessageProps(
    val description: String,
    val result: StepResult,
    val expected: Any? = Unit,
    val evaluated: Any? = Unit)
