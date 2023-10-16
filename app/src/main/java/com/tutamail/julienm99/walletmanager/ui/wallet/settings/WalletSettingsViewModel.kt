package com.tutamail.julienm99.walletmanager.ui.wallet.settings

import androidx.lifecycle.*
import com.tutamail.julienm99.walletmanager.core.model.Wallet
import com.tutamail.julienm99.walletmanager.core.repository.WalletsRepository
import com.tutamail.julienm99.walletmanager.core.util.toStateFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WalletSettingsViewModel(
    walletId: Long,
    private val walletsRepository: WalletsRepository,
): ViewModel() {
    private val wallet: Flow<Wallet?> = walletsRepository.getWallet(walletId)
        .onEach {  wallet ->
            if (wallet != null)
                newWalletName.value = wallet.name
        }

    private val newWalletName: MutableStateFlow<String> = MutableStateFlow("")

    private val showWalletDeletionDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val changesSaved: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val walletDeleted: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val walletSettingsUiState = combine(wallet, newWalletName, showWalletDeletionDialog, changesSaved, walletDeleted) { wallet, newName, showDeletionDialog, changesSaved, deleted ->

        when (wallet) {
            null -> WalletSettingsUiState.WalletNotFound
            else -> WalletSettingsUiState.Loaded(wallet, newName, showDeletionDialog, changesSaved)
        }
    }.toStateFlow(WalletSettingsUiState.Loading)

    fun onWalletNavigated() {
        changesSaved.value = false
    }

    fun onHomeNavigated() {
        walletDeleted.value = false
        showWalletDeletionDialog.value = false
    }

    fun setWalletName(newName: String) {
        newWalletName.value = newName
    }

    fun onResetWalletNameClicked(wallet: Wallet) {
        newWalletName.value = wallet.name
    }

    fun onSaveSettingsClicked(wallet: Wallet) {
        viewModelScope.launch {
            val newName = newWalletName.value

            if (newName != wallet.name) {
                walletsRepository.updateWalletName(wallet, newName)
            }

            changesSaved.value = true
        }
    }

    fun onDeleteWalletClicked() {
        showWalletDeletionDialog.value = true
    }

    fun deleteWallet(wallet: Wallet) {
        viewModelScope.launch {
            walletsRepository.deleteWallet(wallet)

            showWalletDeletionDialog.value = false
            walletDeleted.value = true
        }
    }

    fun hideDeletionDialog() {
        showWalletDeletionDialog.value = false
    }
}

sealed interface WalletSettingsUiState {
    data object Loading: WalletSettingsUiState

    data object WalletNotFound: WalletSettingsUiState

    data class Loaded(
        val wallet: Wallet,
        val newName: String,
        val showDeletionDialog: Boolean,
        val changesSaved: Boolean = false,
    ): WalletSettingsUiState
}