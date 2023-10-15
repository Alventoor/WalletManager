package com.tutamail.julienm99.walletmanager.ui.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

fun currencyFormatter(currency: Currency): NumberFormat {
    val locale = Locale.getDefault()
    val currencyLocale = Locale.Builder()
        .setRegion(currency.currencyCode.substring(0..1))
        .build()

    // We use the currency format used by the device's locale if we can
    return when (currencyLocale.country == locale.country) {
        true -> NumberFormat.getCurrencyInstance(locale)
        false -> NumberFormat.getCurrencyInstance(currencyLocale)
    }
}

fun formatCurrency(currency: Currency, value: BigDecimal): String = currencyFormatter(currency).format(value)