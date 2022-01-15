/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.lang

/**
 * Calls the specified function [block] with each of a set of [items] as its
 * receiver.
 */
inline fun <T> withEach(vararg items: T, block: T.() -> Unit) {
    items.forEach { it.block() }
}
