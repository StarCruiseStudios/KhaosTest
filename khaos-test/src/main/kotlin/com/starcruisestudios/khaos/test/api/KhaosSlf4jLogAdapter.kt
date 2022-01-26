/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.api

import org.slf4j.Logger

/**
 * An SLF4J adapter for the [KhaosLogAdapter] interface.
 *
 * @property logger The [Logger] instance used to output messages.
 */
class KhaosSlf4jLogAdapter(private val logger: Logger) : KhaosLogAdapter {
    override fun info(message: String) {
        logger.info(message)
    }

    override fun warn(message: String) {
        logger.warn(message)
    }

    override fun error(message: String) {
        logger.error(message)
    }
}
