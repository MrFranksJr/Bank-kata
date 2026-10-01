package org.craftedsw.bank.builders

import java.util.Date
import org.craftedsw.bank.domain.Amount
import org.craftedsw.bank.domain.Transaction

class TransactionBuilder {
    private var date: Date = Date()
    private var value: Amount = Amount(0)

    fun with(date: Date): TransactionBuilder {
        this.date = date
        return this
    }

    fun with(value: Amount): TransactionBuilder {
        this.value = value
        return this
    }

    fun build(): Transaction = Transaction(value, date)

    companion object {
        fun aTransaction(): TransactionBuilder = TransactionBuilder()
    }
}
