package org.craftedsw.bank.domain

import io.mockk.mockk
import io.mockk.verify
import java.io.PrintStream
import org.assertj.core.api.Assertions.assertThat
import org.craftedsw.bank.builders.DateCreator.date
import org.craftedsw.bank.domain.Amount.Companion.amountOf
import org.junit.jupiter.api.Test

class TransactionTest {

    private val printer: PrintStream = mockk(relaxed = true)

    @Test
    fun `should print credit transaction`() {
        val transaction = Transaction(amountOf(1000), date("10/01/2012"))

        transaction.printTo(printer, amountOf(1000))

        verify { printer.println("10/01/2012 | 1000.00  |          | 1000.00") }
    }

    @Test
    fun `should print debit transaction`() {
        val transaction = Transaction(amountOf(-1000), date("10/01/2012"))

        transaction.printTo(printer, amountOf(-1000))

        verify { printer.println("10/01/2012 |          | 1000.00  | -1000.00") }
    }

    @Test
    fun `should calculate current balance after deposit`() {
        val transaction = Transaction(amountOf(1000), date("10/01/2012"))

        val currentValue = transaction.balanceAfterTransaction(amountOf(100))

        assertThat(currentValue).isEqualTo(amountOf(1100))
    }

    @Test
    fun `should calculate current balance after withdrawal`() {
        val transaction = Transaction(amountOf(-1000), date("10/01/2012"))

        val currentValue = transaction.balanceAfterTransaction(amountOf(100))

        assertThat(currentValue).isEqualTo(amountOf(-900))
    }

    @Test
    fun `should be equal to other transaction with same value and date`() {
        val depositDate = date("10/01/2012")
        val depositOfOneHundred = Transaction(amountOf(1000), depositDate)
        val anotherDepositOfOneHundred = Transaction(amountOf(1000), depositDate)

        assertThat(depositOfOneHundred).isEqualTo(anotherDepositOfOneHundred)
    }
}
