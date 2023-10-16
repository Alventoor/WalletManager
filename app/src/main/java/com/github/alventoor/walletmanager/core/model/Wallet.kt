package com.github.alventoor.walletmanager.core.model

import java.math.BigDecimal
import java.util.Currency

data class Wallet(
    val name: String,
    val currency: Currency,
    val balance: BigDecimal = BigDecimal.ZERO,
    val id: Long = 0L,
)