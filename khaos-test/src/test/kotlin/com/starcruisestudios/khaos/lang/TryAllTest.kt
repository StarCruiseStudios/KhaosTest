/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.lang

import com.starcruisestudios.khaos.test.exceptions.TestException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class TryAllTest {

    @Test
    fun `handleAll(vararg) handles all exceptions`() {

        // Given
        // * An exception
        val exception1 = TestException("1")
        // * Another exception
        val exception2 = TestException("2")

        // When
        // * The exceptions are thrown inside of different handleAll blocks
        val action: () -> Unit = {
            handleAll(
                { throw exception1 },
                { throw exception2 }
            ) { (it as TestException).isHandled = true }
        }

        // Then
        // * No exception is thrown
        assertDoesNotThrow(action)
        // * the first exception was handled
        assertTrue(exception1.isHandled)
        // * the second exception was handled
        assertTrue(exception2.isHandled)
    }

    @Test
    fun `handleAll(vararg) succeeds with no exceptions`() {

        // Given
        // * the initial value of block1Executed
        var block1Executed = false
        // * the initial value of block2Executed
        var block2Executed = false

        // When
        // * No exceptions are thrown inside any handleAll blocks
        val action: () -> Unit = {
            handleAll(
                { block1Executed = true },
                { block2Executed = true }
            ) { (it as TestException).isHandled = true }
        }

        // Then
        // * No exception is thrown
        assertDoesNotThrow(action)
        // * the first block was executed
        assertTrue(block1Executed)
        // * the second block was executed
        assertTrue(block2Executed)
    }

    @Test
    fun `handleAll(iterable) handles all exceptions`() {

        // Given
        // * An exception
        val exception1 = TestException("1")
        // * Another exception
        val exception2 = TestException("2")

        // When
        // * The exceptions are thrown inside of different handleAll blocks
        val action: () -> Unit = {
            handleAll(
                listOf(
                    { throw exception1 },
                    { throw exception2 }
                )
            ) { (it as TestException).isHandled = true }
        }

        // Then
        // * No exception is thrown
        assertDoesNotThrow(action)
        // * the first exception was handled
        assertTrue(exception1.isHandled)
        // * the second exception was handled
        assertTrue(exception2.isHandled)
    }

    @Test
    fun `handleAll(iterable) succeeds with no exceptions`() {

        // Given
        // * the initial value of block1Executed
        var block1Executed = false
        // * the initial value of block2Executed
        var block2Executed = false

        // When
        // * No exceptions are thrown inside any handleAll blocks
        val action: () -> Unit = {
            handleAll(
                listOf(
                    { block1Executed = true },
                    { block2Executed = true }
                )
            ) { (it as TestException).isHandled = true }
        }

        // Then
        // * No exception is thrown
        assertDoesNotThrow(action)
        // * the first block was executed
        assertTrue(block1Executed)
        // * the second block was executed
        assertTrue(block2Executed)
    }

    @Test
    fun `handleAll(iterable, action) handles all exceptions`() {

        // Given
        // * An exception
        val exception1 = TestException("1")
        // * Another exception
        val exception2 = TestException("2")
        // * A runnable
        val runnable1: Runnable = Runnable { throw exception1 }
        // * Another runnable
        val runnable2: Runnable = Runnable { throw exception2 }

        // When
        // * the exceptions are thrown inside of different handleAll blocks
        val action: () -> Unit = {
            handleAll(listOf(runnable1, runnable2), Runnable::run) {
                (it as TestException).isHandled = true
            }
        }

        // Then
        // * No exception is thrown
        assertDoesNotThrow(action)
        // * the first exception was handled
        assertTrue(exception1.isHandled)
        // * the second exception was handled
        assertTrue(exception2.isHandled)
    }

    @Test
    fun `handleAll(iterable, action) succeeds with no exceptions`() {

        // Given
        // * the initial value of block1Executed
        var block1Executed = false
        // * the initial value of block2Executed
        var block2Executed = false
        // * A runnable
        val runnable1: Runnable = Runnable { block1Executed = true }
        // * Another runnable
        val runnable2: Runnable = Runnable { block2Executed = true }

        // When
        // * No exceptions are thrown inside any handleAll blocks
        val action: () -> Unit = {
            handleAll(listOf(runnable1, runnable2), Runnable::run) {
                (it as TestException).isHandled = true
            }
        }

        // Then
        // * No exception is thrown
        assertDoesNotThrow(action)
        // * the first block was executed
        assertTrue(block1Executed)
        // * the second block was executed
        assertTrue(block2Executed)
    }

    @Test
    fun `tryAll(vararg) tries all blocks when exeptions are thrown`() {

        // Given
        // * An exception
        val exception1 = TestException("1")
        // * Another exception
        val exception2 = TestException("2")

        // When
        // * the exceptions are thrown inside of different tryAll blocks
        val action: () -> Unit = {
            tryAll(
                { throw exception1 },
                { throw exception2 }
            )
        }

        // Then
        // * An AggregateException is thrown
        val thrownException = assertThrows<AggregateException>(action)
        // * the aggregateException contained both exceptions
        assertEquals(2, thrownException.exceptions.count())
        assertTrue(thrownException.exceptions.contains(exception1))
        assertTrue(thrownException.exceptions.contains(exception2))
    }

    @Test
    fun `tryAll(vararg) succeeds with no exceptions`() {

        // Given
        // * the initial value of block1Executed
        var block1Executed = false
        // * the initial value of block2Executed
        var block2Executed = false

        // When
        // * no exceptions are thrown inside any tryAll blocks
        val action: () -> Unit = {
            tryAll(
                { block1Executed = true },
                { block2Executed = true }
            )
        }

        // Then
        // * No exception is thrown
        assertDoesNotThrow(action)
        // * the first block was executed
        assertTrue(block1Executed)
        // * the second block was executed
        assertTrue(block2Executed)
    }

    @Test
    fun `tryAll(vararg) uses provided custom message when exeptions are thrown`() {

        // Given
        // * An exception
        val exception1 = TestException("1")
        // * Another exception
        val exception2 = TestException("2")
        // * a custom message
        val customMessage = "This is a custom message."

        // When
        // * the exceptions are thrown inside of different tryAll blocks
        val action: () -> Unit = {
            tryAll(
                { throw exception1 },
                { throw exception2 }
            ) { customMessage }
        }

        // Then
        // * An AggregateException is thrown
        val thrownException = assertThrows<AggregateException>(action)
        // * the aggregateException used the custom message
        assertTrue(thrownException.message?.startsWith(customMessage) ?: false)
    }

    @Test
    fun `tryAll(iterable) tries all blocks when exeptions are thrown`() {

        // Given
        // * An exception
        val exception1 = TestException("1")
        // * Another exception
        val exception2 = TestException("2")

        // When
        // * the exceptions are thrown inside of different tryAll blocks
        val action: () -> Unit = {
            tryAll(
                listOf(
                    { throw exception1 },
                    { throw exception2 }
                )
            )
        }

        // Then
        // * An AggregateException is thrown
        val thrownException = assertThrows<AggregateException>(action)
        // * the aggregateException contained both exceptions
        assertEquals(2, thrownException.exceptions.count())
        assertTrue(thrownException.exceptions.contains(exception1))
        assertTrue(thrownException.exceptions.contains(exception2))
    }

    @Test
    fun `tryAll(iterable) succeeds with no exceptions`() {

        // Given
        // * the initial value of block1Executed
        var block1Executed = false
        // * the initial value of block2Executed
        var block2Executed = false

        // When
        // * no exceptions are thrown inside any tryAll blocks
        val action: () -> Unit = {
            tryAll(
                listOf(
                    { block1Executed = true },
                    { block2Executed = true }
                )
            )
        }

        // Then
        // * No exception is thrown
        assertDoesNotThrow(action)
        // * the first block was executed
        assertTrue(block1Executed)
        // * the second block was executed
        assertTrue(block2Executed)
    }

    @Test
    fun `tryAll(iterable) uses provided custom message when exeptions are thrown`() {

        // Given
        // * An exception
        val exception1 = TestException("1")
        // * Another exception
        val exception2 = TestException("2")
        // * a custom message
        val customMessage = "This is a custom message."

        // When
        // * the exceptions are thrown inside of different tryAll blocks
        val action: () -> Unit = {
            tryAll(
                listOf(
                    { throw exception1 },
                    { throw exception2 }
                )
            ) { customMessage }
        }

        // Then
        // * An AggregateException is thrown
        val thrownException = assertThrows<AggregateException>(action)
        // * the aggregateException used the custom message
        assertTrue(thrownException.message?.startsWith(customMessage) ?: false)
    }

    @Test
    fun `tryAll(iterable, action) tries all blocks when exeptions are thrown`() {

        // Given
        // * An exception
        val exception1 = TestException("1")
        // * Another exception
        val exception2 = TestException("2")
        // * A runnable
        val runnable1 = Runnable { throw exception1 }
        // * Another runnable
        val runnable2 = Runnable { throw exception2 }

        // When
        // * the exceptions are thrown inside of different tryAll blocks
        val action: () -> Unit = { tryAll(listOf(runnable1, runnable2), Runnable::run) }

        // Then
        // * An AggregateException is thrown
        val thrownException = assertThrows<AggregateException>(action)
        // * the aggregateException contained both exceptions
        assertEquals(2, thrownException.exceptions.count())
        assertTrue(thrownException.exceptions.contains(exception1))
        assertTrue(thrownException.exceptions.contains(exception2))
    }

    @Test
    fun `tryAll(iterable, action) succeeds with no exceptions`() {

        // Given
        // * the initial value of block1Executed
        var block1Executed = false
        // * the initial value of block2Executed
        var block2Executed = false
        // * A runnable
        val runnable1 = Runnable { block1Executed = true }
        // * Another runnable
        val runnable2 = Runnable { block2Executed = true }

        // When
        // * no exceptions are thrown inside any tryAll blocks
        val action: () -> Unit = {
            tryAll(listOf(runnable1, runnable2), Runnable::run)
        }

        // Then
        // * No exception is thrown
        assertDoesNotThrow(action)
        // * the first block was executed
        assertTrue(block1Executed)
        // * the second block was executed
        assertTrue(block2Executed)
    }

    @Test
    fun `tryAll(iterable, action) uses provided custom message when exeptions are thrown`() {

        // Given
        // * An exception
        val exception1 = TestException("1")
        // * Another exception
        val exception2 = TestException("2")
        // * A runnable
        val runnable1 = Runnable { throw exception1 }
        // * Another runnable
        val runnable2 = Runnable { throw exception2 }

        // * a custom message
        val customMessage = "This is a custom message."

        // When
        // * the exceptions are thrown inside of different tryAll blocks"
        val action: () -> Unit = {
            tryAll(listOf(runnable1, runnable2), Runnable::run) { customMessage }
        }

        // Then
        // * An AggregateException is thrown
        val thrownException = assertThrows<AggregateException>(action)
        // * the aggregateException used the custom message
        assertTrue(thrownException.message?.startsWith(customMessage) ?: false)
    }

    @Test
    fun `tryAll flattens nested AggregateExceptions`() {

        // Given
        // * An exception
        val exception1 = TestException("1")
        // * Another exception
        val exception2 = TestException("2")
        // * an aggregate exception
        val aggregateException = AggregateException("A", listOf(exception2))

        // When
        // * the exceptions are thrown inside of different tryAll blocks
        val action: () -> Unit = {
            tryAll(
                { throw exception1 },
                { throw aggregateException }
            )
        }

        // Then
        // * An AggregateException is thrown
        val thrownException = assertThrows<AggregateException>(action)
        // * the first exception was not handled
        assertFalse(exception1.isHandled)
        // * the second exception was not handled
        assertFalse(exception2.isHandled)
        // * the aggregateException contained both exceptions
        assertEquals(2, thrownException.exceptions.count())
        assertTrue(thrownException.exceptions.contains(exception1))
        assertTrue(thrownException.exceptions.contains(exception2))
    }
}
