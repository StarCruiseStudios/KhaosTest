/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.descriptors

import com.starcruisestudios.khaos.test.api.KhaosLogAdapter

/**
 * A [KhaosLogContext] that stores messages in memory until it is flushed.
 *
 * @param logAdapter the log adapter messages will be flushed to.
 */
internal class BufferedLogContext(private val logAdapter: KhaosLogAdapter) : KhaosLogContext {
    private val messages = mutableListOf<Message>()

    override fun info(message: String) {
        messages.add(InfoMessage(message))
    }

    override fun warn(message: String) {
        messages.add(WarnMessage(message))
    }

    override fun error(message: String) {
        messages.add(ErrorMessage(message))
    }

    override fun getChild(logAdapter: KhaosLogAdapter) : KhaosLogContext {
        val child = BufferedLogContext(logAdapter)
        val message = ChildMessage(child)
        messages.add(message)
        return child
    }

    override fun flush() {
        messages.forEach {
            it.writeTo(logAdapter)
        }
        messages.clear()
    }

    private interface Message {
        fun writeTo(logAdapter: KhaosLogAdapter)
    }

    private class InfoMessage(val message: String) : Message {
        override fun writeTo(logAdapter: KhaosLogAdapter) {
            logAdapter.info(message)
        }
    }

    private class WarnMessage(val message: String) : Message {
        override fun writeTo(logAdapter: KhaosLogAdapter) {
            logAdapter.warn(message)
        }
    }

    private class ErrorMessage(val message: String) : Message {
        override fun writeTo(logAdapter: KhaosLogAdapter) {
            logAdapter.error(message)
        }
    }

    private class ChildMessage(val child: KhaosLogContext) : Message {
        override fun writeTo(logAdapter: KhaosLogAdapter) {
            child.flush()
        }
    }
}
