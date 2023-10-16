package com.github.alventoor.walletmanager.ui.transactioncategory.creation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.alventoor.walletmanager.ui.component.*
import com.github.alventoor.walletmanager.R
import com.github.alventoor.walletmanager.ui.theme.AppTheme

@Composable
fun TransactionCategoryCreationDialog(
    onDismiss: () -> Unit,
    viewModel: TransactionCategoryCreationViewModel
) {
    val transactionCategoryCreationUiState by viewModel.transactionCreationUiState.collectAsStateWithLifecycle()

    TransactionCategoryCreationDialog(
        iconPaths = emptyList(),
        transactionCategoryCreationUiState = transactionCategoryCreationUiState,
        onTransactionCategoryNameUpdated = viewModel::setCategoryName,
        onTransactionCategoryIconPathUpdated = viewModel::setCategoryIconPath,
        onDismiss = onDismiss,
        onApply = {
            viewModel.registerCategory()
            onDismiss()
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TransactionCategoryCreationDialog(
    iconPaths: List<String>,
    transactionCategoryCreationUiState: TransactionCategoryCreationUiState,
    onTransactionCategoryNameUpdated: (String) -> Unit,
    onTransactionCategoryIconPathUpdated: (String) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
) {
    AppDialog(
        title = stringResource(R.string.transaction_category_creation_screen_title),
        dismissButton = {
            TextButton(onClick = onDismiss) {
                ButtonText(stringResource(android.R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(
                enabled = transactionCategoryCreationUiState.areDataValid,
                onClick = onApply
            ) {
                ButtonText(stringResource(R.string.transaction_category_creation_screen_add_category_button))
            }
        }
    ) {
        FlowRow(
            modifier = cardContentModifier.width(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 2,
        ) {
            AppTextField(
                modifier = Modifier.weight(1f),
                value = transactionCategoryCreationUiState.name,
                label = stringResource(R.string.transaction_category_creation_screen_name_hint),
                onValueChange = onTransactionCategoryNameUpdated,
                onClearButtonClick = { onTransactionCategoryNameUpdated("") },
                clearButtonContentDescription = stringResource(R.string.transaction_category_creation_screen_name_clear_button_description)
            )

            AppDropDownList(
                modifier = Modifier.weight(1f),
                items = iconPaths,
                selectedItem = stringResource(R.string.transaction_category_creation_screen_no_icon_selected),
                itemDescription = { it },
                onItemSelected = onTransactionCategoryIconPathUpdated,
                label = stringResource(R.string.transaction_category_creation_screen_icon_hint)
            )
        }
    }
}

@Preview
@Composable
fun TransactionCategoryCreationPreview() {
    val transactionCategoryCreationUiState = TransactionCategoryCreationUiState("", "")

    AppTheme {
        TransactionCategoryCreationDialog(
            iconPaths = emptyList(),
            transactionCategoryCreationUiState = transactionCategoryCreationUiState,
            onDismiss = {},
            onApply = {},
            onTransactionCategoryNameUpdated = {},
            onTransactionCategoryIconPathUpdated = {},
        )
    }
}