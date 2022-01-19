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
import org.junit.platform.testkit.engine.Events

class DiscoveryTests {
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
        val testResults = executeTests(TestSpecification::class.java)

        // Then
        testResults.assertStats(numSucceeded = 1)
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
        val testResults = executeTests(TestSpecification::class.java)

        // Then
        testResults.assertStats()
    }

    @Test
    fun `Feature properties are discovered`() {
        // Given
        // * A test specification that contains Features
        class TestSpecification : KhaosSpecification {
            val `Test Feature` = Feature {
                Scenario("A test Scenario") {
                    Then("The test succeeds") { Verify.that(true.isTrue()) }
                }
            }

            val `Test Feature2` = Feature {
                Scenario("A second test Scenario") {
                    Then("The test succeeds") { Verify.that(true.isTrue()) }
                }
            }
        }

        // When
        val testResults = executeTests(TestSpecification::class.java)

        // Then
        testResults.assertStats(numSucceeded = 2)
    }

    @Test
    fun `Non-Feature properties are not discovered`() {
        // Given
        // * A test specification that contains Features
        class TestSpecification : KhaosSpecification {
            val `Test Feature` = Feature {
                Scenario("A test Scenario") {
                    Then("The test succeeds") { Verify.that(true.isTrue()) }
                }
            }

            val `Test Non-Feature`: Any = Feature {
                Scenario("A non-test Scenario") {
                    Then("The test succeeds") { Verify.that(true.isTrue()) }
                }
            }

            val `Not even close` = 10
        }

        // When
        val testResults = executeTests(TestSpecification::class.java)

        // Then
        testResults.assertStats(numSucceeded = 1)
    }

    private fun executeTests(testClass: Class<*>): Events {
        return EngineTestKit.engine(KhaosTestEngine.ID)
            .selectors(DiscoverySelectors.selectClass(testClass))
            .execute()
            .testEvents()
    }

    private fun Events.assertStats(numSucceeded: Long = 0, numFailed: Long = 0, numSkipped: Long = 0) {
        val numStarted = numSucceeded + numFailed
        assertStatistics { stats ->
            stats
                .started(numStarted)
                .succeeded(numSucceeded)
                .failed(numFailed)
                .skipped(numSkipped)
        }
    }
}
