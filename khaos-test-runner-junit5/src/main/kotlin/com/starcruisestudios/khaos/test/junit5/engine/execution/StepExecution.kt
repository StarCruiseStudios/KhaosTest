/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine.execution

import com.starcruisestudios.khaos.test.api.KhaosWriter
import com.starcruisestudios.khaos.test.api.StepBlock
import com.starcruisestudios.khaos.test.api.StepMessageProps
import com.starcruisestudios.khaos.test.api.StepResult

/**
 * Aggregates the results of step executions.
 */
internal class StepExecution : StepBlock {
    private val steps = mutableListOf<TestStep>()

    private fun addStep(
        description: String,
        result: StepResult,
        buildStep: (StepMessageProps) -> TestStep,
        expected: Any? = Unit,
        evaluated: Any? = Unit
    ) {
        val message =  StepMessageProps(description, result, expected, evaluated)
        steps.add(buildStep(message))
    }

    fun <T> executeStep(
        description: String,
        buildStep: (StepMessageProps) -> TestStep,
        action: StepBlock.() -> T,
        expected: Any? = Unit,
        resultOnException: (Throwable) -> StepResult.FailedStepResult = StepResult::ERROR
    ): T {
        var result: StepResult = StepResult.NONE
        var evaluated: Any? = Unit
        try {
            evaluated = action()
            result = StepResult.PASSED
            return evaluated
        } catch (ex: Exception) {
            result = resultOnException(ex)
            throw ex
        } finally {
            addStep(description, result, buildStep, expected = expected, evaluated = evaluated)
        }
    }

    fun <T> executeDeferredStep(
        description: String,
        buildStep: (StepMessageProps) -> TestStep,
        action: StepBlock.() -> T
    ): () -> T {
        addStep(description, StepResult.DEFERRED, buildStep)
        return { action() }
    }

    fun executeNoOpStep(
        description: String,
        buildStep: (StepMessageProps) -> TestStep,
        result: StepResult = StepResult.NONE
    ) {
        addStep(description, result, buildStep)
    }

    fun logSteps(writer: KhaosWriter) {
        var prevStep: TestStep? = null
        steps.forEach {
            when (it) {
                is TestStep.GivenStep -> {
                    if (prevStep !is TestStep.GivenStep) {
                        writer.printStepLabel("Given")
                    }
                    writer.printStep(it.message)
                    prevStep = it
                }
                is TestStep.WhenStep -> {
                    if (prevStep !is TestStep.WhenStep) {
                        writer.printStepLabel("When")
                    }
                    writer.printStep(it.message)
                    prevStep = it
                }
                is TestStep.ThenStep -> {
                    if (prevStep !is TestStep.ThenStep) {
                        writer.printStepLabel("Then")
                    }
                    writer.printStep(it.message)
                    prevStep = it
                }
                is TestStep.LogStep -> {
                    writer.printStepMessage(it.message.description)
                }
                is TestStep.PendingStep -> {
                    writer.printStepMessage("Scenario pending: ${it.message.description}")
                }
            }
        }

        writer.printLine()
    }

    fun clear() {
        steps.clear()
    }

    internal sealed class TestStep(val message: StepMessageProps) {
        class GivenStep(message: StepMessageProps) : TestStep(message)
        class WhenStep(message: StepMessageProps) : TestStep(message)
        class ThenStep(message: StepMessageProps) : TestStep(message)
        class LogStep(message: StepMessageProps) : TestStep(message)
        class PendingStep(message: StepMessageProps) : TestStep(message)
    }
}
