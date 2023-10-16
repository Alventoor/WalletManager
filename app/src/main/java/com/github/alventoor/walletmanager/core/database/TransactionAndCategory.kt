package com.github.alventoor.walletmanager.core.database

import androidx.room.Embedded
import androidx.room.Relation
import com.github.alventoor.walletmanager.core.model.Transaction
import java.math.BigDecimal
import java.util.Currency

data class TransactionAndCategory(
    @Embedded
    val transaction: TransactionEntity,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: TransactionCategoryEntity?
)

fun TransactionAndCategory.asModel(): Transaction {
    val currency = Currency.getInstance(transaction.currencyCode)
    val value = BigDecimal.valueOf(transaction.value, currency.defaultFractionDigits)

    return Transaction(
        walletId = transaction.walletId,
        currency = currency,
        value = value,
        date = transaction.date,
        description = transaction.description,
        category = category?.asModel(),
        id = transaction.id
    )
}

fun List<TransactionAndCategory>.asModel() = map {
    it.asModel()
}