/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.junit5

import com.starcruisestudios.khaos.test.api.Feature
import com.starcruisestudios.khaos.test.api.KhaosSpecification
import com.starcruisestudios.khaos.test.exceptions.TestException
import com.starcruisestudios.khaos.test.junit5.engine.KhaosTestEngine
import org.junit.jupiter.api.Test
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit
import org.junit.platform.testkit.engine.Events

class ExecutionTests {
    @Test
    fun `No exceptions results in success`() {
        // Given
        // * A test specification
        class TestSpecification : KhaosSpecification {
            val `Test Feature` = Feature {
                Scenario("A test Scenario") {
                    Then("The test succeeds")
                }
            }
        }

        // When
        val testResults = executeTests(TestSpecification::class.java)

        // Then
        testResults.assertStats(numSucceeded = 1)
    }

    @Test
    fun `Exception in given results in error`() {
        // Given
        // * A test specification
        class TestSpecification : KhaosSpecification  {
            val `Test Feature` = Feature {
                Scenario("A test Scenario") {
                    Given("An exception is thrown") { throw TestException("Exception in given") }
                }
            }
        }

        // When
        val testResults = executeTests(TestSpecification::class.java)

        // Then
        testResults.assertStats(numFailed = 1)
    }

    @Test
    fun `Exception in when results in error`() {
        // Given
        // * A test specification
        class TestSpecification : KhaosSpecification  {
            val `Test Feature` = Feature {
                Scenario("A test Scenario") {
                    When("An exception is thrown") { throw TestException("Exception in when") }
                }
            }
        }

        // When
        val testResults = executeTests(TestSpecification::class.java)

        // Then
        testResults.assertStats(numFailed = 1)
    }

    @Test
    fun `Exception in then results in failure`() {
        // Given
        // * A test specification
        class TestSpecification : KhaosSpecification  {
            val `Test Feature` = Feature {
                Scenario("A test Scenario") {
                    Then("An exception is thrown") { throw TestException("Exception in then") }
                }
            }

            val `Another Test Feature` = Feature {
                Scenario("A test Scenario") {
                    Then("An exception is thrown", true) {
                        throw TestException("Exception in then with expected")
                    }
                }
            }
        }

        // When
        val testResults = executeTests(TestSpecification::class.java)

        // Then
        testResults.assertStats(numFailed = 2)
    }

    @Test
    fun `Exception oustide of steps results in error`() {
        // Given
        // * A test specification
        class TestSpecification : KhaosSpecification  {
            val `Test Feature` = Feature {
                Scenario("A test Scenario") {
                    throw TestException("Exception outside of steps")
                }
            }
        }

        // When
        val testResults = executeTests(TestSpecification::class.java)

        // Then
        testResults.assertStats(numFailed = 1)
    }

    @Test
    fun `Exception in SetUpEachScenario results in error`() {
        // Given
        // * A test specification
        class TestSpecification : KhaosSpecification  {
            val `Test Feature` = Feature {
                SetUpEachScenario {
                    throw TestException("Exception in setup")
                }

                Scenario("A test Scenario") { }
            }
        }

        // When
        val testResults = executeTests(TestSpecification::class.java)

        // Then
        testResults.assertStats(numFailed = 1)
    }

    @Test
    fun `Exception in CleanUpEachScenario results in error`() {
        // Given
        // * A test specification
        class TestSpecification : KhaosSpecification  {
            val `Test Feature` = Feature {
                CleanUpEachScenario {
                    throw TestException("Exception in cleanup")
                }

                Scenario("A test Scenario") { }
            }
        }

        // When
        val testResults = executeTests(TestSpecification::class.java)

        // Then
        testResults.assertStats(numFailed = 1)
    }

    @Test
    fun `Exception in scenario CleanUp results in error`() {
        // Given
        // * A test specification
        class TestSpecification : KhaosSpecification  {
            val `Test Feature` = Feature {
                Scenario("A test Scenario") { }
                    .CleanUp {
                        throw TestException("Exception in cleanup")
                    }
            }
        }

        // When
        val testResults = executeTests(TestSpecification::class.java)

        // Then
        testResults.assertStats(numFailed = 1)
    }

    private fun executeTests(testClass: Class<*>): Events {
        return EngineTestKit.engine(KhaosTestEngine.ID)
            .selectors(DiscoverySelectors.selectClass(testClass))
            .execute()
            .testEvents()
    }

    private fun Events.assertStats(
        numSucceeded: Long = 0,
        numFailed: Long = 0,
        numSkipped: Long = 0
    ) {
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
