package com.tutamail.julienm99.walletmanager.ui.wallet.settings

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.composable

private const val walletIdArgs = "walletId"
private const val walletSettingsRoute = "walletSettings"

fun NavGraphBuilder.walletSettingsScreen(
    onBackClicked: () -> Unit,
    onNavigateToWallet: (walletId: Long) -> Unit,
    onNavigateToWalletNotFound: (screenTitle: String) -> Unit,
    onNavigateToHome: () -> Unit,
) {
    composable(
        walletSettingsRoute.plus("/{$walletIdArgs}"),
        listOf(navArgument(walletIdArgs) { type = NavType.LongType })
    ) { backStackEntry ->
        val args = WalletSettingsArgs(backStackEntry)
        val viewModel: WalletSettingsViewModel = viewModel(factory = WalletSettingsViewModelFactory(args.walletId, LocalContext.current))

        WalletSettingsScreen(
            onBackClicked = onBackClicked,
            onNavigateToWallet = onNavigateToWallet,
            onNavigateToWalletNotFound = onNavigateToWalletNotFound,
            onNavigateToHome = onNavigateToHome,
            viewModel = viewModel
        )
    }
}

fun NavController.navigateToWalletSettings(walletId: Long) {
    navigate("$walletSettingsRoute/$walletId")
}

private class WalletSettingsArgs(val walletId: Long) {
    constructor(backStackEntry: NavBackStackEntry): this(backStackEntry.arguments?.getLong(walletIdArgs) ?: 0)
}