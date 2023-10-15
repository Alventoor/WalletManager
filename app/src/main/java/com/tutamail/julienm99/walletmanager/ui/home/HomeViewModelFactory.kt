package com.tutamail.julienm99.walletmanager.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tutamail.julienm99.walletmanager.core.database.WalletManagerDatabase
import com.tutamail.julienm99.walletmanager.core.repository.OfflineWalletsRepository
import java.lang.IllegalArgumentException

class HomeViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val database = WalletManagerDatabase.getInstance(context).walletManagerDao()
            val repository = OfflineWalletsRepository(database)

            return HomeViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}