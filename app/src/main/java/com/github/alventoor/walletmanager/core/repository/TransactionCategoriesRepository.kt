package com.github.alventoor.walletmanager.core.repository

import com.github.alventoor.walletmanager.core.model.TransactionCategory
import kotlinx.coroutines.flow.Flow

interface TransactionCategoriesRepository {
    fun getTransactionCategories(): Flow<List<TransactionCategory>>

    fun getTransactionCategoriesExcept(transactionCategoryId: Long): Flow<List<TransactionCategory>>

    suspend fun insertTransactionCategory(transactionCategory: TransactionCategory)

    suspend fun updateTransactionCategory(transactionCategory: TransactionCategory)

    suspend fun deleteTransactionCategory(transactionCategoryId: Long)
}