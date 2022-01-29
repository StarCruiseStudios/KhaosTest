/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.descriptors

import com.starcruisestudios.khaos.test.api.KhaosLogAdapter

/**
 * Provides an interface for accessing the logging properties and methods in a
 * test execution.
 */
internal interface KhaosLogContext : KhaosLogAdapter {
    /**
     * Creates a child log context using the specified [logAdapter].
     */
    fun getChild(logAdapter: KhaosLogAdapter) : KhaosLogContext

    /**
     * Flushes this logContext.
     */
    fun flush()
}
