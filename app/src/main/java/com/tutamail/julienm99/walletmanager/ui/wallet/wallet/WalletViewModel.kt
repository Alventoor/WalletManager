package com.tutamail.julienm99.walletmanager.ui.wallet.wallet

import androidx.lifecycle.*
import com.tutamail.julienm99.walletmanager.core.database.*
import com.tutamail.julienm99.walletmanager.core.model.Transaction
import com.tutamail.julienm99.walletmanager.core.model.Wallet
import com.tutamail.julienm99.walletmanager.core.repository.TransactionsRepository
import com.tutamail.julienm99.walletmanager.core.repository.WalletsRepository
import com.tutamail.julienm99.walletmanager.core.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal

enum class TransactionOrdering(val orderQuery: String) {
    DateDesc("date DESC"),
    DateAsc("date ASC"),
    ValueDesc("value DESC, date DESC"),
    ValueAsc("value ASC, date DESC"),
}

class WalletViewModel(
    walletId: Long,
    walletsRepository: WalletsRepository,
    private val transactionsRepository: TransactionsRepository,
) : ViewModel() {
    private val wallet: Flow<Wallet?> = walletsRepository.getWallet(walletId)

    private val selectionModeEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val selectedTransactions: MutableStateFlow<Set<Long>> = MutableStateFlow(emptySet())

    private val transactionOrdering: MutableStateFlow<TransactionOrdering> = MutableStateFlow(TransactionOrdering.DateDesc)

    private val minDateFilter: MutableStateFlow<Long?> = MutableStateFlow(null)
    private val maxDateFilter: MutableStateFlow<Long?> = MutableStateFlow(null)

    private val minValueFilter: MutableStateFlow<BigDecimal?> = MutableStateFlow(null)
    private val maxValueFilter: MutableStateFlow<BigDecimal?> = MutableStateFlow(null)

    val transactionFilterUiState: StateFlow<TransactionFilterUiState> = combineState(minDateFilter, maxDateFilter, minValueFilter, maxValueFilter, transactionOrdering) { minDate, maxDate, minValue, maxValue, ordering ->
        TransactionFilterUiState(minDate, maxDate, minValue, maxValue, ordering)
    }

    private val filterQuery: MutableStateFlow<String> = MutableStateFlow(transactionFilterUiState.value.query())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val transactions: Flow<List<Transaction>> = filterQuery.flatMapLatest { filterQuery ->
        val startQuery = "SELECT * FROM $TRANSACTION_TABLE WHERE wallet_id = $walletId"

        transactionsRepository.searchTransactions("$startQuery $filterQuery")
    }

    private val transactionsUiState: StateFlow<TransactionsUiState> = combine(transactions, selectedTransactions, selectionModeEnabled) { transactions, selectedTransactions, selectionModeEnabled ->
        when (transactions.isNotEmpty()) {
            true -> {
                val transactionsUi = transactions.mapIndexed { id, transaction ->
                    val selected = selectedTransactions.contains(transaction.id)
                    val displayDate = if (id == 0) true else transactions[id - 1].date != transaction.date

                    TransactionUiState(transaction, selected, displayDate)
                }

                TransactionsUiState.Loaded(transactionsUi, selectionModeEnabled)
            }
            false -> TransactionsUiState.Empty
        }
    }.toStateFlow(TransactionsUiState.Loading)

    val walletUiState: StateFlow<WalletUiState> = combine(wallet, transactionsUiState) { wallet, transactionsUiState ->
        when (wallet) {
            null -> WalletUiState.WalletNotFound
            else -> WalletUiState.Loaded(wallet, transactionsUiState)
        }
    }.toStateFlow(WalletUiState.Loading)

    fun updateTransactionsOrder(transactionsOrder: TransactionOrdering) {
        transactionOrdering.value = transactionsOrder
    }

    fun onFilterApplied() {
        filterQuery.value = transactionFilterUiState.value.query()
    }

    fun enableDeleteMode() {
        selectionModeEnabled.value = true
    }

    fun updateStartDateSearch(startDate: String) {
        val epochDate = startDate.toTimestamp().getOrNull()
        minDateFilter.value = epochDate

        val endDate = maxDateFilter.value
        if (epochDate != null && endDate != null && epochDate > endDate)
            maxDateFilter.value = null
    }

    fun updateEndDateSearch(endDate: String) {
        val epochDate = endDate.toTimestamp().getOrNull()
        maxDateFilter.value = epochDate

        val startDate = minDateFilter.value
        if (epochDate != null && startDate != null && epochDate < startDate)
            minDateFilter.value = null
    }

    fun updateMinValueSearch(minValue: String) {
        minValueFilter.value = when (minValue) {
            "" -> null
            else -> minValue.toBigDecimalResult().getOrElse { return }
        }
    }

    fun updateMaxValueSearch(maxValue: String) {
        maxValueFilter.value = when(maxValue) {
            "" -> null
            else -> maxValue.toBigDecimalResult().getOrElse { return }
        }
    }

    private fun selectTransaction(transactionId: Long) {
        selectedTransactions.value = selectedTransactions.value.plus(transactionId)

        if (!selectionModeEnabled.value)
            selectionModeEnabled.value = true
    }

    private fun unselectTransaction(transactionId: Long) {
        selectedTransactions.value = selectedTransactions.value.minus(transactionId)

        if (selectedTransactions.value.isEmpty())
            selectionModeEnabled.value = false
    }

    fun onTransactionSelected(transactionState: TransactionUiState) {
        when (!transactionState.selected) {
            true -> selectTransaction(transactionState.data.id)
            false -> unselectTransaction(transactionState.data.id)
        }
    }

    fun clearSelectedTransactions() {
        selectedTransactions.value = emptySet()
        selectionModeEnabled.value = false
    }

    fun deleteTransactions() {
        viewModelScope.launch {
            transactionsRepository.deleteTransactions(selectedTransactions.value.toList())
            clearSelectedTransactions()
        }
    }
}

sealed interface WalletUiState {
    data object Loading: WalletUiState

    data object WalletNotFound: WalletUiState

    data class Loaded(val wallet: Wallet, val transactionsState: TransactionsUiState): WalletUiState
}

sealed interface TransactionsUiState {
    data object Loading: TransactionsUiState

    data object Empty: TransactionsUiState

    data class Loaded(val transactions: List<TransactionUiState>, val displayCheckbox: Boolean):
        TransactionsUiState
}

data class TransactionUiState(val data: Transaction, val selected: Boolean, val displayDate: Boolean)

data class TransactionFilterUiState(
    val minDate: Long?,
    val maxDate: Long?,
    val minValue: BigDecimal?,
    val maxValue: BigDecimal?,
    val ordering: TransactionOrdering,
) {
    val areValuesValid: Boolean = minValue == null || maxValue == null || minValue.longValueExact() <= maxValue.longValueExact()
    val isValid: Boolean = areValuesValid

    fun query(): String {
        var filterQuery = ""

        val valueQuery = when {
            minValue != null && maxValue != null -> "value BETWEEN $minValue AND $maxValue"
            minValue != null -> "value >= $minValue"
            maxValue != null -> "value <= $maxValue"
            else -> null
        }

        if (valueQuery != null)
            filterQuery += " AND $valueQuery"

        val dateQuery = when {
            minDate != null && maxDate != null -> "date BETWEEN $minDate AND $maxDate"
            minDate != null -> "date >= $minDate"
            maxDate != null -> "date <= $maxDate"
            else -> null
        }

        if (dateQuery != null)
            filterQuery += " AND $dateQuery"

        filterQuery += " ORDER BY ${ordering.orderQuery}"
        return filterQuery
    }
}