/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5.engine

import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.TestDescriptor

/**
 * [TestDiscoveryEngine]s are used to define different means of test discovery
 * based on an [EngineDiscoveryRequest].
 */
interface TestDiscoveryEngine {
    /**
     * Discover tests described in the given [discoveryRequest] and adds them
     * as children to the provided [parent] test descriptor.
     */
    fun discover(discoveryRequest: EngineDiscoveryRequest, parent: TestDescriptor)
}
