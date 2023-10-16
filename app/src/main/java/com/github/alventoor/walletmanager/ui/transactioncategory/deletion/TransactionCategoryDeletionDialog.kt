package com.github.alventoor.walletmanager.ui.transactioncategory.deletion

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.alventoor.walletmanager.R
import com.github.alventoor.walletmanager.core.model.TransactionCategory
import com.github.alventoor.walletmanager.ui.component.AppDialog
import com.github.alventoor.walletmanager.ui.component.AppDropDownList
import com.github.alventoor.walletmanager.ui.component.ButtonText
import com.github.alventoor.walletmanager.ui.theme.AppTheme

@Composable
fun TransactionCategoryDeletionDialog(
    onDismiss: () -> Unit,
    viewModel: TransactionCategoryDeletionViewModel
) {
    val transactionCategoryDeletionState by viewModel.transactionCategoryDeletionState.collectAsStateWithLifecycle()

    TransactionCategoryDeletionDialog(
        transactionCategoryDeletionState = transactionCategoryDeletionState,
        onUpdateDeleteAction = viewModel::updateDeleteAction,
        onUpdateReplacementCategory = viewModel::updateReplacementCategory,
        onDismiss = onDismiss,
        onConfirm = {
            viewModel.deleteTransactionCategory()
            onDismiss()
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TransactionCategoryDeletionDialog(
    transactionCategoryDeletionState: TransactionCategoryDeletionState,
    onUpdateDeleteAction: (TransactionCategoryDeleteAction) -> Unit,
    onUpdateReplacementCategory: (TransactionCategory) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AppDialog(
        dismissButton = {
            TextButton(onClick = onDismiss) {
                ButtonText(stringResource(android.R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                ButtonText(stringResource(R.string.delete_action))
            }
        },
        title = stringResource(R.string.transaction_category_deletion_title),
    ) {
        Column(modifier = Modifier.padding(horizontal = 6.dp)) {
            Text(
                modifier = Modifier.padding(12.dp),
                text = stringResource(R.string.transaction_category_deletion_text)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                DeleteActionRadioButton(
                    testedValue = transactionCategoryDeletionState.deleteAction,
                    buttonValue = TransactionCategoryDeleteAction.ORPHAN_TRANSACTIONS,
                    onClick = onUpdateDeleteAction
                )
                Text(stringResource(R.string.transaction_category_deletion_orphan_transactions))
            }

            FlowRow(verticalArrangement = Arrangement.Center) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DeleteActionRadioButton(
                        testedValue = transactionCategoryDeletionState.deleteAction,
                        buttonValue = TransactionCategoryDeleteAction.REPLACE_CATEGORY,
                        onClick = onUpdateDeleteAction,
                        enabled = transactionCategoryDeletionState.otherCategories.isNotEmpty()
                    )
                    Text(
                        text = stringResource(R.string.transaction_category_deletion_replace_category)
                    )
                }
                AppDropDownList(
                    modifier = Modifier.padding(start = 6.dp).weight(1f),
                    items = transactionCategoryDeletionState.otherCategories,
                    selectedItem = transactionCategoryDeletionState.replacementCategory,
                    itemDescription = TransactionCategory::name,
                    onItemSelected = onUpdateReplacementCategory,
                    enabled = transactionCategoryDeletionState.deleteAction == TransactionCategoryDeleteAction.REPLACE_CATEGORY
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                DeleteActionRadioButton(
                    testedValue = transactionCategoryDeletionState.deleteAction,
                    buttonValue = TransactionCategoryDeleteAction.DELETE_TRANSACTIONS,
                    onClick = onUpdateDeleteAction,
                )
                Text(stringResource(R.string.transaction_category_deletion_delete_transactions))
            }
        }
    }
}

@Composable
private fun DeleteActionRadioButton(
    testedValue: TransactionCategoryDeleteAction,
    buttonValue: TransactionCategoryDeleteAction,
    onClick: (TransactionCategoryDeleteAction) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    RadioButton(
        modifier = modifier,
        selected = testedValue == buttonValue,
        enabled = enabled,
        onClick = { onClick(buttonValue) }
    )
}

@Preview
@Composable
private fun DeleteDialogPreview() {
    val transactionCategories by remember { mutableStateOf(listOf(
        TransactionCategory(name = "Transaction Category 1", id = 1),
        TransactionCategory(name = "Transaction Category 2", id = 2)
    )) }

    val state = TransactionCategoryDeletionState(
        otherCategories = transactionCategories,
        deleteAction = TransactionCategoryDeleteAction.ORPHAN_TRANSACTIONS,
        replacementCategory = transactionCategories.first()
    )

    AppTheme {
        TransactionCategoryDeletionDialog(
            transactionCategoryDeletionState = state,
            onUpdateDeleteAction = {},
            onUpdateReplacementCategory = {},
            onDismiss = {},
            onConfirm = {}
        )
    }
}