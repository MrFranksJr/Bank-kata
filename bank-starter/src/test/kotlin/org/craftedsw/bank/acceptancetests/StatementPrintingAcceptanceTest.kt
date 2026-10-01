package org.craftedsw.bank.acceptancetests

import io.mockk.mockk
import io.mockk.verifyOrder
import java.io.PrintStream
import org.craftedsw.bank.builders.DateCreator.date
import org.craftedsw.bank.domain.Account
import org.craftedsw.bank.domain.Amount.Companion.amountOf
import org.craftedsw.bank.domain.Statement
import org.junit.jupiter.api.Test

class StatementPrintingAcceptanceTest {

    @Test
    fun `should print statement containing all transactions in reverse chronological order with running balance`() {
        val printer: PrintStream = mockk(relaxed = true)
        val statement = Statement()
        val account = Account(statement)

        account.deposit(amountOf(1000), date("10/01/2012"))
        account.deposit(amountOf(2000), date("13/01/2012"))
        account.withdrawal(amountOf(500), date("14/01/2012"))

        account.printStatement(printer)

        verifyOrder {
            printer.println("date       | credit   | debit    | balance")
            printer.println("14/01/2012 |          | 500.00   | 2500.00")
            printer.println("13/01/2012 | 2000.00  |          | 3000.00")
            printer.println("10/01/2012 | 1000.00  |          | 1000.00")
        }
    }
}
