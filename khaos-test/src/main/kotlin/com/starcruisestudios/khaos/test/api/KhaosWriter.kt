/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.api

/**
 * Provides an interface used by the Khaos Test engine to write output values.
 */
interface KhaosWriter {
    /**
     * Prints the banner at the beginning of a specification with the
     * [displayName].
     */
    fun printSpecBanner(displayName: String)

    /**
     * Prints the banner at the beginning of a feature with the [displayName]
     * and associated [tags].
     */
    fun printFeatureBanner(displayName: String, tags: Collection<String>)

    fun printFeatureLifecycleBanner(displayName: String)

    /**
     * Prints the banner at the beginning of a scenario with the [displayName]
     * and associated [tags].
     */
    fun printScenarioBanner(displayName: String, tags: Collection<String>)

    /**
     * Prints the banner at the end of a scenario with the [result] of the
     * scenario execution.
     */
    fun printScenarioResultBanner(result: ScenarioResult)

    /**
     * Prints a [stepLabel] that identifies the type of steps that follow.
     *
     * e.g. "Given", "When", "Then"
     */
    fun printStepLabel(stepLabel: String)
    fun printStep(stepMessage: StepMessageProps)
    fun printStepMessage(stepMessage: String)
    fun printLine()
}
