package com.tutamail.julienm99.walletmanager.ui.util

class DecimalDigitsInputFilter(digitsAfterZero: Int) {
    private val pattern: Regex

    init {
        val decimalPattern = when (digitsAfterZero > 0) {
            true -> "([.,][0-9]{0,${digitsAfterZero}})?"
            false -> ""
        }

        pattern = Regex("[+-]?([0-9]+$decimalPattern)?")
    }

    fun matches(text: String): Boolean {
        return pattern.matches(text)
    }
}