package com.tutamail.julienm99.walletmanager.core.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tutamail.julienm99.walletmanager.core.model.Wallet
import java.math.BigDecimal
import java.util.*

@Entity(tableName = "wallet")
data class WalletEntity(
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "currency_code")
    val currencyCode: String,
    @ColumnInfo(name = "balance")
    val balance: Long = 0,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
)

fun WalletEntity.asModel(): Wallet {
    val currency = Currency.getInstance(currencyCode)
    val balance = BigDecimal.valueOf(balance, currency.defaultFractionDigits)

    return Wallet(
        name = name,
        currency = currency,
        balance = balance,
        id = id
    )
}

fun Wallet.asEntity() = WalletEntity(
    name = name,
    currencyCode = currency.currencyCode,
    balance = balance.scaleByPowerOfTen(currency.defaultFractionDigits).longValueExact(),
    id = id,
)

fun List<WalletEntity>.asModel() = map {
    it.asModel()
}