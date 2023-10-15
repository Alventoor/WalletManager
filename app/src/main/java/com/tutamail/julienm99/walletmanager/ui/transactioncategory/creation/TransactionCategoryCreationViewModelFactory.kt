package com.tutamail.julienm99.walletmanager.ui.transactioncategory.creation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tutamail.julienm99.walletmanager.core.database.WalletManagerDatabase
import com.tutamail.julienm99.walletmanager.core.repository.OfflineTransactionCategoriesRepository

class TransactionCategoryCreationViewModelFactory(
    private val context: Context
): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionCategoryCreationViewModel::class.java)) {
            val database = WalletManagerDatabase.getInstance(context).walletManagerDao()
            val repository = OfflineTransactionCategoriesRepository(database)

            return TransactionCategoryCreationViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}