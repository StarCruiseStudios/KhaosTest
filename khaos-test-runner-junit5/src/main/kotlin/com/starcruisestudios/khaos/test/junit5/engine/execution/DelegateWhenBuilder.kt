/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine.execution

import com.starcruisestudios.khaos.test.api.StepBlock
import com.starcruisestudios.khaos.test.api.StepResult
import com.starcruisestudios.khaos.test.api.WhenBuilder

/**
 * Provides a delegated implementation of the [WhenBuilder] interface.
 */
internal class DelegateWhenBuilder(private val steps: StepExecution) : WhenBuilder {
    override fun When(description: String) {
        steps.executeNoOpStep(description, StepExecution.TestStep::WhenStep, result = StepResult.ASSUMED)
    }

    override fun <T> When(description: String, action: StepBlock.() -> T): T {
        return steps.executeStep(description, StepExecution.TestStep::WhenStep, action)
    }

    override fun <T> DeferredWhen(description: String, action: StepBlock.() -> T): () -> T {
        return steps.executeDeferredStep(description, StepExecution.TestStep::WhenStep, action)
    }

}
