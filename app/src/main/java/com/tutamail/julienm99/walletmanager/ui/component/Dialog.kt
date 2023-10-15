package com.tutamail.julienm99.walletmanager.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tutamail.julienm99.walletmanager.ui.theme.AppTheme

@Composable
fun AppDialog(
    title: String,
    dismissButton: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(shape = RoundedCornerShape(6.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                text = title,
                style = MaterialTheme.typography.subtitle1
            )

            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                content()
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                dismissButton()

                confirmButton()
            }
        }
    }
}

@Preview
@Composable
private fun AppDialogPreview() {
    AppTheme {
        AppDialog(
            "Title",
            dismissButton = {
                TextButton(onClick = {}) {
                    ButtonText(stringResource(android.R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = {}) {
                    ButtonText(stringResource(android.R.string.ok))
                }
            }
        ) {
            Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
        }
    }
}