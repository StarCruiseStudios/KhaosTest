/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.api

/**
 * Defines common [KhaosFormatProvider]s.
 */
object KhaosFormatProviders {
    /**
     * A [KhaosFormatProvider] that outputs markdown text.
     */
    val MARKDOWN = object : KhaosFormatProvider {
        override fun buildWriter(logAdapter: KhaosLogAdapter): KhaosWriter {
            return KhaosMarkdownWriter(logAdapter)
        }
    }

    /**
     * A [KhaosFormatProvider] that outputs raw text.
     */
    val TEXT = object : KhaosFormatProvider {
        override fun buildWriter(logAdapter: KhaosLogAdapter): KhaosWriter {
            return KhaosTextWriter(logAdapter)
        }
    }
}
