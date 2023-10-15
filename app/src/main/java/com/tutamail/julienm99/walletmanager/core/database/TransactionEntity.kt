package com.tutamail.julienm99.walletmanager.core.database

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Entity
import androidx.room.ForeignKey
import com.tutamail.julienm99.walletmanager.core.model.Transaction
import com.tutamail.julienm99.walletmanager.core.util.toTimestamp
import java.time.LocalDate

@Entity(
    tableName = "wallet_transaction",
    foreignKeys = [
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["id"],
            childColumns = ["wallet_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TransactionCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class TransactionEntity(
    @ColumnInfo(name = "wallet_id")
    val walletId: Long,
    @ColumnInfo(name = "currency_code")
    val currencyCode: String,
    @ColumnInfo(name = "value")
    val value: Long,
    @ColumnInfo(name = "date")
    val date: Long = LocalDate.now().toTimestamp(),
    @ColumnInfo(name = "description")
    val description: String = "",
    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
)

fun Transaction.asEntity() = TransactionEntity(
    walletId = walletId,
    currencyCode = currency.currencyCode,
    value = value.scaleByPowerOfTen(currency.defaultFractionDigits).longValueExact(),
    date = date,
    description = description,
    categoryId = category?.id,
    id = id
)