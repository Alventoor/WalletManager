package com.tutamail.julienm99.walletmanager.ui.wallet.creation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tutamail.julienm99.walletmanager.R

const val walletCreationRoute = "walletCreation"

fun NavGraphBuilder.walletCreationScreen(
    onBackClicked: () -> Unit,
    onNavigateToWallet: (walletId: Long) -> Unit,
) {
    composable(walletCreationRoute) {
        val startCreditDescription = stringResource(R.string.wallet_creation_screen_start_credit)
        val viewModel: WalletCreationViewModel = viewModel(factory = WalletCreationViewModelFactory(startCreditDescription, LocalContext.current))

        WalletCreationScreen(onBackClicked, onNavigateToWallet, viewModel)
    }
}

fun NavController.navigateToWalletCreation() {
    navigate(walletCreationRoute)
}