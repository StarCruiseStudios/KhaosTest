/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.api

import com.starcruisestudios.khaos.lang.withEach
import com.starcruisestudios.khaos.test.exceptions.TestException
import com.starcruisestudios.khaos.test.verification.Verify
import com.starcruisestudios.khaos.validate.doesThrow
import com.starcruisestudios.khaos.validate.isEqualTo
import com.starcruisestudios.khaos.validate.isInstanceOfType
import com.starcruisestudios.khaos.validate.passes
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class KhaosTestDslTest {
    @Test
    fun khaosTestDslCompiles() {
        class TestSpecification : KhaosSpecification {
            val `test feature` = Feature {
                SetUpFeature {
                    Given("Do some feature set up action.") { }
                }

                CleanUpFeature {
                    Then("Do some feature clean up action") { }
                }

                SetUpEachScenario {
                    Given("Do some scenario set up action.") { }
                }

                CleanUpEachScenario {
                    Then("Do some scenario clean up action") { }
                }

                Scenario("Scenario 1") {
                    Info("The test is starting")
                    Given("Some environment value.")
                    val a = Given("Some test value.") { 10 }

                    When("Some side effect occurs.")
                    val result = When("Some action is taken.") { a + 1 }

                    val action = DeferredWhen("Some action will be executed.") { throw TestException() }

                    val thrownException = Then("The deferred action throws an exception") {
                        Verify.that(action.doesThrow<TestException>())
                    }
                    Then("The exception is verified") {
                        Verify.that(thrownException.isInstanceOfType<TestException>())
                    }

                    val verifiedA = Then("The expected result was returned", 11) { expected ->
                        Verify.that(result isEqualTo expected)
                    }
                    Then("The verified value is used") {
                        Verify.that(verifiedA passes { it > a })
                    }

                    Then("No other exceptions ocurred")
                }

                data class Input(val value: Int, val expected: Int)
                withEach(
                    Input(2, 3),
                    Input(3, 4),
                    Input(4, 5)
                ) {
                    Scenario("Scenario $value") {
                        val a = Given("A parameterized test value") { value }
                        val result = When("Some action is taken.") { a + 1 }

                        Then("The expected result was returned", expected) { expected ->
                            Verify.that(result isEqualTo expected)
                        }
                    }
                }
            }
        }

        val specification = TestSpecification()
        assertNotNull(specification.testLogger)
    }
}
