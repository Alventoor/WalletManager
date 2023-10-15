package com.tutamail.julienm99.walletmanager.ui.transactioncategory.creation

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog

private const val transactionCategoryCreationRoute = "transactionCategoryCreation"

fun NavGraphBuilder.transactionCategoryCreationDialog(
    onBackClicked: () -> Unit,
) {
    dialog(transactionCategoryCreationRoute) {
        val viewModel: TransactionCategoryCreationViewModel = viewModel(
            factory = TransactionCategoryCreationViewModelFactory(LocalContext.current)
        )

        TransactionCategoryCreationDialog(onBackClicked, viewModel)
    }
}

fun NavController.navigateToTransactionCategoryCreation() {
    navigate(transactionCategoryCreationRoute)
}