/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor

/**
 * A [KhaosExecutor] contains the logic to execute a test described by a given
 * type of [TestDescriptor].
 */
internal interface KhaosExecutor<T : TestDescriptor> {
    /**
     * Executes the test described by the given [request] and [testDescriptor].
     * A reference to the root [executor] is provided for nested test execution.
     */
    suspend fun executeDescriptor(request: ExecutionRequest, testDescriptor: T, executor: KhaosExecutorCollection)
}
