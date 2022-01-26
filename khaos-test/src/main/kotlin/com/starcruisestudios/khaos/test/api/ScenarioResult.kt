/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.api

/**
 * Enumerates the result statuses that are possible after executing a scenario.
 */
sealed class ScenarioResult {
    /** The scenario completed successfully. */
    object PASSED : ScenarioResult() {
        override fun toString(): String = "PASSED"
    }

    /**
     * The scenario failed with an error occurring during a validation test step.
     */
    class FAILED(val exception: Throwable) : ScenarioResult() {
        override fun toString(): String = "FAILED"
    }

    /**
     * The scenario failed with an unexpected error during a setup, clean up or
     * non validation test step.
     */
    class ERROR(val exception:Throwable) : ScenarioResult() {
        override fun toString(): String = "ERROR"
    }

    /**
     * The scenario has an incomplete implementation and is pending completion.
     */
    object PENDING : ScenarioResult() {
        override fun toString(): String = "PENDING"
    }
}
