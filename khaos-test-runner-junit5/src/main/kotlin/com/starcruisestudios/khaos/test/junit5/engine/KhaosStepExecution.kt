/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import com.starcruisestudios.khaos.test.api.GivenBuilder
import com.starcruisestudios.khaos.test.api.ScenarioBuilder
import com.starcruisestudios.khaos.test.api.ScenarioResult
import com.starcruisestudios.khaos.test.api.StepBlock
import com.starcruisestudios.khaos.test.api.ThenBuilder
import org.slf4j.Logger

/**
 * This class is used to execute scenario steps and aggregate results.
 */
class KhaosStepExecution : ScenarioBuilder, StepBlock {
    private val steps = mutableListOf<TestStep>()

    override fun Given(description: String) {
        executeNoOpStep(description, TestStep::GivenStep, result = StepResult.ASSUMED)
    }

    override fun <T> Given(description: String, value: StepBlock.() -> T): T {
        return executeStep(description, TestStep::GivenStep, value)
    }

    override fun When(description: String) {
        executeNoOpStep(description, TestStep::WhenStep, result = StepResult.ASSUMED)
    }

    override fun <T> When(description: String, action: StepBlock.() -> T): T {
        return executeStep(description, TestStep::WhenStep, action)
    }

    override fun <T> DeferredWhen(description: String, action: StepBlock.() -> T): () -> T {
        return executeDeferredStep(description, TestStep::WhenStep, action)
    }

    override fun Then(description: String) {
        executeNoOpStep(description, TestStep::ThenStep, result = StepResult.ASSUMED)
    }

    override fun <T> Then(description: String, assertion: StepBlock.() -> T): T {
        return executeStep(description, TestStep::ThenStep, assertion, resultOnException = StepResult::FAILED)
    }

    override fun <T, TEXPECTED> Then(
        description: String,
        expectedValue: TEXPECTED,
        assertion: StepBlock.(TEXPECTED) -> T
    ): T {
        return executeStep(
            description,
            TestStep::ThenStep,
            { assertion(expectedValue) },
            expected = expectedValue,
            resultOnException = StepResult::FAILED
        )
    }

    override fun Info(message: String) {
        executeNoOpStep(message, TestStep::LogStep)
    }

    override fun Pending() {
        executeStep(
            "This test is not yet completely implemented.",
            TestStep::PendingStep,
            { throw PendingScenarioException() },
            resultOnException = StepResult::PENDING
        )
    }

    internal fun execute(
        logger: Logger,
        setup: List<GivenBuilder.() -> Unit>,
        cleanup: List<ThenBuilder.() -> Unit>,
        scenarioImplementation: ScenarioBuilder.() -> Unit
    ) : ScenarioResult {
        var result: ScenarioResult = ScenarioResult.PASSED
        try {
            setup.forEach(::apply)
            apply(scenarioImplementation)
        } catch (ex: Exception) {
            // Result will be overridden in the finally block if the exception
            // was thrown from within a test step. If it was not thrown from a
            // test step, this will handle the exception as an ERROR.
            result = ScenarioResult.ERROR(ex)
        } finally {
            try {
                cleanup.forEach(::apply)
                logSteps(logger)
            } catch (ex: Exception) {
                // An exception thrown durring cleanup will cause an ERROR
                // result.
                result = ScenarioResult.ERROR(ex)
            }
        }

        return result
    }

    internal fun executeAsFeature(
        logger: Logger,
        setup: List<GivenBuilder.() -> Unit>,
        cleanup: List<ThenBuilder.() -> Unit>,
        featureImplementation: () -> Unit
    ) : ScenarioResult {
        var result: ScenarioResult = ScenarioResult.PASSED
        try {
            if (setup.isNotEmpty()) {
                try {
                    Info("------------------------------------------------------------")
                    Info("| Feature Set Up:")
                    Info("------------------------------------------------------------")
                    setup.forEach(::apply)
                    Info("------------------------------------------------------------")
                    logSteps(logger)
                    steps.clear()
                } catch (ex: Exception) {
                    result = ScenarioResult.ERROR(ex)
                }

                if (result != ScenarioResult.PASSED) {
                    return result
                }
            }

            featureImplementation()
        } finally {
            if (cleanup.isNotEmpty()) {
                try {
                    Info("------------------------------------------------------------")
                    Info("| Feature Clean Up:")
                    Info("------------------------------------------------------------")
                    cleanup.forEach(::apply)
                    Info("------------------------------------------------------------")
                    logSteps(logger)

                } catch (ex: Exception) {
                    // An exception thrown durring cleanup will cause an ERROR
                    // result.
                    result = ScenarioResult.ERROR(ex)
                }
            }
        }

        return result
    }

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

    private fun <T> executeStep(
        description: String,
        buildStep: (String) -> TestStep,
        action: StepBlock.() -> T,
        expected: Any? = Unit,
        resultOnException: (Throwable) -> StepResult.FailedStepResult = StepResult::ERROR
    ): T {
        var result:StepResult = StepResult.NONE
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

    private fun <T> executeDeferredStep(
        description: String,
        buildStep: (String) -> TestStep,
        action: StepBlock.() -> T
    ): () -> T {
        addStep(description, StepResult.DEFERRED, buildStep)
        return { action() }
    }

    private fun executeNoOpStep(
        description: String,
        buildStep: (String) -> TestStep,
        result: StepResult = StepResult.NONE
    ) {
        addStep(description, result, buildStep)
    }

    private fun logSteps(logger: Logger) {
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

    private sealed class StepResult(val name: String) {
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

    private sealed class TestStep(val message: String) {
        class GivenStep(message: String) : TestStep(message)
        class WhenStep(message: String) : TestStep(message)
        class ThenStep(message: String) : TestStep(message)
        class LogStep(message: String) : TestStep(message)
        class PendingStep(message: String) : TestStep(message)
    }
}
