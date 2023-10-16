package com.tutamail.julienm99.walletmanager.ui.wallet.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tutamail.julienm99.walletmanager.R
import com.tutamail.julienm99.walletmanager.core.model.Wallet
import com.tutamail.julienm99.walletmanager.ui.component.*
import com.tutamail.julienm99.walletmanager.ui.theme.AppTheme
import com.tutamail.julienm99.walletmanager.ui.wallet.deletion.WalletDeletionDialog
import java.util.*

@Composable
fun WalletSettingsScreen(
    onBackClicked: () -> Unit,
    onNavigateToWallet: (walletId: Long) -> Unit,
    onNavigateToWalletNotFound: (screenTitle: String) -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: WalletSettingsViewModel
) {
    val walletSettingsUiState by viewModel.walletSettingsUiState.collectAsStateWithLifecycle()

    val onNavigateToWallet = { walletId: Long ->
        onNavigateToWallet(walletId)
        viewModel.onWalletNavigated()
    }

    val onDeleteWallet = { wallet: Wallet ->
        viewModel.deleteWallet(wallet)
        onNavigateToHome()
        viewModel.onHomeNavigated()
    }

    WalletSettingsScreen(
        walletSettingsUiState = walletSettingsUiState,
        onBackClicked = onBackClicked,
        onNavigateToWallet = onNavigateToWallet,
        onNavigateToWalletNotFound = onNavigateToWalletNotFound,
        onWalletNameUpdated = viewModel::setWalletName,
        onResetWalletNameClicked = viewModel::onResetWalletNameClicked,
        onDeleteWalletClicked = viewModel::onDeleteWalletClicked,
        onDeleteWallet = onDeleteWallet,
        onDismissDeletionDialog = viewModel::hideDeletionDialog,
        onSaveSettingsClicked = viewModel::onSaveSettingsClicked
    )
}

@Composable
private fun WalletSettingsScreen(
    walletSettingsUiState: WalletSettingsUiState,
    onBackClicked: () -> Unit,
    onNavigateToWallet: (walletId: Long) -> Unit,
    onNavigateToWalletNotFound: (screenTitle: String) -> Unit,
    onWalletNameUpdated: (String) -> Unit,
    onResetWalletNameClicked: (wallet: Wallet) -> Unit,
    onDeleteWalletClicked: () -> Unit,
    onDeleteWallet: (wallet: Wallet) -> Unit,
    onDismissDeletionDialog: () -> Unit,
    onSaveSettingsClicked: (wallet: Wallet) -> Unit,
) {
    val screenTitle = stringResource(R.string.wallet_settings_screen_title)

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = screenTitle,
                icon = Icons.Filled.ArrowBack,
                iconDescription = stringResource(R.string.wallet_settings_screen_navigate_up_description),
                onNavigationClick = onBackClicked
            )
        }
    ) { contentPadding ->
        Card(modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(rootPadding)
        ) {
            when (walletSettingsUiState) {
                WalletSettingsUiState.Loading -> {
                    Box(contentAlignment = Alignment.Center) {
                        AppCircularProgressIndicator()
                    }
                }

                WalletSettingsUiState.WalletNotFound -> {
                    LaunchedEffect(Unit) {
                        onNavigateToWalletNotFound(screenTitle)
                    }
                }

                is WalletSettingsUiState.Loaded -> {
                    val wallet = walletSettingsUiState.wallet

                    if (walletSettingsUiState.changesSaved) {
                        LaunchedEffect(Unit) {
                            onNavigateToWallet(wallet.id)
                        }
                    }

                    if (walletSettingsUiState.showDeletionDialog) {
                        WalletDeletionDialog(
                            onDeletion = { onDeleteWallet(wallet) },
                            onCancel = onDismissDeletionDialog
                        )
                    }

                    Column(
                        modifier = cardContentModifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AppTextField(
                            value = walletSettingsUiState.newName,
                            label = stringResource(R.string.wallet_settings_screen_wallet_name_hint),
                            onValueChange = onWalletNameUpdated,
                            onClearButtonClick = { onResetWalletNameClicked(wallet) },
                            clearButtonContentDescription = stringResource(R.string.wallet_settings_screen_wallet_name_clear_button_description)
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        Text(
                            text = stringResource(R.string.wallet_settings_screen_advanced_wallet_settings_menu_title),
                            style = MaterialTheme.typography.h6
                        )

                        Button(
                            modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
                            onClick = onDeleteWalletClicked,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.error,
                                contentColor = MaterialTheme.colors.onError,
                            )
                        ) {
                            ButtonText(stringResource(R.string.wallet_settings_screen_delete_wallet_button))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(onClick = { onSaveSettingsClicked(wallet) }) {
                            ButtonText(stringResource(R.string.apply_action))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun WalletNotFoundWalletSettingsPreview() {
    WalletSettingsPreview(WalletSettingsUiState.WalletNotFound)
}

@Preview
@Composable
fun LoadingWalletSettingsPreview() {
    WalletSettingsPreview(WalletSettingsUiState.Loading)
}

@Preview
@Composable
fun LoadedWalletSettingsPreview() {
    val wallet = Wallet("Preview wallet", currency = Currency.getInstance(Locale.US))
    val uiState = WalletSettingsUiState.Loaded(
        wallet = wallet,
        newName = wallet.name,
        showDeletionDialog = false,
    )

    WalletSettingsPreview(uiState)
}

@Composable
private fun WalletSettingsPreview(uiState: WalletSettingsUiState) {
    AppTheme {
        WalletSettingsScreen(
            walletSettingsUiState = uiState,
            onBackClicked = {},
            onNavigateToWallet = {},
            onNavigateToWalletNotFound = {},
            onWalletNameUpdated = {},
            onResetWalletNameClicked = {},
            onDeleteWalletClicked = {},
            onDeleteWallet = {},
            onDismissDeletionDialog = {},
            onSaveSettingsClicked = {},
        )
    }
}