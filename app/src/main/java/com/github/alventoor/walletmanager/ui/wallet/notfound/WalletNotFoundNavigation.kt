package com.github.alventoor.walletmanager.ui.wallet.notfound

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.*
import androidx.navigation.compose.composable

private const val screenTitleArgs = "screenTitle"
private const val walletNotFoundRoute = "walletNotFound"

fun NavGraphBuilder.walletNotFoundScreen(
    onBackClicked: () -> Unit,
    enterTransition: EnterTransition? = null,
    exitTransition: ExitTransition? = null
) {
    composable(
        walletNotFoundRoute.plus("/{$screenTitleArgs}"),
        listOf(navArgument(screenTitleArgs) { type = NavType.StringType }),
        enterTransition = { enterTransition },
        exitTransition = { exitTransition }
    ) { backStackEntry ->
        val args = WalletNotFoundArgs(backStackEntry)
        WalletNotFoundScreen(title = args.screenTitle, onBackClicked = onBackClicked)
    }
}

fun NavController.navigateToWalletNotFound(screenTitle: String) {
    navigate("$walletNotFoundRoute/$screenTitle")
}

private class WalletNotFoundArgs(val screenTitle: String) {
    constructor(backStackEntry: NavBackStackEntry): this(backStackEntry.arguments?.getString(screenTitleArgs) ?: "")
}