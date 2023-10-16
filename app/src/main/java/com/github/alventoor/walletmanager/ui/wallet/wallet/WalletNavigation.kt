package com.github.alventoor.walletmanager.ui.wallet.wallet

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.composable

private const val walletRoute = "wallet"
private const val walletIdArgs = "walletId"
private const val walletRouteWithArgs = "$walletRoute/{$walletIdArgs}"

fun NavGraphBuilder.walletScreen(
    onBackClicked: () -> Unit,
    onNavigateToWalletNotFound: (screenTitle: String) -> Unit,
    onWalletSettingsClicked: (Long) -> Unit,
    onAddTransactionClicked: (Long) -> Unit,
) {
    composable(
        walletRouteWithArgs,
        listOf(navArgument(walletIdArgs) { type = NavType.LongType })
    ) { backStackEntry ->
        val args = WalletArgs(backStackEntry)
        val viewModel: WalletViewModel = viewModel(factory = WalletViewModelFactory(args.walletId, LocalContext.current))

        WalletScreen(
            onBackClicked = onBackClicked,
            onNavigateToWalletNotFound = onNavigateToWalletNotFound,
            onWalletSettingsClicked = onWalletSettingsClicked,
            onAddTransactionClicked = onAddTransactionClicked,
            viewModel = viewModel
        )
    }
}

fun NavController.navigateToWallet(walletId: Long, builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate("$walletRoute/$walletId", builder)
}

fun NavOptionsBuilder.popUpToWallet(inclusive: Boolean = false) {
    popUpTo(walletRouteWithArgs) { this.inclusive = inclusive }
}

private class WalletArgs(val walletId: Long) {
    constructor(backStackEntry: NavBackStackEntry): this(backStackEntry.arguments?.getLong(walletIdArgs) ?: 0)
}