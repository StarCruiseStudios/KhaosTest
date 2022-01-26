/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine.execution

import com.starcruisestudios.khaos.test.api.StepBlock
import org.slf4j.Logger

/**
 * Aggregates the results of step executions.
 */
internal class StepExecution : StepBlock {
    private val steps = mutableListOf<TestStep>()

    private fun addStep(
        description: String,
        result: StepResult,
        buildStep: (String) -> TestStep,
        expected: Any? = Unit,
        evaluated: Any? = Unit
    ) {
        val message =  buildString {
            append(description)
            if (expected != Unit) {
                append(" ($expected)")
            }

            if (evaluated != Unit) {
                append(" : $evaluated")
            }

            if (result != StepResult.NONE) {
                append(" -> $result")
            }
        }

        steps.add(buildStep(message))
    }

    fun <T> executeStep(
        description: String,
        buildStep: (String) -> TestStep,
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
        buildStep: (String) -> TestStep,
        action: StepBlock.() -> T
    ): () -> T {
        addStep(description, StepResult.DEFERRED, buildStep)
        return { action() }
    }

    fun executeNoOpStep(
        description: String,
        buildStep: (String) -> TestStep,
        result: StepResult = StepResult.NONE
    ) {
        addStep(description, result, buildStep)
    }

    fun logSteps(logger: Logger) {
        var prevStep: TestStep? = null
        steps.forEach {
            when (it) {
                is TestStep.GivenStep -> {
                    if (prevStep !is TestStep.GivenStep) {
                        logger.info("Given")
                    }
                    logger.info(" * ${it.message}")
                    prevStep = it
                }
                is TestStep.WhenStep -> {
                    if (prevStep !is TestStep.WhenStep) {
                        logger.info("When")
                    }
                    logger.info(" * ${it.message}")
                    prevStep = it
                }
                is TestStep.ThenStep -> {
                    if (prevStep !is TestStep.ThenStep) {
                        logger.info("Then")
                    }
                    logger.info(" * ${it.message}")
                    prevStep = it
                }
                is TestStep.LogStep -> {
                    logger.info(it.message)
                }
                is TestStep.PendingStep -> {
                    logger.info("Scenario pending: ${it.message}")
                }
            }
        }
    }

    fun clear() {
        steps.clear()
    }

    internal sealed class StepResult(val name: String) {
        sealed class FailedStepResult(name: String, val exception: Throwable) : StepResult(name)

        object PASSED : StepResult("PASSED")
        class FAILED(exception: Throwable) : FailedStepResult("FAILED", exception)
        class ERROR(exception: Throwable) : FailedStepResult("ERROR", exception)
        object DEFERRED : StepResult("DEFERRED")
        object ASSUMED : StepResult("ASSUMED")
        class PENDING(exception: Throwable) : FailedStepResult("PENDING", exception)
        object NONE : StepResult("NONE")

        override fun toString(): String = name
    }

    internal sealed class TestStep(val message: String) {
        class GivenStep(message: String) : TestStep(message)
        class WhenStep(message: String) : TestStep(message)
        class ThenStep(message: String) : TestStep(message)
        class LogStep(message: String) : TestStep(message)
        class PendingStep(message: String) : TestStep(message)
    }
}
