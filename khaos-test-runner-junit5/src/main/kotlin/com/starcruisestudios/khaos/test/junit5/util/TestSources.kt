/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.util

import com.starcruisestudios.khaos.test.api.SourceLocation
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.FilePosition
import org.junit.platform.engine.support.descriptor.MethodSource

/**
 * Builds the [TestSource] for a feature or scenario declared in the
 * specification [specificationClass].
 *
 * IDEs (IntelliJ in particular) navigate reliably from class and method
 * sources, but not from file sources, so those are preferred:
 * 1. A [MethodSource] when the declaring member is a real JVM method (feature
 *    functions). Kotlin property accessors are reported by reflection as
 *    `<get-name>`, which is not a resolvable method, so they are skipped.
 * 2. A [ClassSource] carrying the declaration's line when it is known.
 * 3. A plain [ClassSource].
 */
internal fun buildTestSource(
    specificationClass: Class<*>,
    sourceLocation: SourceLocation?,
    sourceMethodName: String?
): TestSource {
    return when {
        sourceMethodName != null && !sourceMethodName.startsWith("<") ->
            MethodSource.from(specificationClass.name, sourceMethodName)

        sourceLocation != null && sourceLocation.lineNumber > 0 ->
            ClassSource.from(specificationClass.name, FilePosition.from(sourceLocation.lineNumber))

        else -> ClassSource.from(specificationClass)
    }
}
