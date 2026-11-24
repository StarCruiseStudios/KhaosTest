/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5

import com.starcruisestudios.khaos.test.api.Feature
import com.starcruisestudios.khaos.test.api.KhaosSpecification
import com.starcruisestudios.khaos.test.junit5.engine.KhaosTestEngine
import com.starcruisestudios.khaos.test.validate.isTrue
import com.starcruisestudios.khaos.test.verification.Verify
import org.junit.jupiter.api.Test
import org.junit.platform.engine.DiscoverySelector
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit
import org.junit.platform.testkit.engine.Events

/** Specs discovered by the selector tests below. Top level so they can be scanned for. */
class SelectorSpecOne : KhaosSpecification() {
    val `Feature A` = Feature {
        Scenario("Scenario A1") { Then("passes") { Verify.that(true.isTrue()) } }
        Scenario("Scenario A2") { Then("passes") { Verify.that(true.isTrue()) } }
    }
    val `Feature B` = Feature {
        Scenario("Scenario B1") { Then("passes") { Verify.that(true.isTrue()) } }
    }
}

class SelectorSpecTwo : KhaosSpecification() {
    val `Feature C` = Feature {
        Scenario("Scenario C1") { Then("passes") { Verify.that(true.isTrue()) } }
    }
}

class SelectorDiscoveryTests {
    private val engineId = UniqueId.forEngine(KhaosTestEngine.ID)
    private val specOneId = engineId.append("specification", SelectorSpecOne::class.java.name)

    @Test
    fun `package selectors discover specifications in the package`() {
        val results = execute(DiscoverySelectors.selectPackage(SelectorSpecOne::class.java.packageName))

        // 3 scenarios in SelectorSpecOne, 1 in SelectorSpecTwo.
        results.assertSucceeded(4)
    }

    @Test
    fun `classpath root selectors discover specifications`() {
        val root = SelectorSpecOne::class.java.protectionDomain.codeSource.location.toURI()
        val results = execute(DiscoverySelectors.selectClasspathRoots(setOf(java.nio.file.Paths.get(root))).first())

        // Includes at least the scenarios from the specs declared above.
        results.assertSucceededAtLeast(4)
    }

    @Test
    fun `method selectors run the containing specification`() {
        val results = execute(DiscoverySelectors.selectMethod(SelectorSpecTwo::class.java, "getFeature C"))

        results.assertSucceeded(1)
    }

    @Test
    fun `unique id selector for a specification runs only that specification`() {
        val results = execute(DiscoverySelectors.selectUniqueId(specOneId))

        results.assertSucceeded(3)
    }

    @Test
    fun `unique id selector for a feature runs only that feature`() {
        val featureId = specOneId.append("feature", "Feature A")
        val results = execute(DiscoverySelectors.selectUniqueId(featureId))

        results.assertSucceeded(2)
    }

    @Test
    fun `unique id selector for a scenario runs only that scenario`() {
        val scenarioId = specOneId.append("feature", "Feature A").append("scenario", "Scenario A2")
        val results = execute(DiscoverySelectors.selectUniqueId(scenarioId))

        results.assertSucceeded(1)
    }

    private fun execute(selector: DiscoverySelector): Events {
        return EngineTestKit.engine(KhaosTestEngine.ID)
            .selectors(selector)
            .execute()
            .testEvents()
    }

    private fun Events.assertSucceeded(count: Long) {
        assertStatistics { it.started(count).succeeded(count).failed(0) }
    }

    private fun Events.assertSucceededAtLeast(count: Long) {
        val succeeded = succeeded().count()
        check(succeeded >= count) { "Expected at least $count succeeded tests but was $succeeded." }
        assertStatistics { it.failed(0) }
    }
}
