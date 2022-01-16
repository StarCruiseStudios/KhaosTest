/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.verification

import com.starcruisestudios.khaos.test.exceptions.TestException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class VerifyTest {
    @Test
    fun verifySuccessDoesNotThrow() {
        val a = 10
        val result = Result.success(a)
        assertEquals(a, Verify.that(result))
    }

    @Test
    fun verifyFailureDoesThrow() {
        val exception = TestException("This is a test exception")
        val result = Result.failure<Int>(exception)
        val e = assertThrows<VerificationException> { Verify.that(result) }
        assertEquals(exception, e.cause)
    }
}
