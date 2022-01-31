/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.validate

import com.starcruisestudios.khaos.lang.tryAll
import com.starcruisestudios.khaos.test.exceptions.TestException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.lang.IllegalStateException

class ValidationsTest {
    @Test
    fun passes() {
        tryAll(
            {
                // The value is returned when validation passes.
                val result = 10 passes { true }
                assertTrue(result.isSuccess)
                assertEquals(10, result.getOrThrow())
            },
            {
                // The result is a failure when validation fails.
                val result = 10 passes { false }
                assertTrue(result.isFailure)
            },
            {
                // A null value is handled correctly.
                val result = null passes { true }
                assertTrue(result.isSuccess)
                assertNull(result.getOrThrow())
            }
        )
    }

    @Test
    fun fail() {
        tryAll(
            {
                // The result is a failure for any value
                val result = Any().fail("This should fail.")
                assertTrue(result.isFailure)
            },
            {
                // A null value is handled correctly.
                val result = null.fail("This should fail.")
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun isTrue() {
        tryAll(
            {
                // True passes validation
                val result = true.isTrue()
                assertTrue(result.isSuccess)
                assertTrue(result.getOrThrow())
            },
            {
                // False fails validation
                val result = false.isTrue()
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun isFalse() {
        tryAll(
            {
                // False passes validation
                val result = false.isFalse()
                assertTrue(result.isSuccess)
                assertFalse(result.getOrThrow())
            },
            {
                // True fails validation
                val result = true.isFalse()
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun isEqualTo() {
        tryAll(
            {
                // Any value is equal to itself.
                val a = Any()
                val result = a isEqualTo a
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Equivalent values are equal.
                val a = 10
                val result = a isEqualTo 10
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Different values are not equal.
                val a = 10
                val result = a isEqualTo 20
                assertTrue(result.isFailure)
            },
            {
                // A null value is handled correctly.
                val result = null isEqualTo null
                assertTrue(result.isSuccess)
                assertNull(result.getOrThrow())
            }
        )
    }

    @Test
    fun isNotEqualTo() {
        tryAll(
            {
                // Any value is not not equal to itself.
                val a = Any()
                val result = a isNotEqualTo a
                assertTrue(result.isFailure)
            },
            {
                // Equivalent values are not not equal.
                val a = 10
                val result = a isNotEqualTo 10
                assertTrue(result.isFailure)
            },
            {
                // Different values are not equal.
                val a = 10
                val result = a isNotEqualTo 20
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // A null value is handled correctly.
                val result = null isNotEqualTo null
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun isSameInstanceAs() {
        tryAll(
            {
                // Any value is the same instance as itself.
                val a = Any()
                val result = a isSameInstanceAs a
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Equivalent values are not the same instance.
                val a = Any()
                val result = a isSameInstanceAs Any()
                assertTrue(result.isFailure)
            },
            {
                // Different values are not the same instance.
                val a = 10
                val result = a isSameInstanceAs 20
                assertTrue(result.isFailure)
            },
            {
                // A null value is handled correctly.
                val result = null isSameInstanceAs null
                assertTrue(result.isSuccess)
                assertNull(result.getOrThrow())
            }
        )
    }

    @Test
    fun isNotSameInstanceAs() {
        tryAll(
            {
                // Any value is the not not the same instance as itself.
                val a = Any()
                val result = a isNotSameInstanceAs a
                assertTrue(result.isFailure)
            },
            {
                // Equivalent values are not the same instance.
                val a = Any()
                val result = a isNotSameInstanceAs Any()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Different values are not the same instance.
                val a = 10
                val result = a isNotSameInstanceAs 20
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // A null value is handled correctly.
                val result = null isNotSameInstanceAs null
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun isNull() {
        tryAll(
            {
                // null is null
                val result = null.isNull()
                assertTrue(result.isSuccess)
                assertNull(result.getOrThrow())
            },
            {
                // Any value is not null
                val result = Any().isNull()
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun isNotNull() {
        tryAll(
            {
                // null is not not null
                val result = null.isNotNull()
                assertTrue(result.isFailure)
            },
            {
                // Any value is not null
                val a = Any()
                val result = a.isNotNull()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            }
        )
    }

    @Test
    fun doesThrow() {
        tryAll(
            {
                // Thrown exception of expected type passes.
                val exception = TestException("Test Exception")
                val result = { throw exception } doesThrow TestException::class.java
                assertTrue(result.isSuccess)
                assertSame(exception, result.getOrThrow())
            },
            {
                // Thrown exception of expected type passes with type parameter.
                val exception = TestException("Test Exception")
                val result = { throw exception }.doesThrow<TestException>()
                assertTrue(result.isSuccess)
                assertSame(exception, result.getOrThrow())
            },
            {
                // Thrown exception of different type fails.
                val exception = TestException("Test Exception")
                val result = { throw exception } doesThrow IllegalStateException::class.java
                assertTrue(result.isFailure)
            },
            {
                // Thrown exception of different type fails with type parameter.
                val exception = TestException("Test Exception")
                val result = { throw exception }.doesThrow<IllegalStateException>()
                assertTrue(result.isFailure)
            },
            {
                // No thrown exception fails.
                val result = { } doesThrow TestException::class.java
                assertTrue(result.isFailure)
            },
            {
                // No thrown exception fails with type parameter.
                val result = { }.doesThrow<TestException>()
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun doesNotThrow() {
        tryAll(
            {
                // Action that throws exception fails
                val exception = TestException("Test Exception")
                val result = { throw exception }.doesNotThrow()
                assertTrue(result.isFailure)
            },
            {
                // Action that does not throw exception succeeds.
                val a = Any()
                val result = { a }.doesNotThrow()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            }
        )
    }

    @Test
    fun isInstanceOfType() {
        open class TestType

        class TestSubType : TestType()

        tryAll(
            {
                // Instance that is the same type passes.
                val a = TestType()
                val result = a isInstanceOfType TestType::class.java
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Instance that is the same type passes with type param.
                val a = TestType()
                val result = a.isInstanceOfType<TestType>()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Instance that is the subtype passes.
                val a = TestSubType()
                val result = a isInstanceOfType TestType::class.java
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Instance that is the subtype passes with type param.
                val a = TestSubType()
                val result = a.isInstanceOfType<TestType>()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Instance that is not the type fails.
                val a = Any()
                val result = a isInstanceOfType TestType::class.java
                assertTrue(result.isFailure)
            },
            {
                // Instance that is not the type fails with type param.
                val a = Any()
                val result = a.isInstanceOfType<TestType>()
                assertTrue(result.isFailure)
            },
            {
                // null fails.
                val a = null
                val result = a isInstanceOfType TestType::class.java
                assertTrue(result.isFailure)
            },
            {
                // null fails with type param.
                val a = null
                val result = a.isInstanceOfType<TestType>()
                assertTrue(result.isFailure)
            },
            {
                // null fails when stored as type.
                val a: TestType? = null
                val result = a isInstanceOfType TestType::class.java
                assertTrue(result.isFailure)
            },
            {
                // null fails when stored as type with type param.
                val a: TestType? = null
                val result = a.isInstanceOfType<TestType>()
                assertTrue(result.isFailure)
            },
        )
    }

    @Test
    fun isNullOrInstanceOfType() {
        open class TestType

        class TestSubType : TestType()

        tryAll(
            {
                // Instance that is the same type passes.
                val a = TestType()
                val result = a isNullOrInstanceOfType TestType::class.java
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Instance that is the same type passes with type param.
                val a = TestType()
                val result = a.isNullOrInstanceOfType<TestType>()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Instance that is the subtype passes.
                val a = TestSubType()
                val result = a isNullOrInstanceOfType TestType::class.java
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Instance that is the subtype passes with type param.
                val a = TestSubType()
                val result = a.isNullOrInstanceOfType<TestType>()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Instance that is not the type fails.
                val a = Any()
                val result = a isNullOrInstanceOfType TestType::class.java
                assertTrue(result.isFailure)
            },
            {
                // Instance that is not the type fails with type param.
                val a = Any()
                val result = a.isNullOrInstanceOfType<TestType>()
                assertTrue(result.isFailure)
            },
            {
                // null passes.
                val a = null
                val result = a isNullOrInstanceOfType TestType::class.java
                assertTrue(result.isSuccess)
                assertNull(result.getOrThrow())
            },
            {
                // null passes with type param.
                val a = null
                val result = a.isNullOrInstanceOfType<TestType>()
                assertTrue(result.isSuccess)
                assertNull(result.getOrThrow())
            },
            {
                // null passes when stored as type.
                val a: TestType? = null
                val result = a isNullOrInstanceOfType TestType::class.java
                assertTrue(result.isSuccess)
                assertNull(result.getOrThrow())
            },
            {
                // null passes when stored as type with type param.
                val a: TestType? = null
                val result = a.isNullOrInstanceOfType<TestType>()
                assertTrue(result.isSuccess)
                assertNull(result.getOrThrow())
            },
        )
    }

    @Test
    fun isEmptyString() {
        tryAll(
            {
                // Empty string is empty
                val a = ""
                val result = a.isEmptyString()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Blank string is not empty
                val a = " "
                val result = a.isEmptyString()
                assertTrue(result.isFailure)
            },
            {
                // Non-empty string is not empty
                val a = "Hello"
                val result = a.isEmptyString()
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun isNullOrEmptyString() {
        tryAll(
            {
                // Null is null
                val result = null.isNullOrEmptyString()
                assertTrue(result.isSuccess)
                assertNull(result.getOrThrow())
            },
            {
                // Empty string is empty
                val a = ""
                val result = a.isNullOrEmptyString()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Blank string is not empty
                val a = " "
                val result = a.isNullOrEmptyString()
                assertTrue(result.isFailure)
            },
            {
                // Non-empty string is not empty
                val a = "Hello"
                val result = a.isNullOrEmptyString()
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun isNotEmptyString() {
        tryAll(
            {
                // Empty string is not not empty
                val a = ""
                val result = a.isNotEmptyString()
                assertTrue(result.isFailure)
            },
            {
                // Blank string is not empty
                val a = " "
                val result = a.isNotEmptyString()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Non-empty string is not empty
                val a = "Hello"
                val result = a.isNotEmptyString()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            }
        )
    }

    @Test
    fun isNotNullOrEmptyString() {
        tryAll(
            {
                // Null is not not null
                val result = null.isNotNullOrEmptyString()
                assertTrue(result.isFailure)
            },
            {
                // Empty string is not not empty
                val a = ""
                val result = a.isNotNullOrEmptyString()
                assertTrue(result.isFailure)
            },
            {
                // Blank string is not empty
                val a = " "
                val result = a.isNotNullOrEmptyString()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Non-empty string is not empty
                val a = "Hello"
                val result = a.isNotNullOrEmptyString()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            }
        )
    }

    @Test
    fun isBlankString() {
        tryAll(
            {
                // Empty string is blank
                val a = ""
                val result = a.isBlankString()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Blank string is blank
                val a = " "
                val result = a.isBlankString()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Non-empty string is not blank
                val a = "Hello"
                val result = a.isBlankString()
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun isNullOrBlankString() {
        tryAll(
            {
                // Null is null
                val result = null.isNullOrBlankString()
                assertTrue(result.isSuccess)
                assertNull(result.getOrThrow())
            },
            {
                // Empty string is blank
                val a = ""
                val result = a.isNullOrBlankString()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Blank string is blank
                val a = " "
                val result = a.isNullOrBlankString()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Non-empty string is not blank
                val a = "Hello"
                val result = a.isNullOrBlankString()
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun isNotBlankString() {
        tryAll(
            {
                // Empty string is not not blank
                val a = ""
                val result = a.isNotBlankString()
                assertTrue(result.isFailure)
            },
            {
                // Blank string is not not blank
                val a = " "
                val result = a.isNotBlankString()
                assertTrue(result.isFailure)
            },
            {
                // Non-empty string is not blank
                val a = "Hello"
                val result = a.isNotBlankString()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            }
        )
    }

    @Test
    fun isNotNullOrBlankString() {
        tryAll(
            {
                // Null is not not null
                val result = null.isNotNullOrBlankString()
                assertTrue(result.isFailure)
            },
            {
                // Empty string is not not blank
                val a = ""
                val result = a.isNotNullOrBlankString()
                assertTrue(result.isFailure)
            },
            {
                // Blank string is not not blank
                val a = " "
                val result = a.isNotNullOrBlankString()
                assertTrue(result.isFailure)
            },
            {
                // Non-empty string is not blank
                val a = "Hello"
                val result = a.isNotNullOrBlankString()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            }
        )
    }

    @Test
    fun containsSubstring() {
        tryAll(
            {
                // String with substring passes
                val a = "Hello There"
                val result = a containsSubstring "The"
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // String with substring with different case fails
                val a = "Hello There"
                val result = a containsSubstring "the"
                assertTrue(result.isFailure)
            },
            {
                // String without substring fails
                val a = "Hello There"
                val result = a containsSubstring "goodbye"
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun containsCaseInsensitiveSubstring() {
        tryAll(
            {
                // String with substring passes
                val a = "Hello There"
                val result = a containsCaseInsensitiveSubstring "The"
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // String with substring with different case passes
                val a = "Hello There"
                val result = a containsCaseInsensitiveSubstring "the"
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // String without substring fails
                val a = "Hello There"
                val result = a containsCaseInsensitiveSubstring "goodbye"
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun startsWithSubstring() {
        tryAll(
            {
                // String starting with substring passes
                val a = "Hello There"
                val result = a startsWithSubstring "Hello"
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {

                // String starting with substring with different case fails
                val a = "Hello There"
                val result = a startsWithSubstring "hello"
                assertTrue(result.isFailure)
            },
            {
                // String with substring in middle fails
                val a = "Hello There"
                val result = a startsWithSubstring "The"
                assertTrue(result.isFailure)
            },
            {
                // String without substring fails
                val a = "Hello There"
                val result = a startsWithSubstring "goodbye"
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun startsWithCaseInsensitiveSubstring() {
        tryAll(
            {
                // String starting with substring passes
                val a = "Hello There"
                val result = a startsWithCaseInsensitiveSubstring "Hello"
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {

                // String starting with substring with different case passes
                val a = "Hello There"
                val result = a startsWithCaseInsensitiveSubstring "hello"
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // String with substring in middle fails
                val a = "Hello There"
                val result = a startsWithCaseInsensitiveSubstring "The"
                assertTrue(result.isFailure)
            },
            {
                // String without substring fails
                val a = "Hello There"
                val result = a startsWithCaseInsensitiveSubstring "goodbye"
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun endsWithSubstring() {
        tryAll(
            {
                // String ending with substring passes
                val a = "Hello There"
                val result = a endsWithSubstring "There"
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {

                // String ending with substring with different case fails
                val a = "Hello There"
                val result = a endsWithSubstring "there"
                assertTrue(result.isFailure)
            },
            {
                // String with substring in middle fails
                val a = "Hello There"
                val result = a endsWithSubstring "The"
                assertTrue(result.isFailure)
            },
            {
                // String without substring fails
                val a = "Hello There"
                val result = a endsWithSubstring "goodbye"
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun endsWithCaseInsensitiveSubstring() {
        tryAll(
            {
                // String ending with substring passes
                val a = "Hello There"
                val result = a endsWithCaseInsensitiveSubstring "There"
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {

                // String ending with substring with different case passes
                val a = "Hello There"
                val result = a endsWithCaseInsensitiveSubstring "there"
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // String with substring in middle fails
                val a = "Hello There"
                val result = a endsWithCaseInsensitiveSubstring "The"
                assertTrue(result.isFailure)
            },
            {
                // String without substring fails
                val a = "Hello There"
                val result = a endsWithCaseInsensitiveSubstring "goodbye"
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun matches() {
        tryAll(
            {
                // Fails for a partial regex match
                val a = "Hello There"
                val result = a matches "Hello".toRegex()
                assertTrue(result.isFailure)
            },
            {
                // Passes when a whole regex matches
                val a = "Hello There"
                val result = a matches "Hello There".toRegex()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Fails when the regex doesn't match
                val a = "Hello There"
                val result = a matches "Red".toRegex()
                assertTrue(result.isFailure)
            }
        )
    }


    @Test
    fun containsMatchFor() {
        tryAll(
            {
                // Passes for a partial regex match at the beginning
                val a = "Hello There"
                val result = a containsMatchFor "Hello".toRegex()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Passes for a partial regex match in the middle
                val a = "Hello There"
                val result = a containsMatchFor "The".toRegex()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Passes when a whole regex matches
                val a = "Hello There"
                val result = a containsMatchFor "Hello There".toRegex()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Fails when the regex doesn't match
                val a = "Hello There"
                val result = a containsMatchFor "Red".toRegex()
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun isInRange() {
        tryAll(
            {
                // Value at start of range is in range.
                val a = 10
                val result = a.isInRange(10, 20)
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Value at start of range is in range with range.
                val a = 10
                val result = a isInRange 10..20
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Value at end of range is in range.
                val a = 20
                val result = a.isInRange(10, 20)
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Value at end of range is in range with range.
                val a = 20
                val result = a isInRange 10..20
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Value in middle of range is in range.
                val a = 15
                val result = a.isInRange(10, 20)
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Value in middle of range is in range with range.
                val a = 15
                val result = a isInRange 10..20
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Value before range is not in range.
                val a = 5
                val result = a.isInRange(10, 20)
                assertTrue(result.isFailure)
            },
            {
                // Value before range is not in range with range.
                val a = 5
                val result = a isInRange 10..20
                assertTrue(result.isFailure)
            },
            {
                // Value after range is not in range.
                val a = 25
                val result = a.isInRange(10, 20)
                assertTrue(result.isFailure)
            },
            {
                // Value after range is not in range with range.
                val a = 25
                val result = a isInRange 10..20
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun isEmptyCollection() {
        tryAll(
            {
                // Empty collection is empty
                val a = listOf<Int>()
                val result = a.isEmptyCollection()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Non-empty collection is not empty
                val a = listOf(1, 2, 3)
                val result = a.isEmptyCollection()
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun isNotEmptyCollection() {
        tryAll(
            {
                // Empty collection is not not empty
                val a = listOf<Int>()
                val result = a.isNotEmptyCollection()
                assertTrue(result.isFailure)
            },
            {
                // Non-empty collection is not empty
                val a = listOf(1, 2, 3)
                val result = a.isNotEmptyCollection()
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            }
        )
    }

    @Test
    fun hasSizeOf() {
        tryAll(
            {
                // Collection with expected size passes
                val a = listOf(1, 2, 3)
                val result = a hasSizeOf 3
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Collection with different size fails
                val a = listOf(1, 2)
                val result = a hasSizeOf 3
                assertTrue(result.isFailure)
            },
        )
    }

    @Test
    fun hasSizeOfAtLeast() {
        tryAll(
            {
                // Collection with same size passes
                val a = listOf(1, 2, 3)
                val result = a hasSizeOfAtLeast 3
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Collection with smaller size fails
                val a = listOf(1, 2)
                val result = a hasSizeOfAtLeast 3
                assertTrue(result.isFailure)
            },
            {
                // Collection with larger size passes
                val a = listOf(1, 2, 3, 4)
                val result = a hasSizeOfAtLeast 3
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            }
        )
    }

    @Test
    fun hasSizeOfAtMost() {
        tryAll(
            {
                // Collection with same size passes
                val a = listOf(1, 2, 3)
                val result = a hasSizeOfAtMost 3
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Collection with smaller size passes
                val a = listOf(1, 2)
                val result = a hasSizeOfAtMost 3
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Collection with larger size fails
                val a = listOf(1, 2, 3, 4)
                val result = a hasSizeOfAtMost 3
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun containsAllOf() {
        tryAll(
            {
                // Collection with all elements passes
                val a = listOf(1, 2, 3)
                val result = a containsAllOf listOf(1, 2)
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Collection with all elements with varargs passes
                val a = listOf(1, 2, 3)
                val result = a.containsAllOf(1, 2)
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Collection missing elements fails
                val a = listOf(1, 2, 3)
                val result = a containsAllOf listOf(1, 4)
                assertTrue(result.isFailure)
            },
            {
                // Collection missing elements with varargs fails
                val a = listOf(1, 2, 3)
                val result = a.containsAllOf(1, 4)
                assertTrue(result.isFailure)
            }
        )
    }

    @Test
    fun containsAnyOf() {
        tryAll(
            {
                // Collection with one of the elements passes
                val a = listOf(1, 2, 3)
                val result = a containsAnyOf listOf(1, 4)
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Collection with one of the elements with varargs passes
                val a = listOf(1, 2, 3)
                val result = a.containsAnyOf(1, 4)
                assertTrue(result.isSuccess)
                assertSame(a, result.getOrThrow())
            },
            {
                // Collection missing all elements fails
                val a = listOf(1, 2, 3)
                val result = a containsAnyOf listOf(4, 5)
                assertTrue(result.isFailure)
            },
            {
                // Collection missing all elements with varargs fails
                val a = listOf(1, 2, 3)
                val result = a.containsAnyOf(4, 5)
                assertTrue(result.isFailure)
            }
        )
    }
}
