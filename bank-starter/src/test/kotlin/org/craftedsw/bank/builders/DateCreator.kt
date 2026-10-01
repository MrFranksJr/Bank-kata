package org.craftedsw.bank.builders

import java.text.SimpleDateFormat
import java.util.Date

object DateCreator {
    fun date(dateString: String): Date {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        return sdf.parse(dateString) ?: error("Unable to parse date: $dateString")
    }
}
