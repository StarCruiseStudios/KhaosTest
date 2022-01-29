/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.descriptors

import com.starcruisestudios.khaos.test.api.KhaosLogAdapter

/**
 * A [KhaosLogContext] that delegates messages to the underlying [logAdapter].
 */
internal class DelegatingLogContext(private val logAdapter: KhaosLogAdapter)
    : KhaosLogContext,
    KhaosLogAdapter by logAdapter
{
    override fun getChild(logAdapter: KhaosLogAdapter): KhaosLogContext {
        return DelegatingLogContext(logAdapter)
    }

    override fun flush() { }
}
