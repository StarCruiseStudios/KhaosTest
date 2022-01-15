/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.exceptions

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TestExceptionTest {
    @Test
    fun example_catchTestException() {
        // Given a test action that is throwing an exception as part of the
        // test.
        val action = { throw TestException("This is a test") }

        // The exception can be caught and we are sure that the only
        // TestException that was thrown, is the one in the test action.
        assertThrows<TestException>(action)
    }
}
