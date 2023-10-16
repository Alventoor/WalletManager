package com.github.alventoor.walletmanager.ui.home

import androidx.lifecycle.*
import com.github.alventoor.walletmanager.core.model.Wallet
import com.github.alventoor.walletmanager.core.repository.WalletsRepository
import com.github.alventoor.walletmanager.core.util.toStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: WalletsRepository): ViewModel() {
    private val wallets: StateFlow<List<Wallet>> = repository.getWallets().toStateFlow(emptyList())

    private val walletsToDelete: MutableStateFlow<Map<Long, Wallet>> = MutableStateFlow(emptyMap())

    private val selectionModeEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val walletsUiState: StateFlow<WalletsUiState> = combine(wallets, walletsToDelete, selectionModeEnabled) { wallets, walletsToDelete, selectionModeEnabled ->
        when (wallets.isEmpty()) {
            true -> WalletsUiState.Empty
            false -> {
                val wallets = wallets.map { WalletUiState(it, walletsToDelete.contains(it.id)) }

                WalletsUiState.Loaded(wallets, selectionModeEnabled, walletsToDelete.isNotEmpty())
            }
        }
    }.toStateFlow(WalletsUiState.Loading)

    fun onWalletSelectionEnabled() {
        selectionModeEnabled.value = true
    }

    fun onClearSelectionList() {
        walletsToDelete.value = emptyMap()
        selectionModeEnabled.value = false
    }

    fun onWalletSelected(wallet: WalletUiState) {
        when (!wallet.selected) {
            true -> addWalletToDeletion(wallet.data)
            false -> removeWalletFromDeletion(wallet.data)
        }
    }

    fun onWalletsDeletionClicked() {
        viewModelScope.launch {
            repository.deleteWallets(walletsToDelete.value.values.toList())
            onClearSelectionList()
        }
    }

    private fun addWalletToDeletion(wallet: Wallet) {
        walletsToDelete.value = walletsToDelete.value.plus(wallet.id to wallet)
        selectionModeEnabled.value = true
    }

    private fun removeWalletFromDeletion(wallet: Wallet) {
        walletsToDelete.value = walletsToDelete.value.minus(wallet.id)

        if (walletsToDelete.value.isEmpty())
            selectionModeEnabled.value = false
    }
}

sealed interface WalletsUiState {
    val displayCheckbox: Boolean
    val hasSelectedWallets: Boolean

    object Loading: WalletsUiState {
        override val displayCheckbox: Boolean = false
        override val hasSelectedWallets: Boolean = false
    }

    object Empty: WalletsUiState {
        override val displayCheckbox: Boolean = false
        override val hasSelectedWallets: Boolean = false
    }

    data class Loaded(
        val wallets: List<WalletUiState>,
        override val displayCheckbox: Boolean,
        override val hasSelectedWallets: Boolean
    ): WalletsUiState
}

data class WalletUiState(val data: Wallet, val selected: Boolean)