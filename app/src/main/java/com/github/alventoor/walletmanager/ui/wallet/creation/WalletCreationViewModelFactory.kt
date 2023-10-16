package com.github.alventoor.walletmanager.ui.wallet.creation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.alventoor.walletmanager.core.database.WalletManagerDatabase
import com.github.alventoor.walletmanager.core.repository.OfflineTransactionsRepository
import com.github.alventoor.walletmanager.core.repository.OfflineWalletsRepository
import kotlin.IllegalArgumentException

class WalletCreationViewModelFactory(
    private val startCreditDesc: String,
    private val context: Context
): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalletCreationViewModel::class.java)) {
            val database = WalletManagerDatabase.getInstance(context).walletManagerDao()
            val walletsRepository = OfflineWalletsRepository(database)
            val transactionsRepository = OfflineTransactionsRepository(database)

            return WalletCreationViewModel(startCreditDesc, walletsRepository, transactionsRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}