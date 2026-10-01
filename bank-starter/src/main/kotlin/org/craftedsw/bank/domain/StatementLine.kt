package org.craftedsw.bank.domain

import java.io.PrintStream

class StatementLine(
    private val transaction: Transaction,
    private val currentBalance: Amount
) {

    fun printTo(printer: PrintStream) {
        transaction.printTo(printer, currentBalance)
    }
}
