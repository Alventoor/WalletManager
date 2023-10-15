package com.tutamail.julienm99.walletmanager.ui.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tutamail.julienm99.walletmanager.R
import com.tutamail.julienm99.walletmanager.core.model.TransactionCategory
import com.tutamail.julienm99.walletmanager.ui.component.*
import com.tutamail.julienm99.walletmanager.ui.theme.AppTheme

@Composable
fun SettingsScreen(
    onBackClicked: () -> Unit,
    onDeleteTransactionCategoryClicked: (categoryId: Long) -> Unit,
    viewModel: SettingsViewModel
) {
    val transactionCategoryState by viewModel.transactionCategoryUiState.collectAsStateWithLifecycle()

    SettingsScreen(
        transactionCategoryUiState = transactionCategoryState,
        onBackClicked = onBackClicked,
        onDeleteTransactionCategoryClicked = onDeleteTransactionCategoryClicked,
        onSetTransactionCategoryToEdit = viewModel::transactionCategoryToEdit,
        onResetTransactionCategoryEditing = viewModel::resetTransactionCategoryEditing,
        onEditTransactionCategoryName = viewModel::editTransactionCategoryName,
        onUpdateTransactionCategory = viewModel::updateTransactionCategory,
        onAddTransactionCategory = viewModel::addTransactionCategory
    )
}

@Composable
private fun SettingsScreen(
    transactionCategoryUiState: TransactionCategoryUiState,
    onBackClicked: () -> Unit,
    onDeleteTransactionCategoryClicked: (Long) -> Unit,
    onSetTransactionCategoryToEdit: (TransactionCategory) -> Unit,
    onResetTransactionCategoryEditing: () -> Unit,
    onEditTransactionCategoryName: (String) -> Unit,
    onUpdateTransactionCategory: () -> Unit,
    onAddTransactionCategory: (TransactionCategory) -> Unit,
) {
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = stringResource(R.string.settings_fragment_title),
                icon = Icons.Filled.ArrowBack,
                iconDescription = stringResource(R.string.settings_top_app_bar_content_description_navigate_up),
                onNavigationClick = onBackClicked
            )
        }
    ) { contentPadding ->
        val lazyListState = rememberLazyListState()

        Card(modifier = Modifier
            .padding(contentPadding)
            .padding(top = verticalRootPadding, start = horizontalRootPadding, end = horizontalRootPadding)
            .layout { measurable, constraints ->
                // Used to hide the top of the card when scrolling
                val heightOffset = 4.dp

                val height = when (lazyListState.firstVisibleItemIndex > 0) {
                    true -> verticalRootPadding
                    false -> lazyListState.firstVisibleItemScrollOffset.dp.coerceAtMost(verticalRootPadding + heightOffset)
                }.roundToPx()

                val placeable = measurable.measure(constraints.copy(maxHeight = constraints.maxHeight + height))

                layout(placeable.width, placeable.height) {
                    placeable.place(0, -height / 2)
                }
            },
            elevation = cardElevation
        ) {
            val transactionCategoryMenuExpanded = rememberSaveable { mutableStateOf(true) }

            LazyColumn(
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(cardContentPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(horizontal = cardContentPadding)
            ) {
                SettingsCategory(
                    modifier = Modifier.padding(vertical = cardContentPadding),
                    isExpanded = transactionCategoryMenuExpanded,
                    titleId = R.string.transaction_category_settings_title,
                    alwaysDisplayDivider = false,
                ) {
                    when (transactionCategoryUiState) {
                        is TransactionCategoryUiState.Loading -> {
                            item(contentType = "loadingIndicator") {
                                Box(modifier = Modifier.padding(bottom = cardContentPadding)) {
                                    AppCircularProgressIndicator()
                                }
                            }
                        }

                        is TransactionCategoryUiState.Success -> {
                            items(
                                items = transactionCategoryUiState.settings.categories,
                                key = { it.id },
                                contentType = { it }
                            ) { transactionCategory ->
                                TransactionCategoryItem(
                                    transactionCategory = transactionCategory,
                                    editedTransactionCategory = transactionCategoryUiState.settings.editedCategory,
                                    isEditing = transactionCategoryUiState.settings.isEditing,
                                    onTransactionCategoryNameEdited = onEditTransactionCategoryName,
                                    onTransactionCategoryEditionReset = onResetTransactionCategoryEditing,
                                    onTransactionCategoryUpdated = onUpdateTransactionCategory,
                                    onEditionEnabled = onSetTransactionCategoryToEdit,
                                    onTransactionCategoryDeleted = onDeleteTransactionCategoryClicked,
                                )
                            }

                            TransactionCategoryFormItem(
                                modifier = Modifier.padding(bottom = cardContentPadding),
                                onTransactionCategoryAdded = onAddTransactionCategory
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun LazyListScope.SettingsCategory(
    isExpanded: MutableState<Boolean>,
    titleId: Int,
    modifier: Modifier = Modifier,
    alwaysDisplayDivider: Boolean = true,
    content: LazyListScope.() -> Unit,
) {
    item(contentType = "header") {
        val onClickLabel = when (isExpanded.value) {
            true -> stringResource(R.string.accessibility_close_menu_on_click)
            false -> stringResource(R.string.accessibility_open_menu_on_click)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClickLabel = onClickLabel) { isExpanded.value = !isExpanded.value }
                .then(modifier),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(text = stringResource(titleId), fontSize = 20.sp)

            when (isExpanded.value) {
                true -> Icon(
                    painter = painterResource(R.drawable.baseline_arrow_drop_up_24),
                    contentDescription = null
                )

                false -> Icon(
                    painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
                    contentDescription = null,
                )
            }
        }

        if (isExpanded.value || alwaysDisplayDivider)
            Divider(color = MaterialTheme.colors.primaryVariant)
    }

    if (isExpanded.value)
        content()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TransactionCategoryItem(
    transactionCategory: TransactionCategory,
    editedTransactionCategory: TransactionCategory?,
    isEditing: Boolean,
    onTransactionCategoryNameEdited: (String) -> Unit,
    onTransactionCategoryEditionReset: () -> Unit,
    onTransactionCategoryUpdated: () -> Unit,
    onEditionEnabled: (TransactionCategory) -> Unit,
    onTransactionCategoryDeleted: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (editedTransactionCategory?.id == transactionCategory.id) {
            OutlinedTextField(
                value = editedTransactionCategory.name,
                onValueChange = onTransactionCategoryNameEdited,
                singleLine = true
            )

            Button(onClick = onTransactionCategoryUpdated) {
                Icon(
                    painter = painterResource(R.drawable.baseline_check_24),
                    contentDescription = stringResource(R.string.transaction_category_settings_update_item_button)
                )
            }

            Button(onClick = onTransactionCategoryEditionReset) {
                Icon(
                    painter = painterResource(R.drawable.baseline_cancel_24),
                    contentDescription = stringResource(R.string.transaction_category_settings_cancel_item_change_button)
                )
            }
        }

        else {
            OutlinedTextField(
                value = transactionCategory.name,
                onValueChange = { },
                readOnly = true,
                enabled = false,
            )

            Button(
                onClick = { onEditionEnabled(transactionCategory) },
                enabled = !isEditing
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_mode_edit_24),
                    contentDescription = stringResource(R.string.transaction_category_settings_edit_item_button)
                )
            }

            Button(
                onClick = { onTransactionCategoryDeleted(transactionCategory.id) },
                enabled = !isEditing,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.error,
                    contentColor = MaterialTheme.colors.onError,
                ),
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_delete_forever_24),
                    contentDescription = stringResource(R.string.transaction_category_settings_delete_item_button)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
private fun LazyListScope.TransactionCategoryFormItem(
    onTransactionCategoryAdded: (TransactionCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    item(contentType = "transactionCategoryForm") {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.transaction_category_settings_add_category_title),
                style = MaterialTheme.typography.h6
            )

            FlowRow(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                var transactionCategoryName: String by rememberSaveable {
                    mutableStateOf("")
                }

                OutlinedTextField(
                    value = transactionCategoryName,
                    onValueChange = { transactionCategoryName = it },
                    label = { Text(stringResource(R.string.transaction_category_settings_add_category_category_name_label)) },
                    singleLine = true,
                )

                Button(
                    enabled = transactionCategoryName.isNotEmpty(),
                    onClick = {
                        onTransactionCategoryAdded(TransactionCategory(transactionCategoryName))
                        transactionCategoryName = ""
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.transaction_category_settings_add_category_button)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoadedSettingsScreenPreview() {
    val transactionCategories by remember { mutableStateOf(listOf(
        TransactionCategory(name = "Transaction Category 1", id = 1),
        TransactionCategory(name = "Transaction Category 2", id = 2)
    )) }

    SettingsScreenPreview(
        TransactionCategoryUiState.Success(TransactionCategorySettings(transactionCategories)),
    )
}

@Preview
@Composable
private fun LoadingSettingsScreenPreview() {
    SettingsScreenPreview(TransactionCategoryUiState.Loading)
}

@Composable
private fun SettingsScreenPreview(transactionCategoryUiState: TransactionCategoryUiState) {
    AppTheme {
        SettingsScreen(
            transactionCategoryUiState = transactionCategoryUiState,
            onBackClicked = {},
            onDeleteTransactionCategoryClicked = {},
            onUpdateTransactionCategory = {},
            onSetTransactionCategoryToEdit = {},
            onEditTransactionCategoryName = {},
            onResetTransactionCategoryEditing = {},
            onAddTransactionCategory = {}
        )
    }
}