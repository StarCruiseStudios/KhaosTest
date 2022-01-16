/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.validate

/**
 * Validates that a value passes a given [validation]. A success [Result] is
 * returned containing the value when the validation succeeds, otherwise a
 * failure [Result] with an [IllegalStateException] is returned.
 *
 * @receiver Any nullable value.
 */
infix fun <T : Any?> T.passes(validation: (T) -> Boolean): Result<T> {
    return getResult(validation(this)) { "The value '$this' failed validation." }
}

/**
 * This method is used to indicate that a value failed a custom validation. A
 * A failure [Result] with an [IllegalStateException] using the provided
 * [message] is returned in all cases.
 *
 * @receiver The value that failed validation.
 */
fun Any?.fail(message: String): Result<Nothing> {
    return Result.failure(IllegalStateException("The value '$this' failed validation: $message"))
}

/**
 * Validates that a boolean value is true. A success [Result] is returned
 * containing the boolean value when it is true, otherwise a failure [Result]
 * with an [IllegalStateException] is returned.
 *
 * @receiver A boolean value to validate.
 */
fun Boolean.isTrue(): Result<Boolean> {
    return getResult(this) { "The expression should be true." }
}

/**
 * Validates that a boolean value is false. A success [Result] is returned
 * containing the boolean value when it is false, otherwise a failure [Result]
 * with an [IllegalStateException] is returned.
 *
 * @receiver A boolean value to validate.
 */
fun Boolean.isFalse(): Result<Boolean> {
    return getResult(!this) { "The expression should be false." }
}

/**
 * Validates that a value is equal to some [other] value. A success [Result] is
 * returned containing the value when it is equal to the other value provided,
 * otherwise a failure [Result] with an [IllegalStateException] is returned.
 *
 * This method checks for value equality.
 *
 * @receiver Any nullable value to validate.
 */
infix fun <T : Any?> T.isEqualTo(other: T): Result<T> {
    return getResult(this == other) { "The value '$this' should match value '$other'." }
}

/**
 * Validates that a value is not equal to some [other] value. A success [Result]
 * is returned containing the value when it is not equal to the other value
 * provided, otherwise a failure [Result] with an [IllegalStateException] is
 * returned.
 *
 * This method checks for value equality.
 *
 * @receiver Any nullable value to validate.
 */
infix fun <T : Any?> T.isNotEqualTo(other: T): Result<T> {
    return getResult(this != other) { "The value should not match value '$other'." }
}

/**
 * Validates that a value is the same as some [other] value. A success [Result]
 * is returned containing the value when it is the same as the other value
 * provided, otherwise a failure [Result] with an [IllegalStateException] is
 * returned.
 *
 * This method checks for reference equality.
 *
 * @receiver Any nullable value to validate.
 */
infix fun <T : Any?> T.isSameInstanceAs(other: T): Result<T> {
    return getResult(this === other) { "The instance '$this' should be the same instance as '$other'."}
}

/**
 * Validates that a value is not the same as some [other] value. A success
 * [Result] is returned containing the value when it is not the same as the
 * other value provided, otherwise a failure [Result] with an
 * [IllegalStateException] is returned.
 *
 * This method checks for reference equality.
 *
 * @receiver Any nullable value to validate.
 */
infix fun <T : Any?> T.isNotSameInstanceAs(other: T): Result<T> {
    return getResult(this !== other) { "The instance '$this' should not be the same instance as '$other'."}
}

/**
 * Validates that a value is null. A success [Result] is returned containing the
 * value when it is null, otherwise a failure [Result] with an
 * [IllegalStateException] is returned.
 *
 * @receiver Any nullable value to validate.
 */
fun <T : Any?> T.isNull(): Result<T> {
    return getResult(this == null) { "The value '$this' should be null." }
}

/**
 * Validates that a value is not null. A success [Result] is returned containing
 * the value when it is not null, otherwise a failure [Result] with an
 * [IllegalStateException] is returned.
 *
 * @receiver Any nullable value to validate.
 */
fun <T> T?.isNotNull(): Result<T> {
    return getNonNullResult(this != null) { "The value should not be null." }
}

/**
 * Validates that an action throws an exception of the given type [T].  A
 * success [Result] is returned containing the thrown exception if one was
 * thrown of the given type, otherwise a failure [Result] with an
 * [IllegalStateException] is returned. If the action threw an exception that
 * was not of the given type, that exception will be provided as the inner
 * exception of the returned [IllegalStateException].
 *
 * @receiver Any parameterless action.
 */
inline fun <reified T : Throwable> (()->Any?).doesThrow(): Result<T> {
    return this.doesThrow(T::class.java)
}

/**
 * Validates that an action throws an exception of the given [type].  A
 * success [Result] is returned containing the thrown exception if one was
 * thrown of the given type, otherwise a failure [Result] with an
 * [IllegalStateException] is returned. If the action threw an exception that
 * was not of the given type, that exception will be provided as the inner
 * exception of the returned [IllegalStateException].
 *
 * @receiver Any parameterless action.
 */
infix fun <T : Throwable> (()->Any?).doesThrow(type: Class<T>): Result<T> {
    @Suppress("TooGenericExceptionCaught") // Have to catch everything to handle generic exceptions.
    try {
        this()
    } catch (e: Exception) {
        @Suppress("InstanceOfCheckForException") // Generic exception types cannot be caught.
        return if (type.isAssignableFrom(e.javaClass)) {
            @Suppress("UNCHECKED_CAST") // It is checked above
            Result.success(e as T)
        } else {
            return Result.failure(IllegalStateException("An unexpected exception was thrown.", e))
        }
    }

    return Result.failure(IllegalStateException("No exception was thrown."))
}

/**
 * Validates that an action does not throw any exceptions.  A success [Result]
 * is returned containing the value returned by the action if no exception was
 * thrown, otherwise a failure [Result] with an [IllegalStateException]
 * containing thrown exception as an inner exception is returned.
 *
 * @receiver Any parameterless action.
 */
fun <T> (()->T).doesNotThrow(): Result<T> {
    @Suppress("TooGenericExceptionCaught") // Any exception should cause a failure result.
    return try {
        val result = this()
        Result.success(result)
    } catch (e: Exception) {
        Result.failure(IllegalStateException("An unexpected exception was thrown.", e))
    }
}

private inline fun <T> T.getResult(result: Boolean, failureMessage: () -> String): Result<T> {
    return if (result) {
        Result.success(this)
    } else {
        Result.failure(IllegalStateException(failureMessage()))
    }
}

private inline fun <T> T?.getNonNullResult(result: Boolean, failureMessage: () -> String): Result<T> {
    return if (result) {
        Result.success(this!!)
    } else {
        Result.failure(IllegalStateException(failureMessage()))
    }
}
