package com.github.alventoor.walletmanager.ui.settings

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

private const val settingsRoute = "settings"

fun NavGraphBuilder.settingsScreen(
    onBackClicked: () -> Unit,
    onDeleteTransactionCategoryClicked: (categoryId: Long) -> Unit
) {
    composable(settingsRoute) {
        val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(LocalContext.current))

        SettingsScreen(
            onBackClicked,
            onDeleteTransactionCategoryClicked,
            viewModel
        )
    }
}

fun NavController.navigateToSettings() {
    navigate(settingsRoute)
}