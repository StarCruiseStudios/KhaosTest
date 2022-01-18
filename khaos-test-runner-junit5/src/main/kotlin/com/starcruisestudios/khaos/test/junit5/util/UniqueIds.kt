/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.util

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId

/**
 * Creates a uniqueId as a child of a [TestDescriptor]'s unique ID.
 */
internal fun TestDescriptor.childId(segmentType: String, value: String): UniqueId {
    return uniqueId.append(segmentType, value)
}
