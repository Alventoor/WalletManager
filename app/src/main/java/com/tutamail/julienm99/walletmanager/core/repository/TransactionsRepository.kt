package com.tutamail.julienm99.walletmanager.core.repository

import com.tutamail.julienm99.walletmanager.core.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    fun searchTransactions(searchQuery: String): Flow<List<Transaction>>

    suspend fun insertTransaction(transaction: Transaction)

    // TODO Replace Long with Transaction?
    suspend fun deleteTransactions(transactions: List<Long>)

    suspend fun deleteTransactionsFromCategory(transactionCategoryId: Long)

    suspend fun replaceTransactionCategoryWith(oldTransactionCategoryId: Long, newTransactionCategoryId: Long)
}