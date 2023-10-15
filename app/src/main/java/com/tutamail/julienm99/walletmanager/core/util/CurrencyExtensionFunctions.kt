package com.tutamail.julienm99.walletmanager.core.util

import java.util.*

/**
 * Return a list containing all currencies actually used by at least one country.
 */
fun listAllCountryCurrencies(): List<Currency> {
    return Locale.getISOCountries()
        .map { isoCountry -> Locale.Builder().setRegion(isoCountry).build() }
        .mapNotNull { locale -> Currency.getInstance(locale) }
        .toSet()
        .sortedBy { currency -> currency.displayName }
}