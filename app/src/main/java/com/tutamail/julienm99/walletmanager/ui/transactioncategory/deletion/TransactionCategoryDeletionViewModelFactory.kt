package com.tutamail.julienm99.walletmanager.ui.transactioncategory.deletion

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tutamail.julienm99.walletmanager.core.database.WalletManagerDatabase
import com.tutamail.julienm99.walletmanager.core.repository.OfflineTransactionCategoriesRepository
import com.tutamail.julienm99.walletmanager.core.repository.OfflineTransactionsRepository
import java.lang.IllegalArgumentException

class TransactionCategoryDeletionViewModelFactory(
    private val categoryId: Long,
    private val context: Context
): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionCategoryDeletionViewModel::class.java)) {
            val database = WalletManagerDatabase.getInstance(context).walletManagerDao()
            val transactionsRepository = OfflineTransactionsRepository(database)
            val transactionCategoriesRepository = OfflineTransactionCategoriesRepository(database)

            return TransactionCategoryDeletionViewModel(categoryId, transactionsRepository, transactionCategoriesRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}