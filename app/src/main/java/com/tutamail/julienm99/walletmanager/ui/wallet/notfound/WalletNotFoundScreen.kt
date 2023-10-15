package com.tutamail.julienm99.walletmanager.ui.wallet.notfound

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.tutamail.julienm99.walletmanager.R
import com.tutamail.julienm99.walletmanager.ui.component.*
import com.tutamail.julienm99.walletmanager.ui.theme.AppTheme

@Composable
fun WalletNotFoundScreen(title: String, onBackClicked: () -> Unit) {
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = title,
                icon = Icons.Filled.ArrowBack,
                iconDescription = stringResource(R.string.wallet_not_found_screen_navigate_back_description),
                onNavigationClick = onBackClicked
            )
        }
    ) { contentPadding ->
        Card(
            modifier = Modifier.padding(contentPadding).padding(rootPadding).fillMaxSize(),
            elevation = cardElevation) {
            Box(
                modifier = cardContentModifier,
                contentAlignment = Alignment.Center
            ) {
                TitleText(stringResource(R.string.wallet_not_found_screen_description))
            }
        }
    }
}

@Preview
@Composable
fun WalletNotFoundPreview() {
    AppTheme {
        WalletNotFoundScreen(
            title = "Wallet not found",
            onBackClicked = {}
        )
    }
}