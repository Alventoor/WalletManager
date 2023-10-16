package com.tutamail.julienm99.walletmanager.ui.transactioncreation

import androidx.lifecycle.*
import com.tutamail.julienm99.walletmanager.core.model.Transaction
import com.tutamail.julienm99.walletmanager.core.model.TransactionCategory
import com.tutamail.julienm99.walletmanager.core.model.Wallet
import com.tutamail.julienm99.walletmanager.core.repository.TransactionCategoriesRepository
import com.tutamail.julienm99.walletmanager.core.repository.TransactionsRepository
import com.tutamail.julienm99.walletmanager.core.repository.WalletsRepository
import com.tutamail.julienm99.walletmanager.core.util.combine
import com.tutamail.julienm99.walletmanager.core.util.toBigDecimalResult
import com.tutamail.julienm99.walletmanager.core.util.toStateFlow
import com.tutamail.julienm99.walletmanager.core.util.toTimestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class TransactionCreationViewModel(
    walletId: Long,
    private val transactionsRepository: TransactionsRepository,
    transactionCategoriesRepository: TransactionCategoriesRepository,
    walletsRepository: WalletsRepository,
): ViewModel() {
    private val wallet: Flow<Wallet?> = walletsRepository.getWallet(walletId)

    private val transactionValue: MutableStateFlow<BigDecimal> = MutableStateFlow(BigDecimal.ZERO)

    private val transactionDate: MutableStateFlow<Long?> = MutableStateFlow(null)

    private val transactionDescription: MutableStateFlow<String> = MutableStateFlow("")

    private val transactionCategory: MutableStateFlow<TransactionCategory?> = MutableStateFlow(null)

    private val transactionRegistered: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val transactionCategories: StateFlow<List<TransactionCategory>> = transactionCategoriesRepository.getTransactionCategories().toStateFlow(emptyList())

    val transactionCreationUiState: StateFlow<TransactionCreationUiState> = combine(wallet, transactionValue, transactionDate, transactionDescription, transactionCategory, transactionRegistered) { wallet, value, date, description, category, registered ->
        when (wallet) {
            null -> TransactionCreationUiState.WalletNotFound
            else -> TransactionCreationUiState.Loaded(wallet, value, date, description, category, registered)
        }
    }.toStateFlow(TransactionCreationUiState.Loading)

    fun onWalletNavigated() {
        transactionRegistered.value = false
    }

    fun setTransactionValue(value: String) {
        transactionValue.value = when (value) {
            "" -> BigDecimal.ZERO
            else -> value.toBigDecimalResult().getOrElse { return }
        }
    }

    fun setTransactionDescription(description: String) {
        transactionDescription.value = description
    }

    fun setTransactionDate(date: String) {
        transactionDate.value = date.toTimestamp().getOrNull()
    }

    fun setTransactionCategory(category: TransactionCategory?) {
        transactionCategory.value = category
    }

    fun registerTransaction(wallet: Wallet) {
        viewModelScope.launch {
            val transactionDate = transactionDate.value ?: return@launch

            val transaction = Transaction(
                wallet.id,
                wallet.currency,
                transactionValue.value,
                transactionDate,
                transactionDescription.value,
                transactionCategory.value
            )

            transactionsRepository.insertTransaction(transaction)
            transactionRegistered.value = true
        }
    }
}

sealed interface TransactionCreationUiState {
    data object Loading: TransactionCreationUiState

    data object WalletNotFound: TransactionCreationUiState

    data class Loaded(
        val wallet: Wallet,
        val value: BigDecimal,
        val date: Long?,
        val description: String,
        val category: TransactionCategory?,
        val transactionRegistered: Boolean = false
    ): TransactionCreationUiState {
        val isValid: Boolean = value != BigDecimal.ZERO && date != null && description.isNotBlank()
    }
}