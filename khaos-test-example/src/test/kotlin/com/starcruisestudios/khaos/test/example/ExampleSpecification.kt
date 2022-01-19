/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.example

import com.starcruisestudios.khaos.test.api.Feature
import com.starcruisestudios.khaos.test.api.KhaosSpecification
import com.starcruisestudios.khaos.test.exceptions.TestException
import com.starcruisestudios.khaos.test.verification.Verify
import com.starcruisestudios.khaos.validate.doesThrow
import com.starcruisestudios.khaos.validate.isEqualTo
import com.starcruisestudios.khaos.validate.isNotEqualTo

object ExampleSpecification : KhaosSpecification {
    val `Tagged Feature` = Feature("Tag") {
        Tagged("ScenarioTag")
            .Scenario("A tagged scenario") {
                Given("A tagged scenario")
            }
    }

    val `Test Feature` = Feature {
        SetUpFeature {
            Given("Do this once before") { }
        }

        CleanUpFeature {
            Then("Do this once after") { }
        }

        SetUpEachScenario {
            Given("Some setup action is taken") { }
        }

        CleanUpEachScenario {
            Then("Validate some clean up") {

            }
        }

        Tagged(
            "This is a tag",
            "and another one",
            "foo"
        ).Scenario("A Tagged Test Scenario") {
            Given("A tagged Scenario")
        }

        Scenario("A Test Scenario") {

            Given("Some Value") { 10 }
            val action = When("Some When") { { throw TestException("Boo!") } }

            Then("Should throw") {
                Verify.that(action doesThrow TestException::class.java)
            }
        } CleanUp {
            Then("Some cleanup")
        }

        Scenario("Another Test Scenario") {
            val a = Given("Some value") { 10 }
            val result = When("Some when") { a + 10 }
            Then("Some then", 20) { expected ->
                Verify.that(result isEqualTo expected)
            }
        }

        (0..10).forEach { index ->
            Scenario("Parameterized Scenario $index") {
                if (index == 2) {
                    Pending()
                }

                Given("An index") { index }
                val result = When("Adding one to the index") { index + 1 }
                Then("The result has changed") {
                    Verify.that(result isNotEqualTo index)
                }
            }
        }
    }
}
