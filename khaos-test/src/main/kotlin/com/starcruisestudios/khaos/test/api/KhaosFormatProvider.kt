/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.api

/**
 * Provides an interface used to build [KhaosWriter]s that use a specific
 * formatting.
 */
interface KhaosFormatProvider {
    /**
     * Builds a writer that uses the provided [logAdapter].
     */
    fun buildWriter(logAdapter: KhaosLogAdapter): KhaosWriter
}
