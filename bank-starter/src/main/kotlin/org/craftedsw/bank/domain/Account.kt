package org.craftedsw.bank.domain

import java.io.PrintStream
import java.util.Date
import org.craftedsw.bank.domain.Amount.Companion.amountOf

class Account(
    private val statement: Statement
) {

    private var balance: Amount = amountOf(0)

    fun deposit(value: Amount, date: Date) {
        recordTransaction(value, date)
    }

    fun withdrawal(value: Amount, date: Date) {
        recordTransaction(value.negative(), date)
    }

    fun printStatement(printer: PrintStream) {
        statement.printTo(printer)
    }

    private fun recordTransaction(value: Amount, date: Date) {
        val transaction = Transaction(value, date)
        val balanceAfterTransaction = transaction.balanceAfterTransaction(balance)
        balance = balanceAfterTransaction
        statement.addLineContaining(transaction, balanceAfterTransaction)
    }
}
