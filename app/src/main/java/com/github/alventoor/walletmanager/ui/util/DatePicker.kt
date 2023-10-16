package com.github.alventoor.walletmanager.ui.util

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar

/**
 * Display a datePicker set on today's date. When the user select a date, it's passed to
 * [block] in the format 'dd\MM\yyyy'.
 *
 * @param context the context required to create the date picker dialog.
 * @param calendar the calendar used to retrieve today's date.
 * @param minDate the minimal date selectable by the picker in the epoch format. If null
 * there is no limit set.
 * @param maxDate the maximal date selectable by the picker in the epoch format. If null
 * there is no limit set.
 * @param block the function called when the user select a date.
 */
fun displayDatePicker(context: Context, calendar: Calendar, minDate: Long? = null, maxDate: Long? = null, block: (date: String) -> Unit) {
    displayDatePicker(
        context,
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
        minDate,
        maxDate,
        block
    )
}

/**
 * Display a datePicker set on the specified date. When the user select a date, it's passed to
 * [block] in the format 'dd\MM\yyyy'.
 *
 * @param context the context required to create the date picker dialog.
 * @param year the year selected by default in the datePicker.
 * @param month the month selected by default in the datePicker, from 0 to 11.
 * @param day the day selected by default in the datePicker.
 * @param minDate the minimal date selectable by the picker in the epoch format. If null
 * there is no limit set.
 * @param maxDate the maximal date selectable by the picker in the epoch format. If null
 * there is no limit set.
 * @param block the function called when the user select a date.
 */
fun displayDatePicker(context: Context, year: Int, month: Int, day: Int, minDate: Long? = null, maxDate: Long? = null, block: (date: String) -> Unit) {
    val datePicker = DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
        block(String.format("%02d/%02d/$selectedYear", selectedDay, selectedMonth + 1))
    }, year, month, day)

    if (minDate != null)
        datePicker.datePicker.minDate = minDate
    if (maxDate != null)
        datePicker.datePicker.maxDate = maxDate

    datePicker.show()
}