/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine.execution

import com.starcruisestudios.khaos.test.api.GivenBuilder
import com.starcruisestudios.khaos.test.api.StepBlock

/**
 * Provides a delegated implementation of the [GivenBuilder] interface.
 */
internal class DelegateGivenBuilder(private val steps: StepExecution) : GivenBuilder {
    override fun Given(description: String) {
        steps.executeNoOpStep(description, StepExecution.TestStep::GivenStep, result = StepExecution.StepResult.ASSUMED)
    }

    override fun <T> Given(description: String, value: StepBlock.() -> T): T {
        return steps.executeStep(description, StepExecution.TestStep::GivenStep, value)
    }
}
