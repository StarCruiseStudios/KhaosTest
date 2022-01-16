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
}
