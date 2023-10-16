package com.github.alventoor.walletmanager

import com.github.alventoor.walletmanager.ui.util.DecimalDigitsInputFilter
import org.junit.Assert.assertEquals
import org.junit.Test

class DecimalDigitsInputFilterTest {
    private val filter: DecimalDigitsInputFilter = DecimalDigitsInputFilter(2)
    private val integerFilter: DecimalDigitsInputFilter = DecimalDigitsInputFilter(0)

    @Test
    fun validPositiveSymbolValue() {
        val validSymbol = "+"

        assertEquals(true, filter.matches(validSymbol))
    }

    @Test
    fun validNegativeSymbolValue() {
        val validSymbol = "-"

        assertEquals(true, filter.matches(validSymbol))
    }

    @Test
    fun validIntegerValueWithSymbol() {
        val validValue = "+12"

        assertEquals(true, filter.matches(validValue))
    }

    @Test
    fun validPositiveIntegerValueWithoutSymbol() {
        val validValue = "12"

        assertEquals(true, filter.matches(validValue))
    }

    @Test
    fun validDecimalValue() {
        val validValue = "+12.06"

        assertEquals(true, filter.matches(validValue))
    }

    @Test
    fun invalidDecimalValue() {
        val invalidValue = "+12.0693"

        assertEquals(false, filter.matches(invalidValue))
    }

    @Test
    fun validDecimalValueWithComma() {
        val validValue = "+12,06"

        assertEquals(true, filter.matches(validValue))
    }

    @Test
    fun invalidInput() {
        val invalidInput = "12Invalid"

        assertEquals(false, filter.matches(invalidInput))
    }

    @Test
    fun validIntegerWithZeroDigits() {
        val validInput= "+12"

        assertEquals(true, integerFilter.matches(validInput))
    }

    @Test
    fun invalidIntegerWithZeroDigits() {
        val invalidInput = "+12."

        assertEquals(false, integerFilter.matches(invalidInput))
    }
}