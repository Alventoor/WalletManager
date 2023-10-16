package com.github.alventoor.walletmanager.core.repository

import com.github.alventoor.walletmanager.core.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    fun searchTransactions(searchQuery: String): Flow<List<Transaction>>

    suspend fun insertTransaction(transaction: Transaction)

    suspend fun deleteTransactions(transactions: List<Long>)

    suspend fun deleteTransactionsFromCategory(transactionCategoryId: Long)

    suspend fun replaceTransactionCategoryWith(oldTransactionCategoryId: Long, newTransactionCategoryId: Long)
}