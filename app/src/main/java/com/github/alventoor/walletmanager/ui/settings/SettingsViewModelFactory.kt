package com.github.alventoor.walletmanager.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.alventoor.walletmanager.core.database.WalletManagerDatabase
import com.github.alventoor.walletmanager.core.repository.OfflineTransactionCategoriesRepository
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