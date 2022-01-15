/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.exceptions

/**
 * An exception that can be thrown in a test to verify a test action occurred.
 *
 * A test [message] is provided to add context to the exception thrown and an
 * optional [cause] can be specified.
 *
 * A [TestException] should never be thrown from non-test code. Because of this,
 * it can be caught and handled knowing that it was the exception thrown from
 * the test specified code.
 *
 * @sample com.starcruisestudios.khaos.test.exceptions.TestExceptionTest.example_catchTestException
 */
class TestException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    /**
     * Gets a value indicating whether this exception was handled.
     *
     * This value should be manually modified inside of a catch block to
     * indicate that it was handled.
     */
    var isHandled: Boolean = false
}
