/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine.execution

import com.starcruisestudios.khaos.test.api.GivenBuilder
import com.starcruisestudios.khaos.test.api.GivenStepBuilder
import com.starcruisestudios.khaos.test.api.KhaosWriter
import com.starcruisestudios.khaos.test.api.ScenarioBuilder
import com.starcruisestudios.khaos.test.api.ScenarioResult
import com.starcruisestudios.khaos.test.api.StepBlock
import com.starcruisestudios.khaos.test.api.StepResult
import com.starcruisestudios.khaos.test.api.TestStepBuilder
import com.starcruisestudios.khaos.test.api.ThenBuilder
import com.starcruisestudios.khaos.test.api.ThenStepBuilder
import com.starcruisestudios.khaos.test.api.WhenBuilder
import com.starcruisestudios.khaos.test.junit5.engine.PendingScenarioException

/**
 * This class is used to execute scenario steps and aggregate results.
 */
class KhaosScenarioExecution
private constructor(private val steps: StepExecution)
    : ScenarioBuilder,
    StepBlock,
    TestStepBuilder by DelegateTestStepBuilder(steps),
    GivenBuilder by DelegateGivenBuilder(steps),
    WhenBuilder by DelegateWhenBuilder(steps),
    ThenBuilder by DelegateThenBuilder(steps)
{
    constructor() : this(StepExecution())

    override fun Pending(reason: String) {
        steps.executeStep(
            "This test is not yet completely implemented.",
            StepExecution.TestStep::PendingStep,
            { throw PendingScenarioException(reason) },
            resultOnException = StepResult::PENDING
        )
    }

    internal fun execute(
        writer: KhaosWriter,
        setup: List<GivenStepBuilder.() -> Unit>,
        cleanup: List<ThenStepBuilder.() -> Unit>,
        scenarioImplementation: ScenarioBuilder.() -> Unit
    ) : ScenarioResult {
        var result: ScenarioResult = ScenarioResult.PASSED
        try {
            setup.forEach(::apply)
            apply(scenarioImplementation)
        } catch (pe: PendingScenarioException) {
            result = ScenarioResult.PENDING(pe)
        } catch (ex: Exception) {
            // Result will be overridden in the finally block if the exception
            // was thrown from within a test step. If it was not thrown from a
            // test step, this will handle the exception as an ERROR.
            result = ScenarioResult.ERROR(ex)
        } finally {
            try {
                cleanup.forEach(::apply)
                steps.logSteps(writer)
            } catch (ex: Exception) {
                // An exception thrown durring cleanup will cause an ERROR
                // result.
                result = ScenarioResult.ERROR(ex)
            }
        }

        return result
    }
}

