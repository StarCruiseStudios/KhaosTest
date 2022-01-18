/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5

import com.starcruisestudios.khaos.test.api.Feature
import com.starcruisestudios.khaos.test.api.KhaosSpecification
import com.starcruisestudios.khaos.test.junit5.engine.KhaosTestEngine
import com.starcruisestudios.khaos.test.verification.Verify
import com.starcruisestudios.khaos.validate.isTrue
import org.junit.jupiter.api.Test
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit

class DiscoveryAndExecutionTests {
    @Test
    fun `KhaosSpecification classes are discovered`() {
        // Given
        // * A test specification that implements KhaosSpecification.
        class TestSpecification : KhaosSpecification {
            val `Test Feature` = Feature {
                Scenario("A test Scenario") {
                    Then("The test succeeds") { Verify.that(true.isTrue()) }
                }
            }
        }

        // When
        val testResults = EngineTestKit.engine(KhaosTestEngine.ID)
            .selectors(DiscoverySelectors.selectClass(TestSpecification::class.java))
            .execute()
            .testEvents()

        // Then
        testResults.assertStatistics { stats ->
            stats.started(1)
                .succeeded(1)
                .failed(0)
                .skipped(0)
        }
    }

    @Test
    fun `Non-KhaosSpecification classes are not discovered`() {
        // Given
        // * A test specification that doesn't implement KhaosSpecification.
        class TestSpecification {
            val `Test Feature` = Feature {
                Scenario("A test Scenario") {
                    Then("The test succeeds") { Verify.that(true.isTrue()) }
                }
            }
        }

        // When
        val testResults = EngineTestKit.engine(KhaosTestEngine.ID)
            .selectors(DiscoverySelectors.selectClass(TestSpecification::class.java))
            .execute()
            .testEvents()

        // Then
        testResults.assertStatistics { stats ->
            stats
                .started(0)
                .succeeded(0)
                .failed(0)
                .skipped(0)
        }
    }
}
