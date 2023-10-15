package com.tutamail.julienm99.walletmanager.ui.transactioncreation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tutamail.julienm99.walletmanager.core.database.WalletManagerDatabase
import com.tutamail.julienm99.walletmanager.core.repository.OfflineTransactionCategoriesRepository
import com.tutamail.julienm99.walletmanager.core.repository.OfflineTransactionsRepository
import com.tutamail.julienm99.walletmanager.core.repository.OfflineWalletsRepository
import java.lang.IllegalArgumentException

class TransactionCreationViewModelFactory(
    private val walletId: Long,
    private val context: Context,
): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionCreationViewModel::class.java)) {
            val database = WalletManagerDatabase.getInstance(context).walletManagerDao()
            val transactionsRepository = OfflineTransactionsRepository(database)
            val transactionCategoriesRepository = OfflineTransactionCategoriesRepository(database)
            val walletRepository = OfflineWalletsRepository(database)

            return TransactionCreationViewModel(walletId, transactionsRepository, transactionCategoriesRepository, walletRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}