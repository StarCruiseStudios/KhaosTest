/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.lang

import org.apache.commons.lang3.exception.ExceptionUtils

/**
 * Exception that wraps an aggregate collection of [exceptions].
 *
 * This is used to allow multiple events to be executed, even if one of them
 * throws an exception. This should capture all of the thrown exceptions which
 * would need to be iterated over and handled individually. The provided
 * [message] should indicate the context in which the events were executed.
 */
class AggregateException(message: String, val exceptions: Iterable<Throwable>) :
    Exception(buildMessage(message, exceptions)) {

    companion object {
        private fun buildMessage(message: String, exceptions: Iterable<Throwable>): String {
            return sequence {
                yield(message)
                var exceptionNumber = 1
                val exceptionCount = exceptions.count()

                exceptions.map {
                    val stackTrace = ExceptionUtils.getStackTrace(it)
                    "Aggregated Exception (${exceptionNumber++}/$exceptionCount):\n$stackTrace"
                }.forEach { yield(it) }

                yield("End Aggregated Exception")
            }.joinToString("\n")
        }
    }
}
