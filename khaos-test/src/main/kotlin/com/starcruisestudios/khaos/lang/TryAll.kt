/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.lang

/**
 * Executes each of the given [actions]. If any of the actions throws an
 * exception, the given [catchBlock] is executed, and the exception is
 * swallowed.
 *
 * Each of the given [actions] is guaranteed to be executed, only if the
 * given [catchBlock] does not throw any exceptions.
 */
fun handleAll(vararg actions: () -> Unit, catchBlock: (e: Exception) -> Unit) {
    actions.forEach {
        @Suppress("TooGenericExceptionCaught") // All exceptions should be handled.
        try {
            it()
        } catch (e: Exception) {
            catchBlock(e)
        }
    }
}

/**
 * Executes each of the given [actions]. If any of the actions throws an
 * exception, the given [catchBlock] is executed, and the exception is
 * swallowed.
 *
 * Each of the given [actions] is guaranteed to be executed, only if the
 * given [catchBlock] does not throw any exceptions.
 */
fun handleAll(actions: Iterable<() -> Unit>, catchBlock: (e: Exception) -> Unit) {
    actions.forEach {
        @Suppress("TooGenericExceptionCaught") // All exceptions should be handled.
        try {
            it()
        } catch (e: Exception) {
            catchBlock(e)
        }
    }
}

/**
 * Executes the given [action] on each of the given [items]. If any of the
 * [items] throws an exception, the given [catchBlock] is executed, and the
 * exception is swallowed.
 *
 * The given [action] is guaranteed to be executed for each of the given
 * [items] only if the given [catchBlock] does not throw any exceptions.
 */
fun <T> handleAll(items: Iterable<T>, action: (T) -> Unit, catchBlock: (e: Exception) -> Unit) {
    items.forEach {
        @Suppress("TooGenericExceptionCaught") // All exceptions should be handled.
        try {
            action(it)
        } catch (e: Exception) {
            catchBlock(e)
        }
    }
}

/**
 * Executes each of the given [actions]. If any of the actions throws an
 * exception, an [AggregateException] will be thrown with the given
 * [message].
 *
 * All of the provided [actions] are guaranteed to be executed.
 */
fun tryAll(vararg actions: () -> Unit, message: (() -> String)? = null) {
    val exceptions = ExceptionAggregator()
    handleAll(*actions) { e ->
        exceptions.addException(e)
    }

    if (message == null) {
        exceptions.getException()
    } else {
        exceptions.getException(message)
    }.ifPresent{ throw it }
}

/**
 * Executes each of the given [actions]. If any of the actions throws an
 * exception, an [AggregateException] will be thrown with the given
 * [message].
 *
 * All of the provided [actions] are guaranteed to be executed.
 */
fun tryAll(actions: Iterable<() -> Unit>, message: (() -> String)? = null) {
    val exceptions = ExceptionAggregator()
    handleAll(actions) { e ->
        exceptions.addException(e)
    }

    if (message == null) {
        exceptions.getException()
    } else {
        exceptions.getException(message)
    }.ifPresent{ throw it }
}

/**
 * Executes the given [action] on each of the given [items]. If any of the
 * [items] throws an exception, an [AggregateException] will be thrown with
 * the given [message].
 *
 * The given [action] is guaranteed to be executed on all of the given
 * [items].
 */
fun <T> tryAll(items: Iterable<T>, action: (T) -> Unit, message: (() -> String)? = null) {
    val exceptions = ExceptionAggregator()
    handleAll(items, action) { e ->
        exceptions.addException(e)
    }

    if (message == null) {
        exceptions.getException()
    } else {
        exceptions.getException(message)
    }.ifPresent{ throw it }
}
