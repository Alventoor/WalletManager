package com.tutamail.julienm99.walletmanager.ui.wallet.deletion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tutamail.julienm99.walletmanager.R
import com.tutamail.julienm99.walletmanager.core.model.Wallet
import com.tutamail.julienm99.walletmanager.ui.component.ButtonText
import com.tutamail.julienm99.walletmanager.ui.theme.AppTheme
import java.util.*

@Composable
fun WalletDeletionDialog(
    onDeletion: () -> Unit,
    onCancel: () -> Unit,
    wallets: List<Wallet> = emptyList(),
) {
    AlertDialog(
        title = {
            Text(stringResource(R.string.wallet_deletion_dialog_title))
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (wallets.size > 1) {
                    true -> Text(stringResource(R.string.wallet_deletion_dialog_multiple_wallets_message))
                    false -> Text(stringResource(R.string.wallet_deletion_dialog_one_wallet_message))
                }

                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    for (wallet in wallets) {
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = "\u2022 ${wallet.name}"
                        )
                    }
                }
            }
        },
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(onClick = onDeletion) {
                ButtonText(stringResource(R.string.wallet_deletion_dialog_confirm_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                ButtonText(stringResource(android.R.string.cancel))
            }
        }
    )
}

@Preview
@Composable
private fun WalletDeletionDialogPreview() {
    AppTheme {
        WalletDeletionDialog({}, {})
    }
}

@Preview
@Composable
private fun WalletsDeletionDialogPreview() {
    val currency = Currency.getInstance(Locale.getDefault())

    val wallets = listOf(
        Wallet(name = "Wallet 1", currency = currency),
        Wallet(name = "Wallet 2", currency = currency)
    )

    AppTheme {
        WalletDeletionDialog({}, {}, wallets)
    }
}