package com.tutamail.julienm99.walletmanager.ui.transactioncategory.deletion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutamail.julienm99.walletmanager.core.model.TransactionCategory
import com.tutamail.julienm99.walletmanager.core.repository.TransactionCategoriesRepository
import com.tutamail.julienm99.walletmanager.core.repository.TransactionsRepository
import com.tutamail.julienm99.walletmanager.core.util.combineState
import com.tutamail.julienm99.walletmanager.core.util.toStateFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TransactionCategoryDeletionViewModel(
    private val categoryId: Long,
    private val transactionsRepository: TransactionsRepository,
    private val transactionCategoriesRepository: TransactionCategoriesRepository,
): ViewModel() {
    private val replacementCategory: MutableStateFlow<TransactionCategory> = MutableStateFlow(
        TransactionCategory("")
    )

    private val transactionCategories: StateFlow<List<TransactionCategory>> = transactionCategoriesRepository
        .getTransactionCategoriesExcept(categoryId)
        .toStateFlow(emptyList())

    private val deleteAction: MutableStateFlow<TransactionCategoryDeleteAction> = MutableStateFlow(
        TransactionCategoryDeleteAction.ORPHAN_TRANSACTIONS
    )

    val transactionCategoryDeletionState: StateFlow<TransactionCategoryDeletionState> = combineState(transactionCategories, replacementCategory, deleteAction) { categories, replacement, deleteAction ->
        TransactionCategoryDeletionState(
            otherCategories = categories,
            deleteAction = deleteAction,
            replacementCategory = replacement
        )
    }

    fun deleteTransactionCategory() {
        viewModelScope.launch {
            when (deleteAction.value) {
                TransactionCategoryDeleteAction.ORPHAN_TRANSACTIONS -> Unit
                TransactionCategoryDeleteAction.DELETE_TRANSACTIONS -> transactionsRepository.deleteTransactionsFromCategory(categoryId)
                TransactionCategoryDeleteAction.REPLACE_CATEGORY -> transactionsRepository.replaceTransactionCategoryWith(categoryId, replacementCategory.value.id)
            }

            transactionCategoriesRepository.deleteTransactionCategory(categoryId)
        }
    }

    fun updateDeleteAction(deleteAction: TransactionCategoryDeleteAction) {
        this.deleteAction.value = deleteAction
    }

    fun updateReplacementCategory(replacementCategory: TransactionCategory) {
        this.replacementCategory.value = replacementCategory
    }
}

data class TransactionCategoryDeletionState(
    val otherCategories: List<TransactionCategory>,
    val deleteAction: TransactionCategoryDeleteAction,
    val replacementCategory: TransactionCategory,
)

enum class TransactionCategoryDeleteAction{
    ORPHAN_TRANSACTIONS,
    DELETE_TRANSACTIONS,
    REPLACE_CATEGORY,
}