/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine.execution

import com.starcruisestudios.khaos.test.api.TestStepBuilder

/**
 * Provides a delegated implementation of the [TestStepBuilder] interface.
 */
internal class DelegateTestStepBuilder(private val steps: StepExecution) : TestStepBuilder {
    override fun Info(message: String) {
        steps.executeNoOpStep(message, StepExecution.TestStep::LogStep)
    }
}
