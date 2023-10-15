package com.tutamail.julienm99.walletmanager.core.database

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

const val TRANSACTION_TABLE: String = "wallet_transaction"

@Dao
interface WalletManagerDao {
    @Insert
    suspend fun insertWallet(wallet: WalletEntity): Long

    @Insert
    suspend fun insertWallets(wallets: List<WalletEntity>): List<Long>

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert
    suspend fun insertTransactionCategory(transactionCategory: TransactionCategoryEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM wallet_transaction WHERE id IN (:transactionsId)")
    suspend fun deleteTransactions(transactionsId: List<Long>)

    @Query("DELETE FROM wallet_transaction WHERE category_id = :transactionCategoryId")
    suspend fun deleteTransactionsFromCategory(transactionCategoryId: Long)

    @Query("DELETE FROM transaction_category WHERE id = :transactionCategoryId")
    suspend fun deleteTransactionCategory(transactionCategoryId: Long)

    @Delete
    suspend fun deleteWallet(wallet: WalletEntity)

    @Query("DELETE FROM wallet WHERE id IN (:walletsId)")
    suspend fun deleteWallets(walletsId: List<Long>)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("UPDATE wallet_transaction SET category_id = :newTransactionCategoryId WHERE category_id = :oldTransactionCategoryId")
    suspend fun replaceTransactionCategoryWith(oldTransactionCategoryId: Long, newTransactionCategoryId: Long)

    @Update
    suspend fun updateTransactionCategory(transactionCategory: TransactionCategoryEntity)

    @Query("UPDATE wallet SET name = :walletName WHERE id = :walletId")
    suspend fun updateWalletName(walletId: Long, walletName: String)

    @Query("SELECT * FROM wallet WHERE id = :walletId")
    fun getWallet(walletId: Long): Flow<WalletEntity?>

    @Query("SELECT * FROM wallet")
    fun getWallets(): Flow<List<WalletEntity>>

    @Query("SELECT * FROM wallet WHERE id IN (:walletsId)")
    fun getWallets(walletsId: List<Long>): Flow<List<WalletEntity>>

    @Transaction
    @Query("SELECT * FROM wallet_transaction WHERE id = :transactionId")
    fun getTransaction(transactionId: Long): Flow<TransactionAndCategory?>

    @Transaction
    @Query("SELECT * FROM wallet_transaction WHERE wallet_id = :walletId")
    fun getTransactions(walletId: Long): Flow<List<TransactionAndCategory>>

    @Transaction
    @RawQuery(observedEntities = [TransactionEntity::class])
    fun getTransactionsFiltered(query: SupportSQLiteQuery): Flow<List<TransactionAndCategory>>

    @Query("SELECT * FROM transaction_category WHERE id = :transactionCategoryId")
    fun getTransactionCategory(transactionCategoryId: Long): Flow<TransactionCategoryEntity?>

    @Query("SELECT * FROM transaction_category")
    fun getTransactionCategories(): Flow<List<TransactionCategoryEntity>>

    @Query("SELECT * FROM transaction_category WHERE id != :transactionCategoryId")
    fun getTransactionCategoriesExcept(transactionCategoryId: Long): Flow<List<TransactionCategoryEntity>>
}