/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.api

/**
 * A [KhaosWriter] implementation that outputs raw markdown text.
 *
 * @property log The [KhaosLogAdapter] used to output values.
 */
internal class KhaosMarkdownWriter(private val log: KhaosLogAdapter) : KhaosWriter {
    override fun printSpecBanner(displayName: String) {
        log.info("## SPECIFICATION: $displayName")
        log.info("")
        log.info("----------------------------------------")
        log.info("  ")
    }

    override fun printFeatureBanner(displayName: String, tags: Collection<String>) {
        log.info("### FEATURE: $displayName")
        printTags(tags)
        log.info("")
        log.info("--------------------")
    }

    override fun printFeatureLifecycleBanner(displayName: String) {
        log.info("#### Feature - $displayName")
    }

    override fun printScenarioBanner(displayName: String, tags: Collection<String>) {
        log.info("")
        log.info("#### SCENARIO: $displayName")
        printTags(tags)
    }

    override fun printScenarioResultBanner(result: ScenarioResult) {
        log.info("> Scenario Result: **$result**")
        log.info("  ")
    }

    override fun printStepLabel(stepLabel: String) {
        log.info("")
        log.info("**$stepLabel:**  ")
    }

    override fun printStep(stepMessage: StepMessageProps) {
        val message = buildString {
            append("* ")
            append(stepMessage.description)
            if (stepMessage.expected != Unit) {
                append(" (`${stepMessage.expected}`)")
            }

            if (stepMessage.evaluated != Unit) {
                append(" : `${stepMessage.evaluated}`")
            }

            if (stepMessage.result != StepResult.NONE) {
                append(" -> **${stepMessage.result}**")
            }
        }

        log.info("$message  ")
    }

    override fun printStepMessage(stepMessage: String) {
        log.info("$stepMessage  ")
    }

    override fun printLine() {
        log.info("  ")
    }

    private fun printTags(tags: Collection<String>) {
        if (tags.isNotEmpty()) {
            val allTags = tags.joinToString(" ") { "`[$it]`" }
            log.info("  Tags: $allTags")
        }
    }
}
