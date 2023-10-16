package com.github.alventoor.walletmanager.core.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.github.alventoor.walletmanager.core.database.WalletManagerDao
import com.github.alventoor.walletmanager.core.database.asEntity
import com.github.alventoor.walletmanager.core.database.asModel
import com.github.alventoor.walletmanager.core.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineTransactionsRepository(private val database: WalletManagerDao): TransactionsRepository {
    override fun searchTransactions(searchQuery: String): Flow<List<Transaction>> {
        val sqlQuery = SimpleSQLiteQuery(searchQuery)

        return database.getTransactionsFiltered(sqlQuery).map { it.asModel() }
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        database.insertTransaction(transaction.asEntity())
    }

    override suspend fun deleteTransactions(transactions: List<Long>) {
        database.deleteTransactions(transactions)
    }

    override suspend fun deleteTransactionsFromCategory(transactionCategoryId: Long) {
        database.deleteTransactionsFromCategory(transactionCategoryId)
    }

    override suspend fun replaceTransactionCategoryWith(oldTransactionCategoryId: Long, newTransactionCategoryId: Long) {
        database.replaceTransactionCategoryWith(oldTransactionCategoryId, newTransactionCategoryId)
    }
}