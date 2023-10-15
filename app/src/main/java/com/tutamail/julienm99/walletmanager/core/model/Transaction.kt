package com.tutamail.julienm99.walletmanager.core.model

import com.tutamail.julienm99.walletmanager.core.util.toTimestamp
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Currency

data class Transaction(
    val walletId: Long,
    val currency: Currency,
    val value: BigDecimal,
    val date: Long = LocalDate.now().toTimestamp(),
    val description: String,
    val category: TransactionCategory? = null,
    val id: Long = 0L,
)