/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.api

/**
 * An adapter interface used to log messages to different outputs.
 */
interface KhaosLogAdapter {
    /**
     * Logs a normal, informational message.
     *
     * @param message The message to log.
     */
    fun info(message: String)

    /**
     * Logs a warning message.
     *
     * @param message The message to log.
     */
    fun warn(message: String)

    /**
     * Logs an error message.
     *
     * @param message The message to log.
     */
    fun error(message: String)
}
