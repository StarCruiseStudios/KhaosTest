/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.api

/**
 * A [KhaosWriter] implementation that outputs raw text.
 *
 * @property log The [KhaosLogAdapter] used to output values.
 */
class KhaosTextWriter(private val log: KhaosLogAdapter) : KhaosWriter {
    override fun printSpecBanner(displayName: String) {
        log.info("********************************************************************************")
        log.info("*")
        log.info("*   SPECIFICATION: $displayName")
        log.info("*")
        log.info("********************************************************************************")
    }

    override fun printFeatureBanner(displayName: String, tags: Collection<String>) {
        log.info("============================================================")

        if (tags.isNotEmpty()) {
            log.info("|| FEATURE:")
            tags.forEach {
                log.info("||   [$it]")
            }
            log.info("|| $displayName")
        } else {
            log.info("|| FEATURE: $displayName")
        }
        log.info("============================================================")
    }

    override fun printFeatureLifecycleBanner(displayName: String) {
        printScenarioBanner(displayName, emptyList())
    }

    override fun printScenarioBanner(displayName: String, tags: Collection<String>) {
        log.info("----------------------------------------")
        if (tags.isNotEmpty()) {
            log.info("| SCENARIO:")
            tags.forEach {
                log.info("|   [$it]")
            }
            log.info("| $displayName")
        } else {
            log.info("| SCENARIO: $displayName")
        }
        log.info("----------------------------------------")
    }

    override fun printScenarioResultBanner(result: ScenarioResult) {
        log.info("----------------------------------------")
        log.info("| Scenario Result: $result")
        log.info("----------------------------------------")
    }

    override fun printStepLabel(stepLabel: String) {
        log.info("Given")
    }

    override fun printStep(stepMessage: StepMessageProps) {
        val message = buildString {
            append("* ")
            append(stepMessage.description)
            if (stepMessage.expected != Unit) {
                append(" (${stepMessage.expected})")
            }

            if (stepMessage.evaluated != Unit) {
                append(" : ${stepMessage.evaluated}")
            }

            if (stepMessage.result != StepResult.NONE) {
                append(" -> ${stepMessage.result}")
            }
        }

        log.info(message)
    }

    override fun printStepMessage(stepMessage: String) {
        log.info(stepMessage)
    }

    override fun printLine() {
        log.info("")
    }
}
