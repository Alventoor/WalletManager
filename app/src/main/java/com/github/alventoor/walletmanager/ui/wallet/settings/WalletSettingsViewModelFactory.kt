package com.github.alventoor.walletmanager.ui.wallet.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.alventoor.walletmanager.core.database.WalletManagerDatabase
import com.github.alventoor.walletmanager.core.repository.OfflineWalletsRepository
import java.lang.IllegalArgumentException

class WalletSettingsViewModelFactory(
    private val walletId: Long,
    private val context: Context,
): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalletSettingsViewModel::class.java)) {
            val database = WalletManagerDatabase.getInstance(context).walletManagerDao()
            val repository = OfflineWalletsRepository(database)

            return WalletSettingsViewModel(walletId, repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}