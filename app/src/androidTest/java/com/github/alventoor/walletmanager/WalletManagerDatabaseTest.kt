package com.github.alventoor.walletmanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.alventoor.walletmanager.core.database.*
import com.github.alventoor.walletmanager.core.database.WalletManagerDatabase.Companion.CALLBACK_WALLET_TRANSACTION
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class WalletManagerDatabaseTest {
    companion object {
        private const val defaultCurrencyCode: String = "EUR"
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var walletManagerDao: WalletManagerDao
    private lateinit var db: WalletManagerDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, WalletManagerDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .addCallback(CALLBACK_WALLET_TRANSACTION)
            .build()
        walletManagerDao = db.walletManagerDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetWallet() = runBlocking {
        val wallet = WalletEntity("wallet", defaultCurrencyCode, id = 1)

        walletManagerDao.insertWallet(wallet)
        val storedWallet = walletManagerDao.getWallet(wallet.id)

        assertEquals(wallet.id, storedWallet.first()?.id)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetWallets() = runBlocking {
        val wallets = listOf(
            WalletEntity("wallet", defaultCurrencyCode, id = 1),
            WalletEntity("wallet", defaultCurrencyCode, id = 2),
            WalletEntity("wallet", defaultCurrencyCode, id = 3),
            WalletEntity("wallet", defaultCurrencyCode, id = 4),
            WalletEntity("wallet", defaultCurrencyCode, id = 5)
        )

        walletManagerDao.insertWallets(wallets)
        val storedWallets = walletManagerDao.getWallets()
        assertEquals(wallets, storedWallets.first())

        val oddWallets = wallets.filter { it.id % 2 == 1L }
        val storedOddWallets = walletManagerDao.getWallets(listOf(1, 3, 5))
        assertEquals(oddWallets, storedOddWallets.first())
    }

    @Test
    @Throws(Exception::class)
    fun updateWalletName() = runBlocking {
        val wallet = WalletEntity("wallet", defaultCurrencyCode, id = 1)

        walletManagerDao.insertWallet(wallet)
        val newWalletName = "new_name"
        walletManagerDao.updateWalletName(1, newWalletName)

        val storedWallet = walletManagerDao.getWallet(1)
        assertEquals(newWalletName, storedWallet.first()?.name)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTransaction() = runBlocking {
        val wallet = WalletEntity("wallet", defaultCurrencyCode, id = 1)
        val transaction = TransactionEntity(id = 1, currencyCode = defaultCurrencyCode, value = 75, walletId = wallet.id)

        walletManagerDao.insertWallet(wallet)
        walletManagerDao.insertTransaction(transaction)

        // Check if the transaction has been correctly registered inside the db
        val storedTransaction = walletManagerDao.getTransaction(transaction.id)
        assertEquals(transaction.id, storedTransaction.first()?.transaction?.id)

        // Check if the balance of the associated wallet has been correctly updated
        val storedWallet = walletManagerDao.getWallet(wallet.id)
        assertEquals(transaction.value, storedWallet.first()?.balance)
    }

    @Test
    @Throws(Exception::class)
    fun deleteTransaction() = runBlocking {
        val wallet = WalletEntity("wallet", defaultCurrencyCode, id = 1)
        val transaction = TransactionEntity(id = 1, currencyCode = defaultCurrencyCode, value = 75, walletId = wallet.id)

        walletManagerDao.insertWallet(wallet)
        walletManagerDao.insertTransaction(transaction)
        walletManagerDao.deleteTransaction(transaction)

        val storedTransactions = walletManagerDao.getTransactions(wallet.id)
        assertEquals(0, storedTransactions.first().size)

        val storedWallet = walletManagerDao.getWallet(wallet.id)
        assertEquals(0L, storedWallet.first()?.balance)
    }

    @Test
    @Throws(Exception::class)
    fun deleteTransactions() = runBlocking {
        val wallet = WalletEntity("wallet", defaultCurrencyCode, id = 1)
        val transaction1 = TransactionEntity(id = 1, currencyCode = defaultCurrencyCode, value = 75, walletId = 1)
        val transaction2 = TransactionEntity(id = 2, currencyCode = defaultCurrencyCode, value = 15, walletId = 1)
        val transaction3 = TransactionEntity(id = 3, currencyCode = defaultCurrencyCode, value = 15, walletId = 1)

        walletManagerDao.insertWallet(wallet)
        walletManagerDao.insertTransaction(transaction1)
        walletManagerDao.insertTransaction(transaction2)
        walletManagerDao.insertTransaction(transaction3)
        walletManagerDao.deleteTransactions(listOf(transaction1.id, transaction2.id))

        val storedTransactions = walletManagerDao.getTransactions(wallet.id)
        assertEquals(listOf(transaction3.id), storedTransactions.first().map { it.transaction.id })
    }

    @Test
    @Throws(Exception::class)
    fun deleteTransactionsFromCategory() = runBlocking {
        val wallet = WalletEntity("wallet", defaultCurrencyCode, id = 1)
        val transactionCategory = TransactionCategoryEntity("Energy bill", id = 1)
        val transaction = TransactionEntity(value = 75, currencyCode = defaultCurrencyCode, walletId = wallet.id, categoryId = transactionCategory.id, id = 1)

        walletManagerDao.insertWallet(wallet)
        walletManagerDao.insertTransactionCategory(transactionCategory)
        walletManagerDao.insertTransaction(transaction)
        walletManagerDao.deleteTransactionsFromCategory(transactionCategory.id)

        val storedTransactions = walletManagerDao.getTransactions(wallet.id)
        assertEquals(0, storedTransactions.first().size)
    }

    @Test
    @Throws(Exception::class)
    fun updateTransaction() = runBlocking {
        val wallet = WalletEntity("wallet", defaultCurrencyCode, id = 1)
        var transaction = TransactionEntity(id = 1, currencyCode = defaultCurrencyCode, value = 75, walletId = wallet.id)

        walletManagerDao.insertWallet(wallet)
        walletManagerDao.insertTransaction(transaction)

        transaction = transaction.copy(value = 50)
        walletManagerDao.updateTransaction(transaction)

        val storedTransaction = walletManagerDao.getTransaction(transaction.id)
        assertEquals(transaction.value, storedTransaction.first()?.transaction?.value)

        val storedWallet = walletManagerDao.getWallet(wallet.id)
        assertEquals(transaction.value, storedWallet.first()?.balance)

        var secondTransaction = TransactionEntity(id = 2, currencyCode = defaultCurrencyCode, value = 130, walletId = wallet.id)
        walletManagerDao.insertTransaction(secondTransaction)

        secondTransaction = secondTransaction.copy(value = 170)
        walletManagerDao.updateTransaction(secondTransaction)

        assertEquals(220L, storedWallet.first()?.balance)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTransactions() = runBlocking {
        val wallet = WalletEntity("wallet", defaultCurrencyCode, id = 1)
        val transaction1 = TransactionEntity(id = 1, currencyCode = defaultCurrencyCode, value = 75, walletId = wallet.id)
        val transaction2 = TransactionEntity(id = 2, currencyCode = defaultCurrencyCode, value = 130, walletId = wallet.id)

        walletManagerDao.insertWallet(wallet)
        walletManagerDao.insertTransaction(transaction1)
        walletManagerDao.insertTransaction(transaction2)

        val storedTransactions = walletManagerDao.getTransactions(wallet.id)
        assertEquals(listOf(transaction1.id, transaction2.id), storedTransactions.first().map { it.transaction.id })
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTransactionCategory() = runBlocking {
        val transactionCategory = TransactionCategoryEntity("name", id = 1)

        walletManagerDao.insertTransactionCategory(transactionCategory)

        val storedTransactionCategory = walletManagerDao.getTransactionCategory(transactionCategory.id)
        assertEquals(transactionCategory.id, storedTransactionCategory.first()?.id)
    }

    @Test
    @Throws(Exception::class)
    fun updateTransactionCategory() = runBlocking {
        var transactionCategory = TransactionCategoryEntity("Energy bill", id = 1)

        walletManagerDao.insertTransactionCategory(transactionCategory)
        transactionCategory = transactionCategory.copy(name = "EDF")
        walletManagerDao.updateTransactionCategory(transactionCategory)

        val storedTransactionCategory = walletManagerDao.getTransactionCategory(transactionCategory.id)
        assertEquals(transactionCategory.name, storedTransactionCategory.first()?.name)
    }

    @Test
    @Throws(Exception::class)
    fun replaceTransactionCategoryWith() = runBlocking {
        val wallet = WalletEntity("wallet", defaultCurrencyCode, id = 1)
        val transactionCategory1 = TransactionCategoryEntity("name", id = 1)
        val transactionCategory2 = TransactionCategoryEntity("name", id = 2)
        val transaction = TransactionEntity(currencyCode = defaultCurrencyCode, value = 75, categoryId = transactionCategory1.id, walletId = wallet.id, id = 1)

        walletManagerDao.insertWallet(wallet)
        walletManagerDao.insertTransactionCategory(transactionCategory1)
        walletManagerDao.insertTransactionCategory(transactionCategory2)
        walletManagerDao.insertTransaction(transaction)
        walletManagerDao.replaceTransactionCategoryWith(transactionCategory1.id, transactionCategory2.id)

        val storedTransaction = walletManagerDao.getTransaction(transaction.id)
        assertEquals(transactionCategory2.id, storedTransaction.first()?.category?.id)
    }

    @Test
    @Throws(Exception::class)
    fun deleteTransactionCategory() = runBlocking {
        val transactionCategory = TransactionCategoryEntity("name", id = 1)

        walletManagerDao.insertTransactionCategory(transactionCategory)
        walletManagerDao.deleteTransactionCategory(transactionCategory.id)

        val storedTransactionCategories = walletManagerDao.getTransactionCategories()
        assertEquals(0, storedTransactionCategories.first().size)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTransactionCategories() = runBlocking {
        val transactionCategory1 = TransactionCategoryEntity("name", id = 1)
        val transactionCategory2 = TransactionCategoryEntity("name", id = 2)

        walletManagerDao.insertTransactionCategory(transactionCategory1)
        walletManagerDao.insertTransactionCategory(transactionCategory2)

        val storedTransactionCategory = walletManagerDao.getTransactionCategories()
        assertEquals(listOf(transactionCategory1, transactionCategory2), storedTransactionCategory.first())
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTransactionCategoriesExcept() = runBlocking {
        val transactionCategory1 = TransactionCategoryEntity("name", id = 1)
        val transactionCategory2 = TransactionCategoryEntity("name", id = 2)
        val transactionCategory3 = TransactionCategoryEntity("name", id = 3)

        walletManagerDao.insertTransactionCategory(transactionCategory1)
        walletManagerDao.insertTransactionCategory(transactionCategory2)
        walletManagerDao.insertTransactionCategory(transactionCategory3)

        val storedTransactionCategories = walletManagerDao.getTransactionCategoriesExcept(3)
        assertEquals(listOf(transactionCategory1, transactionCategory2), storedTransactionCategories.first())
    }

    @Test
    @Throws(Exception::class)
    fun deleteWallet() = runBlocking {
        val wallet = WalletEntity("wallet", defaultCurrencyCode, id = 1)

        walletManagerDao.insertWallet(wallet)
        walletManagerDao.deleteWallet(wallet)

        val storedWallets = walletManagerDao.getWallets()
        assertEquals(0, storedWallets.first().size)
    }

    @Test
    @Throws(Exception::class)
    fun deleteWallets() = runBlocking {
        val wallet1 = WalletEntity("wallet1", defaultCurrencyCode, id = 1)
        val wallet2 = WalletEntity("wallet2", defaultCurrencyCode, id = 2)

        walletManagerDao.insertWallet(wallet1)
        walletManagerDao.insertWallet(wallet2)
        walletManagerDao.deleteWallets(listOf(wallet1.id, wallet2.id))

        val storedWallets = walletManagerDao.getWallets()
        assertEquals(emptyList<WalletEntity>(), storedWallets.first())
    }

    @Test
    @Throws(Exception::class)
    fun autoDeleteTransactions() = runBlocking {
        val wallet = WalletEntity("wallet", defaultCurrencyCode, id = 1)
        val transaction1 = TransactionEntity(id = 1, currencyCode = defaultCurrencyCode, value = 75, walletId = wallet.id)
        val transaction2 = TransactionEntity(id = 2, currencyCode = defaultCurrencyCode, value = 130, walletId = wallet.id)

        walletManagerDao.insertWallet(wallet)
        walletManagerDao.insertTransaction(transaction1)
        walletManagerDao.insertTransaction(transaction2)
        walletManagerDao.deleteWallet(wallet)

        val storedTransactions = walletManagerDao.getTransactions(wallet.id)
        assertEquals(0, storedTransactions.first().size)
    }

    @Test
    @Throws(Exception::class)
    fun deleteAssociatedTransactionCategory() = runBlocking {
        val wallet = WalletEntity("wallet", defaultCurrencyCode, id = 1)
        val transactionCategory = TransactionCategoryEntity("name", id = 1)
        val transaction = TransactionEntity(walletId = wallet.id, currencyCode = defaultCurrencyCode, value = 75, categoryId = transactionCategory.id, id = 1)

        walletManagerDao.insertWallet(wallet)
        walletManagerDao.insertTransactionCategory(transactionCategory)
        walletManagerDao.insertTransaction(transaction)
        walletManagerDao.deleteTransactionCategory(transactionCategory.id)

        val storedTransaction = walletManagerDao.getTransaction(transaction.id).first()!!
        assertEquals(null, storedTransaction.category?.id)
    }
}