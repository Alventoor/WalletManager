package com.tutamail.julienm99.walletmanager.core.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tutamail.julienm99.walletmanager.core.model.TransactionCategory

@Entity(tableName = "transaction_category")
data class TransactionCategoryEntity(
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "icon_name")
    val iconName: String = "",
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
)

fun TransactionCategoryEntity.asModel() = TransactionCategory(
    name = name,
    iconName = iconName,
    id = id
)

fun TransactionCategory.asEntity() = TransactionCategoryEntity(
    name = name,
    iconName = iconName,
    id = id
)

fun List<TransactionCategoryEntity>.asModel()= map {
    it.asModel()
}