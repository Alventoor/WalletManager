package com.github.alventoor.walletmanager.ui.transactioncategory.deletion

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.dialog

private const val transactionCategoryIdArgs = "transactionCategoryId"
private const val transactionCategoryDeletionRoute = "transactionCategoryDeletion"

fun NavGraphBuilder.transactionCategoryDeletionDialog(
    onDismiss: () -> Unit,
) {
    dialog(
        transactionCategoryDeletionRoute.plus("/{$transactionCategoryIdArgs}"),
        listOf(navArgument(transactionCategoryIdArgs) { type = NavType.LongType })
    ) { backStackEntry ->
        val args = TransactionCategoryDeletionArgs(backStackEntry)
        val viewModel: TransactionCategoryDeletionViewModel = viewModel(factory = TransactionCategoryDeletionViewModelFactory(args.transactionCategoryId, LocalContext.current))

        TransactionCategoryDeletionDialog(onDismiss, viewModel)
    }
}

fun NavController.navigateToTransactionCategoryDeletion(transactionCategoryId: Long) {
    navigate("$transactionCategoryDeletionRoute/$transactionCategoryId")
}

private class TransactionCategoryDeletionArgs(val transactionCategoryId: Long) {
    constructor(backStackEntry: NavBackStackEntry): this(backStackEntry.arguments?.getLong(transactionCategoryIdArgs) ?: 0)
}