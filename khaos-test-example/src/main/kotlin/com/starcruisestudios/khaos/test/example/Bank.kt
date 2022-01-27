/*
 * Copyright (c) 2022 StarCruiseStudios, LLC. All rights reserved.
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package com.starcruisestudios.khaos.test.example

class Bank {
    private val accounts = mutableMapOf<Customer, AccountSummary>()
    fun createAccount(customer: Customer, initialBalance: Double = 0.0): AccountSummary {
        check(!accounts.containsKey(customer)) {
            "Customer ${customer.name} already has a bank account."
        }

        val account = AccountSummary(initialBalance)
        accounts[customer] = account

        return account
    }

    fun getAccountSummary(customer: Customer): AccountSummary {
        return accounts.getValue(customer)
    }

    fun deposit(customer: Customer, amount: Double): AccountSummary {
        val account = accounts.getValue(customer)
        val updatedAccount = account.copy(balance = account.balance + amount)
        accounts[customer] = updatedAccount
        return updatedAccount
    }

    fun withdraw(customer: Customer, amount: Double): AccountSummary {
        require(amount >= 0) { "Cannot withdraw negative amount: $amount" }

        val account = accounts.getValue(customer)
        check(account.balance >= amount) { "Insufficient funds in account." }

        val updatedAccount = account.copy(balance = account.balance - amount)
        accounts[customer] = updatedAccount
        return updatedAccount
    }
}

