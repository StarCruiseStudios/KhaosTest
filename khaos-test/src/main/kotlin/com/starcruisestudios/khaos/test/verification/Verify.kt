/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.verification

/**
 * Provides a method to verify a result is successful.
 */
object Verify {
    /**
     * Verifies that a [result] is successful. If the result is successful,
     * the value it contains is returned, otherwise a [VerificationException] is
     * thrown with the optionally provided [message].
     *
     * @throws VerificationException if the result is not successful.
     */
    fun <T> that(result: Result<T>, message: () -> String? = { null }): T {
        return if (result.isSuccess) {
            result.getOrThrow()
        } else {
            throw VerificationException(message(), result.exceptionOrNull()!!)
        }
    }
}
