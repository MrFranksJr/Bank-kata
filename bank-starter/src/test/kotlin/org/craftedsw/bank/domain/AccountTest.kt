package org.craftedsw.bank.domain

import io.mockk.mockk
import io.mockk.verify
import java.io.PrintStream
import org.craftedsw.bank.builders.DateCreator.date
import org.craftedsw.bank.builders.TransactionBuilder.Companion.aTransaction
import org.craftedsw.bank.domain.Amount.Companion.amountOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AccountTest {

    private val statement: Statement = mockk(relaxed = true)
    private lateinit var account: Account

    @BeforeEach
    fun setUp() {
        account = Account(statement)
    }

    @Test
    fun `should add deposit line to statement`() {
        val depositDate = date("10/01/2012")
        val depositAmount = amountOf(1000)

        account.deposit(depositAmount, depositDate)

        verify {
            statement.addLineContaining(
                aTransaction()
                    .with(depositDate)
                    .with(depositAmount).build(),
                depositAmount
            )
        }
    }

    @Test
    fun `should add withdraw line to statement`() {
        val withdrawalDate = date("12/01/2012")

        account.withdrawal(amountOf(500), withdrawalDate)

        verify {
            statement.addLineContaining(
                aTransaction()
                    .with(amountOf(-500))
                    .with(withdrawalDate).build(),
                amountOf(-500)
            )
        }
    }

    @Test
    fun `should print statement`() {
        val printer: PrintStream = System.out

        account.printStatement(printer)

        verify {
            statement.printTo(printer)
        }
    }
}
