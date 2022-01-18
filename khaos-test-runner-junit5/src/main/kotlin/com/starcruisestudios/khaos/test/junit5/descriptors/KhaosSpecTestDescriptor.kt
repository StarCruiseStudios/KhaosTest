/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.descriptors

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.ClassSource
import org.slf4j.Logger

/**
 * [TestDescriptor] for a Specification that has been discovered by the test
 * engine.
 *
 * A specification is the top level element in the khaos test heirarchy, and
 * represents a collections of features that define how a component or system
 * is used.
 *
 * @property specificationClass The type of the specification that is described.
 * @property testLogger The [Logger] instance used to log messages while
 *   executing the specification.
 * @param displayName The name of the test displayed by the test platform.
 * @param uniqueId The unique ID of the described test.
 */
internal class KhaosSpecTestDescriptor(
    val specificationClass: Class<*>,
    val testLogger: Logger,
    displayName: String,
    uniqueId: UniqueId
) : AbstractTestDescriptor(uniqueId, displayName, ClassSource.from(specificationClass)) {
    override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER
}
