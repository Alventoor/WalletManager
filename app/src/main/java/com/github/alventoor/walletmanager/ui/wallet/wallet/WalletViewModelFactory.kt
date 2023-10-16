package com.github.alventoor.walletmanager.ui.wallet.wallet

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.alventoor.walletmanager.core.database.WalletManagerDatabase
import com.github.alventoor.walletmanager.core.repository.OfflineTransactionsRepository
import com.github.alventoor.walletmanager.core.repository.OfflineWalletsRepository
import java.lang.IllegalArgumentException

class WalletViewModelFactory(
    private val walletId: Long,
    private val context: Context
): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
            val database = WalletManagerDatabase.getInstance(context).walletManagerDao()
            val walletsRepository = OfflineWalletsRepository(database)
            val transactionsRepository = OfflineTransactionsRepository(database)

            return WalletViewModel(walletId, walletsRepository, transactionsRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}