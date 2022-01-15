/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.lang

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AggregateExceptionTest {
    @Test
    fun `Aggregated Exceptions show up in message`() {
        // Given
        // * A first Exception
        val exception1Message = "Exception1"
        val exception1 = Exception(exception1Message)
        // * A second Exception
        val exception2Message = "Exception2"
        val exception2 = Exception(exception2Message)
        // * An AggregateException with both of those
        val aggregateExceptionMessage = "TestException"
        val aggregateException = AggregateException(aggregateExceptionMessage, listOf(exception1, exception2))

        // When
        // * The message is retrieved from the thrown AggregateException
        val exceptionMessage = assertThrows<AggregateException> {
            throw aggregateException
        }.message!!

        // Then
        // * The AggregateException's message was included in the message
        assertTrue(exceptionMessage.contains(aggregateExceptionMessage))
        // * The first Exception's message was included in the message
        assertTrue(exceptionMessage.contains(exception1Message))
        // * The second Exception's message was included in the message
        assertTrue(exceptionMessage.contains(exception2Message))
    }
}
