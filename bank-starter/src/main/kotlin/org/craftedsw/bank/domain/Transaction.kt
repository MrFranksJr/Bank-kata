package org.craftedsw.bank.domain

import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.Date
import org.craftedsw.bank.domain.Amount.Companion.amountOf

data class Transaction(
    val value: Amount,
    val date: Date
) {

    fun balanceAfterTransaction(currentBalance: Amount): Amount {
        return currentBalance.plus(value)
    }

    fun printTo(printer: PrintStream, currentBalance: Amount) {
        val builder = StringBuilder()
        addDateTo(builder)
        addValueTo(builder)
        addCurrentBalanceTo(builder, currentBalance)
        printer.println(builder.toString())
    }

    private fun addDateTo(builder: StringBuilder) {
        val sdf = SimpleDateFormat(DATE_FORMAT)
        builder.append(sdf.format(date)).append(" |")
    }

    private fun addValueTo(builder: StringBuilder) {
        if (value.isGreaterThan(amountOf(0))) {
            addCreditTo(builder)
        } else {
            addDebitTo(builder)
        }
    }

    private fun addCreditTo(builder: StringBuilder) {
        builder.append(valueToString())
            .append("|")
            .append(EMPTY_VALUE)
    }

    private fun addDebitTo(builder: StringBuilder) {
        builder.append(EMPTY_VALUE)
            .append("|")
            .append(valueToString())
    }

    private fun addCurrentBalanceTo(builder: StringBuilder, currentBalance: Amount) {
        builder.append("| ")
            .append(currentBalance.moneyRepresentation())
    }

    private fun valueToString(): String {
        val stringValue = " " + value.absoluteValue().moneyRepresentation()
        return stringValue.padEnd(10, ' ')
    }

    companion object {
        private const val DATE_FORMAT = "dd/MM/yyyy"
        private const val EMPTY_VALUE = "          "
    }
}
