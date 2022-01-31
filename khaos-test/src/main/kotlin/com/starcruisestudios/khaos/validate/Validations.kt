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

/**
 * Validates that a value is an instance of the given type [T]. A success
 * [Result] is returned containing the value when it is of the given type,
 * otherwise a failure [Result] with an [IllegalStateException] is returned.
 *
 * A null value will always return a failure [Result].
 *
 * @receiver Any nullable value.
 */
inline fun <reified T> Any?.isInstanceOfType(): Result<T> {
    return this.isInstanceOfType(T::class.java)
}

/**
 * Validates that a value is an instance of the given [type]. A success
 * [Result] is returned containing the value when it is of the given type,
 * otherwise a failure [Result] with an [IllegalStateException] is returned.
 *
 * A null value will always return a failure [Result].
 *
 * @receiver Any nullable value.
 */
infix fun <T> Any?.isInstanceOfType(type: Class<T>): Result<T> {
    return getNonNullResult(this != null && type.isAssignableFrom(this.javaClass)) {
        "The value '$this' should be an instance of type '$type'."
    }.map {
        @Suppress("UNCHECKED_CAST")
        it as T
    }
}

/**
 * Validates that a value is either null or an instance of the given type [T]. A
 * success [Result] is returned containing the value when it is null or of the
 * given type, otherwise a failure [Result] with an [IllegalStateException] is
 * returned.
 *
 * @receiver Any nullable value.
 */
inline fun <reified T> Any?.isNullOrInstanceOfType(): Result<T?> {
    return isNullOrInstanceOfType(T::class.java)
}

/**
 * Validates that a value is either null or an instance of the given [type]. A
 * success [Result] is returned containing the value when it is null or of the
 * given type, otherwise a failure [Result] with an [IllegalStateException] is
 * returned.
 *
 * @receiver Any nullable value.
 */
infix fun <T> Any?.isNullOrInstanceOfType(type: Class<T>): Result<T?> {
    return getResult(this == null || type.isAssignableFrom(this.javaClass)) {
        "The value '$this' should be null or an instance of type '$type'."
    }.map {
        @Suppress("UNCHECKED_CAST")
        it as T?
    }
}

/**
 * Validates that a string is an empty string. A success [Result] is returned
 * containing the value when it is empty, otherwise a failure [Result] with an
 * [IllegalStateException] is returned.
 *
 * @receiver Any string to validate.
 */
fun String.isEmptyString(): Result<String?> {
    return getResult(this.isEmpty()) { "The value '$this' should be empty." }
}

/**
 * Validates that a string is either null or an empty string. A success [Result]
 * is returned containing the value when it is null or empty, otherwise a
 * failure [Result] with an [IllegalStateException] is returned.
 *
 * @receiver Any nullable string to validate.
 */
fun String?.isNullOrEmptyString(): Result<String?> {
    return getResult(this.isNullOrEmpty()) { "The value '$this' should be null or empty." }
}

/**
 * Validates that a string is not an empty string. A success [Result] is
 * returned containing the value when it is not empty, otherwise a failure
 * [Result] with an [IllegalStateException] is returned.
 *
 * @receiver Any string to validate.
 */
fun String.isNotEmptyString(): Result<String> {
    return getResult(!this.isEmpty()) { "The value '$this' should not be empty." }
}

/**
 * Validates that a string is neither null nor an empty string. A success
 * [Result] is returned containing the value when it is not null or empty,
 * otherwise a failure [Result] with an [IllegalStateException] is returned.
 *
 * @receiver Any nullable string to validate.
 */
fun String?.isNotNullOrEmptyString(): Result<String> {
    return getNonNullResult(!this.isNullOrEmpty()) { "The value '$this' should not be null or empty." }
}

/**
 * Validates that a string is either empty, or entirely whitespace. A success
 * [Result] is returned containing the value when it is empty or blank,
 * otherwise a failure [Result] with an [IllegalStateException] is returned.
 *
 * @receiver Any string to validate.
 */
fun String.isBlankString(): Result<String?> {
    return getResult(this.isBlank()) { "The value '$this' should be blank." }
}

/**
 * Validates that a string is either null, empty, or entirely whitespace. A
 * success [Result] is returned containing the value when it is null, empty, or
 * blank, otherwise a failure [Result] with an [IllegalStateException] is
 * returned.
 *
 * @receiver Any nullable string to validate.
 */
fun String?.isNullOrBlankString(): Result<String?> {
    return getResult(this.isNullOrBlank()) { "The value '$this' should be null or blank." }
}

/**
 * Validates that a string is neither empty, nor entirely whitespace. A success
 * [Result] is returned containing the value when it is not empty or blank,
 * otherwise a failure [Result] with an [IllegalStateException] is returned.
 *
 * @receiver Any string to validate.
 */
fun String.isNotBlankString(): Result<String> {
    return getResult(!this.isBlank()) { "The value '$this' should not be blank." }
}

/**
 * Validates that a string is neither null, empty, nor entirely whitespace. A
 * success [Result] is returned containing the value when it is not null, empty,
 * or blank, otherwise a failure [Result] with an [IllegalStateException] is
 * returned.
 *
 * @receiver Any nullable string to validate.
 */
fun String?.isNotNullOrBlankString(): Result<String> {
    return getNonNullResult(!this.isNullOrBlank()) { "The value '$this' should not be null or blank." }
}

/**
 * Validates that a string contains a given [substring]. A success [Result] is
 * returned containing the string when it contains the substring, otherwise a
 * failure [Result] with an [IllegalStateException] is returned.
 *
 * This validation performs a case sensitive search.
 * @see containsCaseInsensitiveSubstring For a case insensitive search.
 *
 * @receiver Any non-null string.
 */
infix fun String.containsSubstring(substring: String): Result<String> {
    return getResult(contains(substring)) {
        "The string should contain substring '$substring'."
    }
}

/**
 * Validates that a string contains a given case insensitive [substring]. A
 * success [Result] is returned containing the string when it contains the
 * substring, otherwise a failure [Result] with an [IllegalStateException] is
 * returned.
 *
 * This validation performs a case insensitive search.
 * @see containsSubstring For a case sensitive search.
 *
 * @receiver Any non-null string.
 */
infix fun String.containsCaseInsensitiveSubstring(substring: String): Result<String> {
    return getResult(contains(substring, true)) {
        "The string should contain substring '$substring'."
    }
}

/**
 * Validates that a string starts with a given [substring]. A success [Result]
 * is returned containing the string when it starts with the substring,
 * otherwise a failure [Result] with an [IllegalStateException] is returned.
 *
 * This validation performs a case sensitive match.
 * @see startsWithCaseInsensitiveSubstring For a case insensitive match.
 *
 * @receiver Any non-null string.
 */
infix fun String.startsWithSubstring(substring: String): Result<String> {
    return getResult(startsWith(substring)) {
        "The string should start with substring '$substring'."
    }
}

/**
 * Validates that a string starts with a given case insensitive [substring]. A
 * success [Result] is returned containing the string when it starts with the
 * substring, otherwise a failure [Result] with an [IllegalStateException] is
 * returned.
 *
 * This validation performs a case insensitive match.
 * @see startsWithSubstring For a case sensitive match.
 *
 * @receiver Any non-null string.
 */
infix fun String.startsWithCaseInsensitiveSubstring(substring: String): Result<String> {
    return getResult(startsWith(substring, true)) {
        "The string should start with substring '$substring'."
    }
}

/**
 * Validates that a string ends with a given [substring]. A success [Result] is
 * returned containing the string when it ends with the substring, otherwise a
 * failure [Result] with an [IllegalStateException] is returned.
 *
 * This validation performs a case sensitive match.
 * @see endsWithCaseInsensitiveSubstring For a case insensitive match.
 *
 * @receiver Any non-null string.
 */
infix fun String.endsWithSubstring(substring: String): Result<String> {
    return getResult(endsWith(substring)) {
        "The string should end with substring '$substring'."
    }
}

/**
 * Validates that a string ends with a given case insensitive [substring]. A
 * success [Result] is returned containing the string when it ends with the
 * substring, otherwise a failure [Result] with an [IllegalStateException] is
 * returned.
 *
 * This validation performs a case insensitive match.
 * @see endsWithSubstring For a case sensitive match.
 *
 * @receiver Any non-null string.
 */
infix fun String.endsWithCaseInsensitiveSubstring(substring: String): Result<String> {
    return getResult(endsWith(substring, true)) {
        "The string should end with substring '$substring'."
    }
}

/**
 * Validates that a string matches a given regex. A success [Result] is returned
 * containing the string when it matches, otherwise a failure [Result] with an
 * [IllegalStateException] is returned.
 *
 * @receiver Any non-null string.
 */
infix fun String.matches(regex: Regex): Result<String> {
    return getResult(regex.matches(this)) {
        "The string should match the regex '${regex.pattern}"
    }
}

/**
 * Validates that a string contains a match for a given regex. A success
 * [Result] is returned containing the original string when it matches,
 * otherwise a failure [Result] with an [IllegalStateException] is returned.
 *
 * @receiver Any non-null string.
 */
infix fun String.containsMatchFor(regex: Regex): Result<String> {
    return getResult(regex.containsMatchIn(this)) {
        "The string should contain a match for the regex '${regex.pattern}"
    }
}

/**
 * Validates that a value is within the given [range] (inclusive). A success
 * [Result] is returned containing the value when it is in the range, otherwise
 * a failure [Result] with an [IllegalStateException] is returned.
 *
 * @receiver Any comparable value.
 */
infix fun <T: Comparable<T>> T.isInRange(range: ClosedRange<T>): Result<T> {
    return getResult(range.contains(this)) {
        "The value '$this' should be in the range [${range.start}, ${range.endInclusive}]."
    }
}

/**
 * Validates that a value is between the given [minInclusive] and
 * [maxInclusive] values. A success [Result] is returned containing the value
 * when it is in the range, otherwise a failure [Result] with an
 * [IllegalStateException] is returned.
 *
 * @receiver Any comparable value.
 */
fun <T: Comparable<T>> T.isInRange(minInclusive: T, maxInclusive: T): Result<T> {
    return getResult(this in minInclusive..maxInclusive) {
        "The value '$this' should be in the range [$minInclusive, $maxInclusive]."
    }
}

/**
 * Validates that an iterable collection is empty. A success [Result] is
 * returned containing the collection when it is empty, otherwise a failure
 * [Result] with an [IllegalStateException] is returned.
 *
 * This validation is performed greedily, and will not iterate the entire
 * collection.
 *
 * @receiver Any iterable collection.
 */
fun <T> Iterable<T>.isEmptyCollection(): Result<Iterable<T>> {
    return getResult(!this.any()) { "The collection should be empty. Found ${this.count()} elements." }
}

/**
 * Validates that an iterable collection is not empty. A success [Result] is
 * returned containing the collection when it is not empty, otherwise a failure
 * [Result] with an [IllegalStateException] is returned.
 *
 * This validation is performed greedily, and will not iterate the entire
 * collection.
 *
 * @receiver Any iterable collection.
 */
fun <T> Iterable<T>.isNotEmptyCollection(): Result<Iterable<T>> {
    return getResult(this.any()) { "The collection should not be empty." }
}

/**
 * Validates that an iterable collection has the given [expectedSize]. A success
 * [Result] is returned containing the collection when it has the given size,
 * otherwise a failure [Result] with an [IllegalStateException] is returned.
 *
 * This validation is performed non-greedily, and will iterate the entire
 * collection.
 *
 * @receiver Any iterable collection.
 */
infix fun <T> Iterable<T>.hasSizeOf(expectedSize: Int): Result<Iterable<T>> {
    val size = this.count()
    return getResult(size == expectedSize) {
        "The collection should contain $expectedSize elements. Found $size elements."
    }
}

/**
 * Validates that an iterable collection is at least as large as the given
 * [expectedSize] (inclusive). A success [Result] is returned containing the
 * collection when it is at least as large as the given size, otherwise a
 * failure [Result] with an [IllegalStateException] is returned.
 *
 * This validation is performed non-greedily, and will iterate the entire
 * collection.
 *
 * @receiver Any iterable collection.
 */
infix fun <T> Iterable<T>.hasSizeOfAtLeast(expectedSize: Int): Result<Iterable<T>> {
    val size = this.count()
    return getResult(size >= expectedSize) {
        "The collection should contain at least $expectedSize elements. Found $size elements."
    }
}

/**
 * Validates that an iterable collection is at most as large as the given
 * [expectedSize] (inclusive). A success [Result] is returned containing the
 * collection when it is at most as large as the given size, otherwise a failure
 * [Result] with an [IllegalStateException] is returned.
 *
 * This validation is performed non-greedily, and will iterate the entire
 * collection.
 *
 * @receiver Any iterable collection.
 */
infix fun <T> Iterable<T>.hasSizeOfAtMost(expectedSize: Int): Result<Iterable<T>> {
    val size = this.count()
    return getResult(size <= expectedSize) {
        "The collection should contain at most $expectedSize elements. Found $size elements."
    }
}

/**
 * Validates that an iterable collection contains all of the given [elements].
 * A success [Result] is returned containing the collection when it contains all
 * of the given elements in any order, otherwise a failure [Result] with an
 * [IllegalStateException] is returned.
 *
 * @receiver Any iterable collection.
 */
fun <T> Iterable<T>.containsAllOf(vararg elements: T): Result<Iterable<T>> {
    return containsAllOf(elements.asIterable())
}

/**
 * Validates that an iterable collection contains all of the given [elements].
 * A success [Result] is returned containing the collection when it contains all
 * of the given elements in any order, otherwise a failure [Result] with an
 * [IllegalStateException] is returned.
 *
 * @receiver Any iterable collection.
 */
infix fun <T> Iterable<T>.containsAllOf(elements: Iterable<T>): Result<Iterable<T>> {
    return getResult(elements.all(this::contains)) {
        "The collection should contain elements '${elements.filter { !this.contains(it) } }'."
    }
}

/**
 * Validates that an iterable collection contains any of the given [elements].
 * A success [Result] is returned containing the collection when it contains at
 * least one of the given elements, otherwise a failure [Result] with an
 * [IllegalStateException] is returned.
 *
 * @receiver Any iterable collection.
 */
fun <T> Iterable<T>.containsAnyOf(vararg elements: T): Result<Iterable<T>> {
    return containsAnyOf(elements.asIterable())
}

/**
 * Validates that an iterable collection contains any of the given [elements].
 * A success [Result] is returned containing the collection when it contains at
 * least one of the given elements, otherwise a failure [Result] with an
 * [IllegalStateException] is returned.
 *
 * This validation is performed greedily, and will not iterate the entire
 * collection.
 *
 * @receiver Any iterable collection.
 */
infix fun <T> Iterable<T>.containsAnyOf(elements: Iterable<T>): Result<Iterable<T>> {
    return getResult(elements.any(this::contains)) {
        "The collection should contain at least one of '$elements'."
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
