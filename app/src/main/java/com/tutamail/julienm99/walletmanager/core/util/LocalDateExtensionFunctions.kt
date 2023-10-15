package com.tutamail.julienm99.walletmanager.core.util

import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Convert a [LocalDate] to the Unix Epoch timestamp.
 *
 * @return the converted timestamp.
 */
fun LocalDate.toTimestamp(): Long {
    return atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
}