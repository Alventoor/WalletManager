package com.tutamail.julienm99.walletmanager.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tutamail.julienm99.walletmanager.core.database.WalletManagerDatabase
import com.tutamail.julienm99.walletmanager.core.repository.OfflineTransactionCategoriesRepository
import java.lang.IllegalArgumentException

class SettingsViewModelFactory(
    private val context: Context
    ): ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                val database = WalletManagerDatabase.getInstance(context).walletManagerDao()
                val transactionCategoriesRepository = OfflineTransactionCategoriesRepository(database)

                return SettingsViewModel(transactionCategoriesRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }