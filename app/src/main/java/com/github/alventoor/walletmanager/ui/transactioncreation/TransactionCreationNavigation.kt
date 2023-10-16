package com.github.alventoor.walletmanager.ui.transactioncreation

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.composable

private const val walletIdArgs = "walletId"
const val transactionCreationRoute = "transactionCreation"

fun NavGraphBuilder.transactionCreationScreen(
    onBackClicked: () -> Unit,
    onNavigateToWallet: (walletId: Long) -> Unit,
    onNavigateToWalletNotFound: (screenTitle: String) -> Unit,
    onAddTransactionCategoryClicked: () -> Unit
) {
    composable(
        transactionCreationRoute.plus("/{$walletIdArgs}"),
        listOf(navArgument(walletIdArgs) { type = NavType.LongType })
    ) { backStackEntry ->
        val args = TransactionCreationArgs(backStackEntry)
        val viewModel: TransactionCreationViewModel = viewModel(factory = TransactionCreationViewModelFactory(args.walletId, LocalContext.current))

        TransactionCreationScreen(
            onBackClicked = onBackClicked,
            onNavigateToWallet = onNavigateToWallet,
            onNavigateToWalletNotFound = onNavigateToWalletNotFound,
            onAddTransactionCategoryClicked = onAddTransactionCategoryClicked,
            viewModel = viewModel
        )
    }
}

fun NavController.navigateToTransactionCreation(walletId: Long) {
    navigate("$transactionCreationRoute/$walletId")
}

private class TransactionCreationArgs(val walletId: Long) {
    constructor(backStackEntry: NavBackStackEntry): this(backStackEntry.arguments?.getLong(walletIdArgs) ?: 0)
}