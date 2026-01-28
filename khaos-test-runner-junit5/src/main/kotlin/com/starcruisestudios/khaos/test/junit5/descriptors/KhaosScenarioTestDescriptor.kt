/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.descriptors

import com.starcruisestudios.khaos.test.api.GivenStepBuilder
import com.starcruisestudios.khaos.test.api.ScenarioBuilder
import com.starcruisestudios.khaos.test.api.ThenStepBuilder
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestTag
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.FilePosition
import org.junit.platform.engine.support.descriptor.FileSource
import org.junit.platform.engine.support.descriptor.MethodSource
import java.io.File
import com.starcruisestudios.khaos.test.api.SourceLocation

/**
 * [TestDescriptor] for a Scenario that has been discovered by the test engine.
 *
 * A scenario is the bottom most element in the khaos test heirarchy, and
 * represents a single complete test case within a feature.
 *
 * @param tags A list of the tags associated with the scenario.
 * @property setUp A list of steps that should be executed before the scenario.
 *   These steps are used to set up the environment for scenario execution.
 * @property cleanUp A list of steps that should be executed after the
 *   scenario. These steps are used to clean up after scenario execution.
 * @property scenarioImplementation The executable code that defines the
 *   scenario's behavior.
 * @property featureTestDescriptor The [KhaosFeatureTestDescriptor] instance
 *   used to define the feature that contains this scenario.
 * @param displayName The name of the test displayed by the test platform.
 * @param uniqueId The unique ID of the scenario test.
 */
internal class KhaosScenarioTestDescriptor(
    tags: List<String>,
    val setUp: List<GivenStepBuilder.() -> Unit>,
    val cleanUp: List<ThenStepBuilder.() -> Unit>,
    val scenarioImplementation: ScenarioBuilder.() -> Unit,
    val featureTestDescriptor: KhaosFeatureTestDescriptor,
    displayName: String,
    uniqueId: UniqueId,
    sourceLocation: SourceLocation?,
    sourceMethodName: String?
) : AbstractTestDescriptor(
    uniqueId,
    displayName,
    buildSource(featureTestDescriptor, sourceLocation, sourceMethodName)
) {
    private val tags: Set<TestTag> = tags.map { TestTag.create(it) }.toSet()

    override fun getType(): TestDescriptor.Type = TestDescriptor.Type.TEST

    override fun getTags() = tags

    companion object {
        private fun buildSource(
            featureTestDescriptor: KhaosFeatureTestDescriptor,
            sourceLocation: SourceLocation?,
            sourceMethodName: String?
        ): org.junit.platform.engine.TestSource {
            return when {
                sourceMethodName != null -> MethodSource.from(
                    featureTestDescriptor.specTestDescriptor.specificationClass.name,
                    sourceMethodName
                )
                sourceLocation != null -> FileSource.from(
                    File(sourceLocation.filePath),
                    FilePosition.from(sourceLocation.lineNumber)
                )
                else -> ClassSource.from(featureTestDescriptor.specTestDescriptor.specificationClass)
            }
        }
    }
}
