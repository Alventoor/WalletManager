package com.tutamail.julienm99.walletmanager.core.util

import java.lang.NumberFormatException
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Parses the string as a [Double] number.
 *
 * @return the result or [NumberFormatException] if the string is not a valid
 * representation of a number.
 */
fun String.toDoubleResult(): Result<Double> = runCatching {
    this.toDouble()
}

/**
 * Parses the string as a [BigDecimal] number.
 *
 * @param commaAsDot Decide if comma must be considered as dot.
 * Useful if you want to manipulate some currency like Euro where comma is
 * usually used as decimal separator.
 *
 * @return the result or [NumberFormatException] if the string is not a valid
 * representation of a number.
 */
fun String.toBigDecimalResult(commaAsDot: Boolean = true): Result<BigDecimal> = runCatching {
    when (commaAsDot) {
        true -> this.replace(',', '.').toBigDecimal()
        false -> this.toBigDecimal()
    }
}

/**
 * Convert a string in the format 'dd\MM\yyyy' to the Unix Epoch timestamp.
 *
 * @return the parsed timestamp or [DateTimeParseException] if the string is not in the
 * correct format.
 */
fun String.toTimestamp(): Result<Long> = runCatching {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    LocalDate.parse(this, dateFormatter).toTimestamp()
}