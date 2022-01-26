/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.api

/**
 * Enumerates the result statuses that are possible after executing a test step.
 *
 * @property name The name of the result.
 */
sealed class StepResult(val name: String) {
    /**
     * Enumerates the result status that represent a test failure.
     *
     * @param name The name of the result.
     * @property exception The exception that caused the failure.
     */
    sealed class FailedStepResult(name: String, val exception: Throwable) : StepResult(name)

    /**
     * The step completed successfully.
     */
    object PASSED : StepResult("PASSED")

    /**
     * The step failed with an error occurring during a validation test step.
     *
     * @param exception The exception that caused the failure.
     */
    class FAILED(exception: Throwable) : FailedStepResult("FAILED", exception)

    /**
     * The step failed with an error occurring during a validation test step.
     *
     * @param exception The exception that caused the failure.
     */
    class ERROR(exception: Throwable) : FailedStepResult("ERROR", exception)

    /**
     * The step declares an action that is returned to be executed later.
     */
    object DEFERRED : StepResult("DEFERRED")

    /**
     * The step is assumed to have completed successfully, but did not perform
     * any actual validation.
     */
    object ASSUMED : StepResult("ASSUMED")

    /**
     * The step has an incomplete implementation and is pending completion.
     *
     * @param exception The exception that caused the failure.
     */
    class PENDING(exception: Throwable) : FailedStepResult("PENDING", exception)

    /**
     * The step does not have an associated result.
     */
    object NONE : StepResult("NONE")

    override fun toString(): String = this.name
}
