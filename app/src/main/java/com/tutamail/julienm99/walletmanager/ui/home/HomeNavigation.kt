package com.tutamail.julienm99.walletmanager.ui.home

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable

const val homeRoute = "home"

fun NavGraphBuilder.homeScreen(
    onSettingsClicked: () -> Unit,
    onWalletClicked: (walletId: Long) -> Unit,
    onAddWalletClicked: () -> Unit,
) {
    composable(homeRoute) {
        val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(LocalContext.current))

        HomeScreen(
            onSettingsClicked = onSettingsClicked,
            onWalletClicked = onWalletClicked,
            onAddWalletClicked = onAddWalletClicked,
            viewModel = viewModel
        )
    }
}

fun NavController.navigateToHome(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(homeRoute, builder)
}