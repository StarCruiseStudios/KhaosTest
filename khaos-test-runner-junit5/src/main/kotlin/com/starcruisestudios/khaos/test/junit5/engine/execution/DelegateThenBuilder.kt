/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine.execution

import com.starcruisestudios.khaos.test.api.StepBlock
import com.starcruisestudios.khaos.test.api.ThenBuilder

/**
 * Provides a delegated implementation of the [ThenBuilder] interface.
 */
internal class DelegateThenBuilder(private val steps: StepExecution) : ThenBuilder {
    override fun Then(description: String) {
        steps.executeNoOpStep(description, StepExecution.TestStep::ThenStep, result = StepExecution.StepResult.ASSUMED)
    }

    override fun <T> Then(description: String, assertion: StepBlock.() -> T): T {
        return steps.executeStep(description,
            StepExecution.TestStep::ThenStep, assertion, resultOnException = StepExecution.StepResult::FAILED
        )
    }

    override fun <T, TEXPECTED> Then(
        description: String,
        expectedValue: TEXPECTED,
        assertion: StepBlock.(TEXPECTED) -> T
    ): T {
        return steps.executeStep(
            description,
            StepExecution.TestStep::ThenStep,
            { assertion(expectedValue) },
            expected = expectedValue,
            resultOnException = StepExecution.StepResult::FAILED
        )
    }
}
