package com.github.alventoor.walletmanager.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.alventoor.walletmanager.core.model.TransactionCategory
import com.github.alventoor.walletmanager.core.repository.TransactionCategoriesRepository
import com.github.alventoor.walletmanager.core.util.toStateFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val transactionCategoriesRepository: TransactionCategoriesRepository
) : ViewModel() {
    private val transactionCategories: Flow<List<TransactionCategory>> = transactionCategoriesRepository.getTransactionCategories()

    private val editedTransactionCategory: MutableStateFlow<TransactionCategory?> = MutableStateFlow(null)

    val transactionCategoryUiState: StateFlow<TransactionCategoryUiState> = combine(transactionCategories, editedTransactionCategory) { categories, edited ->
        TransactionCategoryUiState.Success(TransactionCategorySettings(
            categories = categories,
            editedCategory = edited,
        ))
    }.toStateFlow(TransactionCategoryUiState.Loading)

    fun transactionCategoryToEdit(transactionCategory: TransactionCategory) {
        editedTransactionCategory.value = transactionCategory
    }

    fun editTransactionCategoryName(name: String) {
        editedTransactionCategory.value = editedTransactionCategory.value?.copy(name = name)
    }

    fun resetTransactionCategoryEditing() {
        editedTransactionCategory.value = null
    }

    fun updateTransactionCategory() {
        viewModelScope.launch {
            when (val category = editedTransactionCategory.value) {
                null -> {}
                else -> transactionCategoriesRepository.updateTransactionCategory(category)
            }
        }

        resetTransactionCategoryEditing()
    }

    fun addTransactionCategory(transactionCategory: TransactionCategory) {
        viewModelScope.launch {
            transactionCategoriesRepository.insertTransactionCategory(transactionCategory)
        }
    }
}

data class TransactionCategorySettings(
    val categories: List<TransactionCategory>,
    val editedCategory: TransactionCategory? = null,
) {
    val isEditing: Boolean = editedCategory != null
}

sealed interface TransactionCategoryUiState {
    object Loading: TransactionCategoryUiState
    data class Success(val settings: TransactionCategorySettings): TransactionCategoryUiState
}