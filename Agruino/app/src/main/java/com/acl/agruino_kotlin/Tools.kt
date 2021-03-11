package com.acl.agruino_kotlin

import com.acl.agruino_kotlin.models.ValueLog
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Tools {
    companion object {
        fun isNumeric(str: String?): Boolean {
            if (str == null) {
                return false
            }
            try {
                str.toDouble()
            } catch (e: NumberFormatException) {
                return false
            }
            return true
        }

        fun calcDayOfYear(): Int {
            val calendar = Calendar.getInstance()
            return calendar[Calendar.DAY_OF_YEAR]
        }

        fun today(): String? {
            val c = Calendar.getInstance()
            val year = c[Calendar.YEAR]
            val month = c[Calendar.MONTH]
            val day = c[Calendar.DAY_OF_MONTH]
            return String.format("%02d/%02d/%4d", day, month + 1, year)
        }
    }
}