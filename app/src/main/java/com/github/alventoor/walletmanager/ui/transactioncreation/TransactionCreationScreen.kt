package com.github.alventoor.walletmanager.ui.transactioncreation

import android.icu.util.Calendar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.alventoor.walletmanager.ui.component.*
import com.github.alventoor.walletmanager.R
import com.github.alventoor.walletmanager.core.model.TransactionCategory
import com.github.alventoor.walletmanager.core.model.Wallet
import com.github.alventoor.walletmanager.ui.theme.AppTheme
import com.github.alventoor.walletmanager.ui.util.DecimalDigitsInputFilter
import com.github.alventoor.walletmanager.ui.util.displayDatePicker
import java.math.BigDecimal
import java.util.*

@Composable
fun TransactionCreationScreen(
    onBackClicked: () -> Unit,
    onNavigateToWallet: (walletId: Long) -> Unit,
    onNavigateToWalletNotFound: (screenTitle: String) -> Unit,
    onAddTransactionCategoryClicked: () -> Unit,
    viewModel: TransactionCreationViewModel
) {
    val transactionCategories by viewModel.transactionCategories.collectAsStateWithLifecycle()
    val transactionCreationUiState by viewModel.transactionCreationUiState.collectAsStateWithLifecycle()

    val onNavigateToWallet = { walletId: Long ->
        onNavigateToWallet(walletId)
        viewModel.onWalletNavigated()
    }

    TransactionCreationScreen(
        transactionCategories = transactionCategories,
        transactionCreationUiState = transactionCreationUiState,
        onBackClicked = onBackClicked,
        onNavigateToWallet = onNavigateToWallet,
        onNavigateToWalletNotFound = onNavigateToWalletNotFound,
        onAddTransactionCategoryClicked = onAddTransactionCategoryClicked,
        onDescriptionUpdated = viewModel::setTransactionDescription,
        onValueUpdated = viewModel::setTransactionValue,
        onDateUpdated = viewModel::setTransactionDate,
        onCategoryUpdated = viewModel::setTransactionCategory,
        onAddButtonClicked = viewModel::registerTransaction
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TransactionCreationScreen(
    transactionCategories: List<TransactionCategory>,
    transactionCreationUiState: TransactionCreationUiState,
    onBackClicked: () -> Unit,
    onNavigateToWallet: (walletId: Long) -> Unit,
    onNavigateToWalletNotFound: (screenTitle: String) -> Unit,
    onAddTransactionCategoryClicked: () -> Unit,
    onDescriptionUpdated: (String) -> Unit,
    onValueUpdated: (String) -> Unit,
    onDateUpdated: (String) -> Unit,
    onCategoryUpdated: (TransactionCategory?) -> Unit,
    onAddButtonClicked: (wallet: Wallet) -> Unit,
) {
    val screenTitle = stringResource(R.string.transaction_creation_screen_title)

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = screenTitle,
                icon = Icons.Filled.ArrowBack,
                iconDescription = stringResource(R.string.transaction_creation_screen_navigate_up_description),
                onNavigationClick = onBackClicked
            )
        }
    ) { contentPadding ->
        Card(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(rootPadding),
            elevation = cardElevation
        ) {
            when (transactionCreationUiState) {
                is TransactionCreationUiState.Loading -> {
                    Box(contentAlignment = Alignment.Center) {
                        AppCircularProgressIndicator()
                    }
                }
                is TransactionCreationUiState.WalletNotFound -> {
                    LaunchedEffect(Unit) {
                        onNavigateToWalletNotFound(screenTitle)
                    }
                }
                is TransactionCreationUiState.Loaded -> {
                    if (transactionCreationUiState.transactionRegistered) {
                        LaunchedEffect(Unit) {
                            onNavigateToWallet(transactionCreationUiState.wallet.id)
                        }
                    }

                    Column(
                        modifier = Modifier.padding(vertical = cardContentPadding, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        FlowRow(
                            modifier = Modifier.width(IntrinsicSize.Max),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            maxItemsInEachRow = 2
                        ) {
                            val context = LocalContext.current
                            val inputFilter = DecimalDigitsInputFilter(transactionCreationUiState.wallet.currency.defaultFractionDigits)

                            var textValue: String by rememberSaveable { mutableStateOf("") }

                            LaunchedEffect(textValue) {
                                onValueUpdated(textValue)
                            }

                            AppTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = transactionCreationUiState.description,
                                label = stringResource(R.string.transaction_creation_screen_transaction_description_hint),
                                onValueChange = onDescriptionUpdated,
                                onClearButtonClick = { onDescriptionUpdated("") },
                                clearButtonContentDescription = stringResource(R.string.transaction_creation_screen_transaction_description_clear_button_description)
                            )

                            AppTextField(
                                modifier = Modifier.fillMaxWidth(0.45f),
                                value = textValue,
                                label = stringResource(R.string.transaction_creation_screen_transaction_value_hint),
                                onValueChange = { if (inputFilter.matches(it)) textValue = it },
                                onClearButtonClick = { textValue = "" },
                                clearButtonContentDescription = stringResource(R.string.transaction_creation_screen_transaction_value_clear_button_description),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )


                            ClickableAppTextField(
                                modifier = Modifier.fillMaxWidth(0.5f),
                                value = dateFormattedShort(transactionCreationUiState.date),
                                label = stringResource(R.string.transaction_creation_screen_transaction_date_hint),
                                onClick = {
                                    displayDatePicker(
                                        context,
                                        Calendar.getInstance(),
                                        block = onDateUpdated
                                    )
                                },
                                onClearButtonClick = { onDateUpdated("") },
                                clearButtonContentDescription = stringResource(R.string.transaction_creation_screen_transaction_value_clear_button_description)
                            )

                            val nullCategoryDescription =
                                stringResource(R.string.transaction_creation_screen_no_transaction_category_selected)

                            AppDropDownList(
                                modifier = Modifier.weight(0.8f),
                                items = mutableListOf(null).plus(transactionCategories),
                                selectedItem = transactionCreationUiState.category,
                                itemDescription = { it?.name ?: nullCategoryDescription },
                                onItemSelected = onCategoryUpdated,
                                label = stringResource(R.string.transaction_creation_screen_transaction_category_hint)
                            )

                            IconButton(
                                modifier = Modifier.weight(0.2f),
                                onClick = onAddTransactionCategoryClicked
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.transaction_creation_screen_add_transaction_category_button_description))
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            modifier = Modifier.padding(top = 32.dp),
                            onClick = { onAddButtonClicked(transactionCreationUiState.wallet) },
                            enabled = transactionCreationUiState.isValid
                        ) {
                            ButtonText(stringResource(R.string.transaction_creation_screen_add_transaction_button))
                        }
                    }
                }
            }

        }
    }
}

@Preview
@Composable
fun WalletNotFoundTransactionCreationScreenPreview() {
    TransactionCreationScreenPreview(TransactionCreationUiState.WalletNotFound)
}

@Preview
@Composable
fun LoadingTransactionCreationScreenPreview() {
    TransactionCreationScreenPreview(TransactionCreationUiState.Loading)
}

@Preview
@Composable
fun LoadedTransactionCreationScreenPreview() {
    val wallet = Wallet("Preview wallet", Currency.getInstance(Locale.US), BigDecimal.ZERO)
    val uiState = TransactionCreationUiState.Loaded(wallet, BigDecimal.ZERO, null, "", null)

    TransactionCreationScreenPreview(uiState)
}

@Composable
private fun TransactionCreationScreenPreview(uiState: TransactionCreationUiState) {
    AppTheme {
        TransactionCreationScreen(
            transactionCategories = listOf(),
            transactionCreationUiState = uiState,
            onBackClicked = {},
            onNavigateToWallet = {},
            onNavigateToWalletNotFound = {},
            onAddTransactionCategoryClicked = {},
            onDescriptionUpdated = {},
            onValueUpdated = {},
            onCategoryUpdated = {},
            onDateUpdated = {},
            onAddButtonClicked = {}
        )
    }
}