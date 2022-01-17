/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.api

import mu.KotlinLogging
import org.junit.platform.commons.annotation.Testable
import org.slf4j.Logger

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
     * The [Logger] instance used to log messages and status from this
     * specification.
     *
     * This property can be overridden to define a custom logger.
     */
    val testLogger: Logger
        get() = KotlinLogging.logger(javaClass.name)
}

/**
 * Marker class used to define a feature that is discoverable within a test
 * specification, the [buildFeature] block is used to define the
 * feature's behavior.
 */
class FeatureDefinition(val buildFeature: FeatureBuilder.() -> Unit)

/**
 * [KhaosTestDsl] function used to define a discoverable feature within a
 * specification. The [feature] block is used to define the feature.
 *
 * This function should be used for the value of a propety in a class that
 * implements the [KhaosSpecification] interface.
 */
@KhaosTestDsl
fun Feature(feature: FeatureBuilder.() -> Unit): FeatureDefinition {
    return FeatureDefinition(feature)
}

/**
 * [KhaosTestDsl] interface used to define the scenarios and lifecycle methods
 * within a feature.
 */
@KhaosTestDsl
interface FeatureBuilder {
    /**
     * Defines one time set up steps that are executed once at the beginning
     * of the feature before any scenarios.
     *
     * A failure in this method will cause an [Result.Error] result for the
     * feature.
     */
    fun SetUpFeature(definition: GivenBuilder.() -> Unit)

    /**
     * Defines one time clean up steps that are executed at the end of the
     * Feature after any Scenarios.
     *
     * These steps will always be executed, even if there is an earlier failure.
     * A failure in this method will cause an [Result.Error] result for the
     * feature.
     */
    fun CleanUpFeature(definition: ThenBuilder.() -> Unit)

    /**
     * Defines set up steps that are executed before each scenario.
     *
     * A failure in this method will cause an [Result.Error] result for the
     * scenario.
     */
    fun SetUpEachScenario(definition: GivenBuilder.() -> Unit)

    /**
     * Defines clean up steps that are executed after each scenario.
     *
     * These steps will always be executed, even if the scenario fails. A
     * failure in this method will cause an [Result.Error] result for the
     * scenario.
     */
    fun CleanUpEachScenario(definition: ThenBuilder.() -> Unit)

    /**
     * Defines the steps that comprise the Scenario.
     *
     * A failure in this method will cause a [Result.Failure] result for the
     * scenario.
     */
    fun Scenario(scenarioName: String, definition: ScenarioBuilder.() -> Unit)
}

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
 * [KhaosTestDsl] interface used to define DSL scopes that use Given steps.
 */
@KhaosTestDsl
interface GivenBuilder : TestStepBuilder {
    /**
     * Defines a value that is part of the initial scenario context that will be
     * operated on. A [description] can be specified to provide more details
     * about the purpose of a value. The result of the given [value] function is
     * returned so it can be assigned to a variable.
     */
    fun <T> Given(description: String, value: () -> T): T

    /**
     * Defines a state or condition that is part of the initial test context. A
     * [description] can be specified to provide more details about the
     * condition.
     */
    fun Given(description: String)
}

/**
 * [KhaosTestDsl] interface used to define DSL scopes that use When steps.
 */
@KhaosTestDsl
interface WhenBuilder : TestStepBuilder {
    /**
     * Defines an event that occurs as part of a scenario. This should be an
     * action that is under test. A [description] can be specified to provide
     * more details about the purpose of an action. The result of the [action]
     * will also be logged, and is returned so it can be assigned to a variable.
     */
    fun <T> When(description: String, action: () -> T): T

    /**
     * Defines an event that will occur as a part of a scenario. This should be
     * an action that is under test, but that will be executed at another time.
     * This is typically used with a verification that the [action] throws an
     * exception. A [description] is specified to provide more details about
     * the purpose of the action. The [action] provided will be returned so
     * it can be assigned to a variable.
     */
    fun <T> DeferredWhen(description: String, action:() -> T): () -> T

    /**
     * Defines an event that occurs as part of a scenario. This should be an
     * action that is under test. A [description] can be specified to provide
     * more details about the purpose of an action.
     */
    fun When(description: String)
}

/**
 * KhaosTestDsl interface used to define DSL scopes that use Then steps.
 */
@KhaosTestDsl
interface ThenBuilder : TestStepBuilder {
    /**
     * Defines an expected outcome of a scenario. This should be an assertion
     * on a value that is returned from an action under test. A [description]
     * can be specified to provide more details about the purpose of an
     * assertion. Whether the assertion passed or failed (threw an exception)
     * will also be logged. The result of the [assertion] is returned so it can
     * be assigned to a variable.
     */
    fun <T> Then(description: String, assertion: () -> T): T

    /**
     * Defines an expected outcome of a scenario. This should be an assertion
     * on a value that is returned from an action under test. A [description]
     * can be specified to provide more details about the purpose of an
     * assertion. An [expectedValue] can be provided to be logged with the
     * description. Whether the assertion passed or failed (threw an exception)
     * will also be logged. The result of the [assertion] is returned so it can
     * be assigned to a variable.
     */
    fun <T, TEXPECTED> Then(description: String, expectedValue: TEXPECTED, assertion: (TEXPECTED) -> T): T

    /**
     * Defines an expected outcome of a scenario. This should be an assertion
     * on a value that is returned from an action under test. A [description]
     * can be specified to provide more details about the purpose of an
     * assertion.
     */
    fun Then(description: String)
}

/**
 * [KhaosTestDsl] interface used to define the DSL scope for defining a
 * scenario.
 */
@KhaosTestDsl
interface ScenarioBuilder : GivenBuilder, WhenBuilder, ThenBuilder {
    /**
     * Defines a clean up [action] that is unique to this scenario.
     *
     * This step will always be executed, even if the scenario fails.
     * A failure in this method will cause an error result for the
     * scenario.
     */
    fun CleanUp(action: () -> Unit)

    /**
     * Indicates that this scenario's implementation is pending completion and
     * should be skipped.
     *
     * Invoking this method will cause a pending result for the
     * scenario.
     */
    fun Pending()

    /**
     * Indicates that this scenario does not meet the filter criteria and should
     * be skipped. The [reason] the test is skipped should be specified and will
     * be displayed in the test output.
     *
     * Invoking this method will cause a skipped result for the scenario.
     */
    fun Skip(reason: String)

    /**
     * Defines the [tags] associated with the test scenario. The scenario will be
     * skipped if none of the tags associated with the test were specified when
     * running the tests.
     */
    fun Tags(vararg tags: String)
}
