/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.lang

import java.util.Optional

/**
 * Builder class used to aggregate exceptions into an [AggregateException].
 */
class ExceptionAggregator {
    private val exceptions = mutableListOf<Throwable>()

    /**
     * Adds an exception [e] to the aggregated collection.
     */
    fun addException(e: Throwable) {
        exceptions.add(e)
    }

    /**
     * Gets an [AggregateException] from the collection of exceptions aggregated
     * using the optionally provided [message]. If no exceptions have been
     * aggregated, an empty optional is returned. Any [AggregateException]s in
     * the collection will be flattened.
     */
    fun getException(message: () -> String = { "Multiple errors have occurred." }): Optional<AggregateException> {
        return when {
            exceptions.size >= 1 -> {
                val flattenedExceptions = exceptions.flatMap { exception ->
                    // Flatten AggregateExceptions instead of nesting them.
                    if (exception is AggregateException) {
                        exception.exceptions
                    } else {
                        listOf(exception)
                    }
                }
                Optional.of(AggregateException(message(), flattenedExceptions))
            }
            else -> Optional.empty()
        }
    }
}
