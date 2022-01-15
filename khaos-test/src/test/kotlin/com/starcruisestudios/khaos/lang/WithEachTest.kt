/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.lang

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class WithEachTest {
    @Test
    fun `an action is invoked on each item`() {
        // Given
        // * Multiple test items
        val item1 = TestObject()
        val item2 = TestObject()
        val item3 = TestObject()

        // When
        // * Each item is set
        withEach(item1, item2, item3) {
            isSet = true
        }

        // Then
        // * Each item was set
        assertTrue(item1.isSet)
        assertTrue(item2.isSet)
        assertTrue(item3.isSet)
    }

    @Test
    fun `an action is invoked on each item in a list`() {
        // Given
        // * Multiple test items
        val items = listOf(TestObject(), TestObject(), TestObject())

        // When
        // * Each item is set
        withEach(*items.toTypedArray()) {
            isSet = true
        }

        // Then
        // * Each item was set
        items.forEach {
            assertTrue(it.isSet)
        }
    }

    class TestObject {
        var isSet = false
    }
}
