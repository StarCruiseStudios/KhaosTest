/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine.execution

import com.starcruisestudios.khaos.test.api.GivenBuilder
import com.starcruisestudios.khaos.test.api.GivenStepBuilder
import com.starcruisestudios.khaos.test.api.ScenarioResult
import com.starcruisestudios.khaos.test.api.StepBlock
import com.starcruisestudios.khaos.test.api.TestStepBuilder
import com.starcruisestudios.khaos.test.api.ThenBuilder
import com.starcruisestudios.khaos.test.api.ThenStepBuilder
import org.slf4j.Logger

/**
 * This class is used to execute feature steps and aggregate results.
 */
class KhaosFeatureExecution
private constructor(private val steps: StepExecution)
    : StepBlock,
    GivenStepBuilder,
    ThenStepBuilder,
    TestStepBuilder by DelegateTestStepBuilder(steps),
    GivenBuilder by DelegateGivenBuilder(steps),
    ThenBuilder by DelegateThenBuilder(steps)
{
    constructor() : this(StepExecution())

    internal fun execute(
        logger: Logger,
        setup: List<GivenStepBuilder.() -> Unit>,
        cleanup: List<ThenStepBuilder.() -> Unit>,
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
                    steps.logSteps(logger)
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
                    steps.logSteps(logger)

                } catch (ex: Exception) {
                    // An exception thrown durring cleanup will cause an ERROR
                    // result.
                    result = ScenarioResult.ERROR(ex)
                }
            }
        }

        return result
    }
}
