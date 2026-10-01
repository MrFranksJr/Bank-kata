package org.craftedsw.bank.domain

import io.mockk.mockk
import io.mockk.verify
import java.io.PrintStream
import org.craftedsw.bank.builders.DateCreator.date
import org.craftedsw.bank.builders.TransactionBuilder.Companion.aTransaction
import org.craftedsw.bank.domain.Amount.Companion.amountOf
import org.junit.jupiter.api.Test

class StatementLineTest {

    private val printer: PrintStream = mockk(relaxed = true)

    @Test
    fun `should print itself`() {
        val statementLine = StatementLine(
            aTransaction()
                .with(amountOf(1000))
                .with(date("10/01/2012")).build(),
            amountOf(1000)
        )

        statementLine.printTo(printer)

        verify { printer.println("10/01/2012 | 1000.00  |          | 1000.00") }
    }

    @Test
    fun `should print withdrawal`() {
        val statementLine = StatementLine(
            aTransaction()
                .with(amountOf(-1000))
                .with(date("10/01/2012")).build(),
            amountOf(-1000)
        )

        statementLine.printTo(printer)

        verify { printer.println("10/01/2012 |          | 1000.00  | -1000.00") }
    }
}
