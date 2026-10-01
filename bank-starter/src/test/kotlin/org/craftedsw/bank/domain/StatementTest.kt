package org.craftedsw.bank.domain

import io.mockk.mockk
import io.mockk.verifyOrder
import java.io.PrintStream
import org.craftedsw.bank.builders.DateCreator.date
import org.craftedsw.bank.builders.TransactionBuilder.Companion.aTransaction
import org.craftedsw.bank.domain.Amount.Companion.amountOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StatementTest {

    private val printer: PrintStream = mockk(relaxed = true)
    private lateinit var statement: Statement

    @BeforeEach
    fun setUp() {
        statement = Statement()
    }

    @Test
    fun `should print statement header`() {
        statement.printTo(printer)

        verifyOrder {
            printer.println(Statement.STATEMENT_HEADER)
        }
    }

    @Test
    fun `should print deposit`() {
        statement.addLineContaining(
            aTransaction()
                .with(amountOf(1000))
                .with(date("10/01/2012")).build(),
            amountOf(1000)
        )

        statement.printTo(printer)

        verifyOrder {
            printer.println(Statement.STATEMENT_HEADER)
            printer.println("10/01/2012 | 1000.00  |          | 1000.00")
        }
    }

    @Test
    fun `should print withdrawal`() {
        statement.addLineContaining(
            aTransaction()
                .with(amountOf(-1000))
                .with(date("10/01/2012")).build(),
            amountOf(-1000)
        )

        statement.printTo(printer)

        verifyOrder {
            printer.println(Statement.STATEMENT_HEADER)
            printer.println("10/01/2012 |          | 1000.00  | -1000.00")
        }
    }

    @Test
    fun `should print two deposits in reverse order`() {
        statement.addLineContaining(
            aTransaction()
                .with(amountOf(1000))
                .with(date("10/01/2012")).build(),
            amountOf(1000)
        )
        statement.addLineContaining(
            aTransaction()
                .with(amountOf(2000))
                .with(date("13/01/2012")).build(),
            amountOf(3000)
        )

        statement.printTo(printer)

        verifyOrder {
            printer.println(Statement.STATEMENT_HEADER)
            printer.println("13/01/2012 | 2000.00  |          | 3000.00")
            printer.println("10/01/2012 | 1000.00  |          | 1000.00")
        }
    }
}
