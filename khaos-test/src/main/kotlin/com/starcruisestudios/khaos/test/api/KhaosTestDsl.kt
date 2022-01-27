/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

// "when" is a Kotlin keyword and cannot be used. The capital letters also help
// these stand out from test specific code.
@file:Suppress("FunctionName")

package com.starcruisestudios.khaos.test.api

import mu.KotlinLogging
import org.junit.platform.commons.annotation.Testable

/**
 * Marker annotation for [KhaosTestDsl] classes.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@DslMarker
annotation class KhaosTestDsl

/**
 * All Khaos Test classes should implement this interface and define at least one
 * property using the [Feature]() method.
 */
@Testable
interface KhaosSpecification {
    /**
     * The [KhaosWriter] instance used to log messages and status from this
     * specification.
     *
     * This property can be overridden to define a custom logger.
     */
    val writer: KhaosWriter
        get() = MarkdownKhaosWriter(KhaosSlf4jLogAdapter(KotlinLogging.logger(javaClass.name)))
}

/**
 * [KhaosTestDsl] function used to define a discoverable feature within a
 * specification. The [feature] block is used to define the feature.
 *
 * This function should be used for the value of a propety in a class that
 * implements the [KhaosSpecification] interface.
 */
@KhaosTestDsl
fun Feature(vararg tags: String, feature: FeatureBuilder.() -> Unit): FeatureDefinition {
    return FeatureDefinition(tags.asList(), feature)
}

/**
 * [KhaosTestDsl] interface used to define the scenarios and lifecycle methods
 * within a feature.
 */
@KhaosTestDsl
interface FeatureBuilder : ScenarioDefinitionBuilder{
    /**
     * Defines one time set up steps that are executed once at the beginning
     * of the feature before any scenarios.
     *
     * A failure in this method will cause an [ScenarioResult.ERROR] result for
     * the feature.
     */
    fun SetUpFeature(definition: GivenStepBuilder.() -> Unit)

    /**
     * Defines one time clean up steps that are executed at the end of the
     * Feature after any Scenarios.
     *
     * These steps will always be executed, even if there is an earlier failure.
     * A failure in this method will cause an [ScenarioResult.ERROR] result for
     * the feature.
     */
    fun CleanUpFeature(definition: ThenStepBuilder.() -> Unit)

    /**
     * Defines set up steps that are executed before each scenario.
     *
     * A failure in this method will cause an [ScenarioResult.ERROR] result for
     * the scenario.
     */
    fun SetUpEachScenario(definition: GivenStepBuilder.() -> Unit)

    /**
     * Defines clean up steps that are executed after each scenario.
     *
     * These steps will always be executed, even if the scenario fails. A
     * failure in this method will cause an [ScenarioResult.ERROR] result for
     * the scenario.
     */
    fun CleanUpEachScenario(definition: ThenStepBuilder.() -> Unit)

    /**
     * Defines tags that will be associated with the scenario defined using the
     * returned [ScenarioDefinitionBuilder].
     */
    fun Tagged(vararg tags: String): ScenarioDefinitionBuilder
}

/**
 * [KhaosTestDsl] interface used to define a scenario within a feature.
 */
@KhaosTestDsl
interface ScenarioDefinitionBuilder {
    /**
     * Defines the steps that comprise the Scenario.
     *
     * A failure in this method will cause a [ScenarioResult.ERROR] or
     * [ScenarioResult.FAILED] result for the scenario depending on which step
     * the error occurs in.
     */
    fun Scenario(scenarioName: String, definition: ScenarioBuilder.() -> Unit): ScenarioCleanUpBuilder
}

/**
 * [KhaosTestDsl] interface used to define the steps used to clean up a scenario
 * after execution.
 */
@KhaosTestDsl
interface ScenarioCleanUpBuilder {
    /**
     * Defines clean up steps that are executed after a specific scenario.
     *
     * These steps will always be executed, even if the scenario fails. A
     * failure in this method will cause an [ScenarioResult.ERROR] result for
     * the scenario.
     */
    infix fun CleanUp(definition: ThenStepBuilder.() -> Unit)
}

/**
 * [KhaosTestDsl] interface used to define the DSL scope of a single test step.
 *
 * This is a marker interface is used as an implicit receiver for the code block
 * associated with a test step to prevent the invocation of other step builder
 * functions inside of a test block.
 *
 * For example, this is prevented:
 * ```kotlin
 * Given("Some condition.") {
 *   Then("Some validation.") {}
 * }
 * ```
 */
@KhaosTestDsl
interface StepBlock

/**
 * [KhaosTestDsl] interface used to define DSL scopes that define test steps
 * (Both scenarios and lifecycle methods).
 */
@KhaosTestDsl
interface TestStepBuilder {
    /**
     * Defines a message that will be logged inline with the other test steps.
     * This can be used to log any other test messages that aren't part of
     * one of the other test steps.
     */
    fun Info(message: String)
}

/**
 * Provides an interface for a class that builds Given steps.
 */
@KhaosTestDsl
interface GivenBuilder {
    /**
     * Defines a state or condition that is part of the initial test context. A
     * [description] can be specified to provide more details about the
     * condition.
     */
    fun Given(description: String)

    /**
     * Defines a value that is part of the initial scenario context that will be
     * operated on. A [description] can be specified to provide more details
     * about the purpose of a value. The result of the given [value] function is
     * returned so it can be assigned to a variable.
     *
     * An exception that is thrown from this step will cause a
     * [ScenarioResult.ERROR] result for the scenario.
     */
    fun <T> Given(description: String, value: StepBlock.() -> T): T
}

/**
 * Provides an interface for a class that builds When steps.
 */
@KhaosTestDsl
interface WhenBuilder {
    /**
     * Defines an event that occurs as part of a scenario. This should be an
     * action that is under test. A [description] can be specified to provide
     * more details about the purpose of an action.
     */
    fun When(description: String)

    /**
     * Defines an event that occurs as part of a scenario. This should be an
     * action that is under test. A [description] can be specified to provide
     * more details about the purpose of an action. The result of the [action]
     * will also be logged, and is returned so it can be assigned to a variable.
     *
     * An exception that is thrown from this step will cause a
     * [ScenarioResult.ERROR] result for the scenario.
     */
    fun <T> When(description: String, action: StepBlock.() -> T): T

    /**
     * Defines an event that will occur as a part of a scenario. This should be
     * an action that is under test, but that will be executed at another time.
     * This is typically used with a verification that the [action] throws an
     * exception. A [description] is specified to provide more details about
     * the purpose of the action. The [action] provided will be returned so
     * it can be assigned to a variable.
     *
     * An exception that is thrown from this step will cause a
     * [ScenarioResult.ERROR] result for the scenario.
     */
    fun <T> DeferredWhen(description: String, action:StepBlock.() -> T): () -> T
}

/**
 * Provides an interface for a class that builds Then steps.
 */
@KhaosTestDsl
interface ThenBuilder {
    /**
     * Defines an expected outcome of a scenario. This should be an assertion
     * on a value that is returned from an action under test. A [description]
     * can be specified to provide more details about the purpose of an
     * assertion.
     */
    fun Then(description: String)

    /**
     * Defines an expected outcome of a scenario. This should be an assertion
     * on a value that is returned from an action under test. A [description]
     * can be specified to provide more details about the purpose of an
     * assertion. Whether the assertion passed or failed (threw an exception)
     * will also be logged. The result of the [assertion] is returned so it can
     * be assigned to a variable.
     *
     * An exception that is thrown from this step will cause a
     * [ScenarioResult.FAILED] result for the scenario.
     */
    fun <T> Then(description: String, assertion: StepBlock.() -> T): T

    /**
     * Defines an expected outcome of a scenario. This should be an assertion
     * on a value that is returned from an action under test. A [description]
     * can be specified to provide more details about the purpose of an
     * assertion. An [expectedValue] can be provided to be logged with the
     * description. Whether the assertion passed or failed (threw an exception)
     * will also be logged. The result of the [assertion] is returned so it can
     * be assigned to a variable.
     *
     * An exception that is thrown from this step will cause a
     * [ScenarioResult.FAILED] result for the scenario.
     */
    fun <T, TEXPECTED> Then(description: String, expectedValue: TEXPECTED, assertion: StepBlock.(TEXPECTED) -> T): T
}

/**
 * [KhaosTestDsl] interface used to define DSL scopes that use Given steps.
 */
@KhaosTestDsl
interface GivenStepBuilder : GivenBuilder, TestStepBuilder

/**
 * [KhaosTestDsl] interface used to define DSL scopes that use When steps.
 */
@KhaosTestDsl
interface WhenStepBuilder : WhenBuilder, TestStepBuilder

/**
 * [KhaosTestDsl] interface used to define DSL scopes that use Then steps.
 */
@KhaosTestDsl
interface ThenStepBuilder : ThenBuilder, TestStepBuilder

/**
 * [KhaosTestDsl] interface used to define the DSL scope for defining a
 * scenario.
 */
@KhaosTestDsl
interface ScenarioBuilder : GivenStepBuilder, WhenStepBuilder, ThenStepBuilder {
    /**
     * Indicates that this scenario's implementation is pending completion and
     * should be ignored for the given [reason].
     *
     * Invoking this method will cause a [ScenarioResult.PENDING] result for the
     * scenario.
     */
    fun Pending(reason: String)
}
