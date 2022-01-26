/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.descriptors

import com.starcruisestudios.khaos.test.api.GivenStepBuilder
import com.starcruisestudios.khaos.test.api.ThenStepBuilder
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestTag
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.slf4j.Logger

/**
 * [TestDescriptor] for a Feature that has been discovered by the test engine.
 *
 * A feature is a middle tier container element in the khaos test heirarchy, and
 * represents a collection of test scenarios all related to a single logical
 * feature.
 *
 * @property tags A list of tags associated with the feature.
 * @property setUpFeatureSteps A list of steps that should be executed before
 *   all scenarios in a feature. These steps are used to set up the environment
 *   for scenario execution.
 * @property cleanUpFeatureSteps A list of steps that should be executed after
 *   all scenarios in a feature. These steps are used to clean up after scenario
 *   execution.
 * @property testLogger The [Logger] instance used to log messages while
 *   executing the feature.
 * @param displayName The name of the test displayed by the test platform.
 * @param uniqueId The unique ID of the feature test.
 */
internal class KhaosFeatureTestDescriptor(
    tags: List<String>,
    val setUpFeatureSteps: List<GivenStepBuilder.() -> Unit>,
    val cleanUpFeatureSteps: List<ThenStepBuilder.() -> Unit>,
    val testLogger: Logger,
    displayName: String,
    uniqueId: UniqueId
) : AbstractTestDescriptor(uniqueId, displayName) {
    private val tags: Set<TestTag> = tags.map { TestTag.create(it) }.toSet()

    override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER

    override fun getTags() = tags
}
