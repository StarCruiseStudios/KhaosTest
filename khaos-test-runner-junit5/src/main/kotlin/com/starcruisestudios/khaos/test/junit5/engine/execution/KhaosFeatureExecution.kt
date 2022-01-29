/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine.execution

import com.starcruisestudios.khaos.test.api.GivenBuilder
import com.starcruisestudios.khaos.test.api.GivenStepBuilder
import com.starcruisestudios.khaos.test.api.KhaosWriter
import com.starcruisestudios.khaos.test.api.ScenarioResult
import com.starcruisestudios.khaos.test.api.StepBlock
import com.starcruisestudios.khaos.test.api.TestStepBuilder
import com.starcruisestudios.khaos.test.api.ThenBuilder
import com.starcruisestudios.khaos.test.api.ThenStepBuilder
import com.starcruisestudios.khaos.test.junit5.engine.PendingScenarioException

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

    internal suspend fun execute(
        logger: KhaosWriter,
        setup: List<GivenStepBuilder.() -> Unit>,
        cleanup: List<ThenStepBuilder.() -> Unit>,
        featureImplementation: suspend () -> Unit
    ) : ScenarioResult {
        var result: ScenarioResult = ScenarioResult.PASSED
        try {
            if (setup.isNotEmpty()) {
                try {
                    logger.printFeatureLifecycleBanner("Set Up")
                    setup.forEach(::apply)
                    steps.logSteps(logger)
                    steps.clear()
                } catch (pe: PendingScenarioException) {
                    result = ScenarioResult.PENDING(pe)
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
                    logger.printFeatureLifecycleBanner("Clean Up")
                    cleanup.forEach(::apply)
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
