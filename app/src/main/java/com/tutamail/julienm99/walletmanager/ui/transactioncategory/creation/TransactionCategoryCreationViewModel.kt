package com.tutamail.julienm99.walletmanager.ui.transactioncategory.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutamail.julienm99.walletmanager.core.model.TransactionCategory
import com.tutamail.julienm99.walletmanager.core.repository.TransactionCategoriesRepository
import com.tutamail.julienm99.walletmanager.core.util.combineState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TransactionCategoryCreationViewModel(private val repository: TransactionCategoriesRepository): ViewModel() {
    private val categoryName: MutableStateFlow<String> = MutableStateFlow("")

    private val categoryIconPath: MutableStateFlow<String> = MutableStateFlow("")

    val transactionCreationUiState = combineState(categoryName, categoryIconPath) { name, iconPath ->
        TransactionCategoryCreationUiState(name, iconPath)
    }

    fun setCategoryName(name: String) {
        categoryName.value = name
    }

    fun setCategoryIconPath(iconPath: String) {
        categoryIconPath.value = iconPath
    }

    fun registerCategory() {
        viewModelScope.launch {
            val category = TransactionCategory(categoryName.value, categoryIconPath.value)
            repository.insertTransactionCategory(category)
        }
    }
}

data class TransactionCategoryCreationUiState(val name: String, val iconPath: String) {
    val areDataValid: Boolean = name.isNotBlank()
}