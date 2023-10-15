package com.tutamail.julienm99.walletmanager.core.repository

import com.tutamail.julienm99.walletmanager.core.database.WalletManagerDao
import com.tutamail.julienm99.walletmanager.core.database.asEntity
import com.tutamail.julienm99.walletmanager.core.database.asModel
import com.tutamail.julienm99.walletmanager.core.model.TransactionCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineTransactionCategoriesRepository(private val database: WalletManagerDao): TransactionCategoriesRepository {
    override fun getTransactionCategories(): Flow<List<TransactionCategory>> {
        return database.getTransactionCategories().map { it.asModel() }
    }

    override fun getTransactionCategoriesExcept(transactionCategoryId: Long): Flow<List<TransactionCategory>> {
        return database.getTransactionCategoriesExcept(transactionCategoryId).map { it.asModel() }
    }


    override suspend fun insertTransactionCategory(transactionCategory: TransactionCategory) {
        database.insertTransactionCategory(transactionCategory.asEntity())
    }

    override suspend fun updateTransactionCategory(transactionCategory: TransactionCategory) {
        database.updateTransactionCategory(transactionCategory.asEntity())
    }

    override suspend fun deleteTransactionCategory(transactionCategoryId: Long) {
        database.deleteTransactionCategory(transactionCategoryId)
    }
}