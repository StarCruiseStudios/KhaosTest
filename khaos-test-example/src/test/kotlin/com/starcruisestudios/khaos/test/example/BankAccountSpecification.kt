/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.example

import com.starcruisestudios.khaos.lang.withEach
import com.starcruisestudios.khaos.test.api.Feature
import com.starcruisestudios.khaos.test.api.KhaosSpecification
import com.starcruisestudios.khaos.test.api.ScenarioBuilder
import com.starcruisestudios.khaos.test.verification.Verify
import com.starcruisestudios.khaos.validate.doesNotThrow
import com.starcruisestudios.khaos.validate.doesThrow
import com.starcruisestudios.khaos.validate.isEqualTo
import java.lang.IllegalArgumentException

private data class BankCustomer(val bank: Bank, val customer: Customer)
private fun ScenarioBuilder.givenACustomerWithBankAccount(
    customerName: String,
    initialBalance: Double = 0.0
): BankCustomer {
    val bank = Given("A bank") { Bank() }
    val customer = Given("A customer") { Customer(customerName) }
    Given("The customer has a bank account at the bank") {
        bank.createAccount(customer, initialBalance)
    }

    return BankCustomer(bank, customer)
}

object BankAccountSpecification : KhaosSpecification {
    val `Bank account creation` = Feature {
        Scenario("A new bank account is created with an initial balance") {
            val bank = Given("A bank") { Bank() }
            val customer = Given("A customer") { Customer("Jim") }

            val accountSummary = When("The customer creates an account at the bank") {
                bank.createAccount(customer)
            }

            Then("The account has the correct initial balance", 0.0) { expected ->
                Verify.that(accountSummary.balance isEqualTo expected)
            }
        }

        Scenario("A customer can only have one account at a bank") {
            val (bank, customer) = givenACustomerWithBankAccount("Jim")

            val action = DeferredWhen("The customer creates another account at the bank.") {
                bank.createAccount(customer)
            }

            Then("The account creation fails") {
                Verify.that(action.doesThrow<IllegalStateException>())
            }
        }
    }

    val `Bank account deposits and withdrawals` = Feature {
        Scenario("Money is deposited in a bank account") {
            val (bank, customer) = givenACustomerWithBankAccount("Jim")
            val money = Given("The customer has some money") { 5.0 }

            When("The customer deposits the money") {
                bank.deposit(customer, money)
            }

            Then("The account has the expected balance", money) { expected ->
                Verify.that(bank.getAccountSummary(customer).balance isEqualTo expected)
            }
        }

        data class Input(
            val scenarioName: String,
            val initialBalance: Double,
            val withdrawalAmount: Double,
            val expectedException: Class<out Throwable>? = null)
        withEach(
            Input("Money is withdrawn from a bank account", 100.0, 5.0),
            Input("Amount withdrawn must be positive", 100.0, -5.0, IllegalArgumentException::class.java),
            Input("A bank account cannot be overdrawn", 5.0, 100.0, IllegalStateException::class.java)
        ) {
            Scenario(scenarioName) {
                val (bank, customer) = givenACustomerWithBankAccount("Jim", initialBalance)
                val money = Given("The customer needs some money") { withdrawalAmount }

                val action = DeferredWhen("The customer withdraws the money") {
                    bank.withdraw(customer, money)
                }

                if (expectedException == null) {
                    Then("The withdrawal succeeds") {
                        Verify.that(action.doesNotThrow())
                    }

                    Then("The account has the expected balance", initialBalance - money) { expected ->
                        Verify.that(bank.getAccountSummary(customer).balance isEqualTo expected)
                    }
                } else {
                    Then("The withdrawal fails") {
                        Verify.that(action doesThrow expectedException)
                    }
                }
            }
        }
    }
}
