# Khaos Test

Khaos Test is a Kotlin test framework and toolkit for improving
self-documentation, readability, and debugging of tests.

Khaos Test allows a developer to intuitively structure tests as individual
scenarios that make up features, and features are grouped into a specification.

```kotlin
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
```

Scenarios are self-documented as a series of given/when/then steps with human
readable verification methods and all test values are logged for easy debugging
of failed tests.

```markdown
## SPECIFICATION: com.starcruisestudios.khaos.test.example.BankAccountSpecification

----------------------------------------

### FEATURE: Bank account deposits and withdrawals

--------------------

#### SCENARIO: Money is deposited in a bank account

**Given:**
* A bank : `com.starcruisestudios.khaos.test.example.Bank@107f4980` -> **PASSED**
* A customer : `Customer(name=Jim)` -> **PASSED**
* The customer has a bank account at the bank : `AccountSummary(balance=0.0)` -> **PASSED**
* The customer has some money : `5.0` -> **PASSED**

**When:**
* The customer deposits the money : `AccountSummary(balance=5.0)` -> **PASSED**

**Then:**
* The account has the expected balance (`5.0`) -> **PASSED**

> Scenario Result: **PASSED**


#### SCENARIO: Money is withdrawn from a bank account

**Given:**
* A bank : `com.starcruisestudios.khaos.test.example.Bank@472698d` -> **PASSED**
* A customer : `Customer(name=Jim)` -> **PASSED**
* The customer has a bank account at the bank : `AccountSummary(balance=100.0)` -> **PASSED**
* The customer needs some money : `5.0` -> **PASSED**

**When:**
* The customer withdraws the money -> **DEFERRED**

**Then:**
* The withdrawal succeeds : `AccountSummary(balance=95.0)` -> **PASSED**
* The account has the expected balance (`95.0`) : `95.0` -> **PASSED**

> Scenario Result: **PASSED**


#### SCENARIO: Amount withdrawn must be positive

**Given:**
* A bank : `com.starcruisestudios.khaos.test.example.Bank@40712ee9` -> **PASSED**
* A customer : `Customer(name=Jim)` -> **PASSED**
* The customer has a bank account at the bank : `AccountSummary(balance=100.0)` -> **PASSED**
* The customer needs some money : `-5.0` -> **PASSED**

**When:**
* The customer withdraws the money -> **DEFERRED**

**Then:**
* The withdrawal fails : `java.lang.IllegalArgumentException: Cannot withdraw negative amount: -5.0` -> **PASSED**

> Scenario Result: **PASSED**


#### SCENARIO: A bank account cannot be overdrawn

**Given:**
* A bank : `com.starcruisestudios.khaos.test.example.Bank@39fa8ad2` -> **PASSED**
* A customer : `Customer(name=Jim)` -> **PASSED**
* The customer has a bank account at the bank : `AccountSummary(balance=5.0)` -> **PASSED**
* The customer needs some money : `100.0` -> **PASSED**

**When:**
* The customer withdraws the money -> **DEFERRED**

**Then:**
* The withdrawal fails : `java.lang.IllegalStateException: Insufficient funds in account.` -> **PASSED**

> Scenario Result: **PASSED**

### FEATURE: Bank account creation

--------------------

#### SCENARIO: A new bank account is created with an initial balance

**Given:**
* A bank : `com.starcruisestudios.khaos.test.example.Bank@7942a854` -> **PASSED**
* A customer : `Customer(name=Jim)` -> **PASSED**

**When:**
* The customer creates an account at the bank : `AccountSummary(balance=0.0)` -> **PASSED**

**Then:**
* The account has the correct initial balance (`0.0`) -> **PASSED**

> Scenario Result: **PASSED**


#### SCENARIO: A customer can only have one account at a bank

**Given:**
* A bank : `com.starcruisestudios.khaos.test.example.Bank@33a3c44a` -> **PASSED**
* A customer : `Customer(name=Jim)` -> **PASSED**
* The customer has a bank account at the bank : `AccountSummary(balance=0.0)` -> **PASSED**

**When:**
* The customer creates another account at the bank. -> **DEFERRED**

**Then:**
* The account creation fails -> **PASSED**

> Scenario Result: **PASSED**
```