package org.craftedsw.bank.domain

import java.io.PrintStream
import java.util.LinkedList

class Statement {

    private val statementLines: MutableList<StatementLine> = LinkedList()

    fun addLineContaining(transaction: Transaction, currentBalance: Amount) {
        statementLines.add(TOP_OF_THE_LIST, StatementLine(transaction, currentBalance))
    }

    fun printTo(printer: PrintStream) {
        printer.println(STATEMENT_HEADER)
        printStatementLines(printer)
    }

    private fun printStatementLines(printer: PrintStream) {
        for (statementLine in statementLines) {
            statementLine.printTo(printer)
        }
    }

    companion object {
        private const val TOP_OF_THE_LIST = 0
        const val STATEMENT_HEADER = "date       | credit   | debit    | balance"
    }
}
