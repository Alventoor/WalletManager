package com.github.alventoor.walletmanager.ui.wallet.wallet

import android.icu.util.Calendar
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.alventoor.walletmanager.ui.component.*
import com.github.alventoor.walletmanager.ui.util.DecimalDigitsInputFilter
import com.github.alventoor.walletmanager.R
import com.github.alventoor.walletmanager.core.model.Transaction
import com.github.alventoor.walletmanager.core.model.TransactionCategory
import com.github.alventoor.walletmanager.core.model.Wallet
import com.github.alventoor.walletmanager.core.util.toTimestamp
import com.github.alventoor.walletmanager.ui.theme.AppTheme
import com.github.alventoor.walletmanager.ui.util.displayDatePicker
import com.github.alventoor.walletmanager.ui.util.formatCurrency
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*

@Composable
fun WalletScreen(
    onBackClicked: () -> Unit,
    onNavigateToWalletNotFound: (screenTitle: String) -> Unit,
    onWalletSettingsClicked: (walletId: Long) -> Unit,
    onAddTransactionClicked: (walletId: Long) -> Unit,
    viewModel: WalletViewModel
) {
    val walletUiState: WalletUiState by viewModel.walletUiState.collectAsStateWithLifecycle()
    val transactionFilterUiState: TransactionFilterUiState by viewModel.transactionFilterUiState.collectAsStateWithLifecycle()

    WalletScreen(
        walletUiState = walletUiState,
        transactionFilterUiState = transactionFilterUiState,
        onBackClicked = onBackClicked,
        onNavigateToWalletNotFound = onNavigateToWalletNotFound,
        onWalletSettingsClicked = onWalletSettingsClicked,
        onAddTransactionClicked = onAddTransactionClicked,
        onTransactionSelected = viewModel::onTransactionSelected,
        onClearSelectedTransactions = viewModel::clearSelectedTransactions,
        onDeleteSelectedTransactions = viewModel::deleteTransactions,
        onUpdateTransactionOrder = viewModel::updateTransactionsOrder,
        onUpdateStartDateSearch = viewModel::updateStartDateSearch,
        onUpdateEndDateSearch = viewModel::updateEndDateSearch,
        onUpdateMinValueSearch = viewModel::updateMinValueSearch,
        onUpdateMaxValueSearch = viewModel::updateMaxValueSearch,
        onFilterButtonClicked = viewModel::onFilterApplied,
        onDeleteModeClicked = viewModel::enableDeleteMode
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun WalletScreen(
    walletUiState: WalletUiState,
    transactionFilterUiState: TransactionFilterUiState,
    onBackClicked: () -> Unit,
    onNavigateToWalletNotFound: (screenTitle: String) -> Unit,
    onWalletSettingsClicked: (walletId: Long) -> Unit,
    onAddTransactionClicked: (walletId: Long) -> Unit,
    onTransactionSelected: (TransactionUiState) -> Unit,
    onClearSelectedTransactions: () -> Unit,
    onDeleteSelectedTransactions: () -> Unit,
    onUpdateTransactionOrder: (TransactionOrdering) -> Unit,
    onUpdateStartDateSearch: (String) -> Unit,
    onUpdateEndDateSearch: (String) -> Unit,
    onUpdateMinValueSearch: (String) -> Unit,
    onUpdateMaxValueSearch: (String) -> Unit,
    onFilterButtonClicked: () -> Unit,
    onDeleteModeClicked: () -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val bottomSheetCoroutine = rememberCoroutineScope()

    val onApplyFilterButtonClicked: () -> Unit = {
        onFilterButtonClicked()

        bottomSheetCoroutine.launch {
            bottomSheetState.hide()
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = {
            if (walletUiState is WalletUiState.Loaded) {
                TransactionFilterScreen(
                    wallet = walletUiState.wallet,
                    transactionFilterUiState = transactionFilterUiState,
                    onUpdateTransactionOrder = onUpdateTransactionOrder,
                    onUpdateStartDate = onUpdateStartDateSearch,
                    onUpdateEndDate = onUpdateEndDateSearch,
                    onUpdateMinValue = onUpdateMinValueSearch,
                    onUpdateMaxValue = onUpdateMaxValueSearch,
                    onApplyButtonClicked = onApplyFilterButtonClicked
                )
            }
        },
    ) {
        val defaultScreenTitle = stringResource(R.string.wallet_screen_default_title)

        val displayDeleteMenu = walletUiState is WalletUiState.Loaded
                && walletUiState.transactionsState is TransactionsUiState.Loaded
                && walletUiState.transactionsState.displayCheckbox

        Scaffold(
            topBar = {
                when (displayDeleteMenu) {
                    true -> DeleteTopAppBar(
                        onDelete = onDeleteSelectedTransactions,
                        onCancel = onClearSelectedTransactions,
                        deleteActionDescription = stringResource(R.string.wallet_screen_delete_selected_transactions_button_description),
                        cancelActionDescription = stringResource(R.string.wallet_screen_cancel_transaction_deletion_button_description),
                    )

                    false -> {
                        when (walletUiState) {
                            is WalletUiState.Loaded -> {
                                AppTopAppBar(
                                    title = walletUiState.wallet.name,
                                    icon = Icons.Filled.ArrowBack,
                                    iconDescription = stringResource(R.string.wallet_screen_navigate_up_description),
                                    onNavigationClick = onBackClicked,
                                ) {
                                    AppBarMenu(
                                        menuDescription = stringResource(R.string.wallet_screen_menu_button_description),
                                        items = listOf(
                                            stringResource(R.string.wallet_screen_menu_item_settings) to {
                                                onWalletSettingsClicked(walletUiState.wallet.id)
                                            },
                                            stringResource(R.string.wallet_screen_menu_item_delete_transactions) to onDeleteModeClicked
                                        )
                                    )
                                }
                            }

                            else -> {
                                AppTopAppBar(
                                    title = defaultScreenTitle,
                                    icon = Icons.Filled.ArrowBack,
                                    iconDescription = stringResource(R.string.wallet_screen_navigate_up_description),
                                    onNavigationClick = onBackClicked,
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                BottomAppBar {
                    if (walletUiState is WalletUiState.Loaded && walletUiState.transactionsState is TransactionsUiState.Loaded) {
                        OutlinedButton(
                            onClick = {
                                bottomSheetCoroutine.launch {
                                    bottomSheetState.show()
                                }
                            },
                            elevation = ButtonDefaults.elevation(defaultElevation = 2.5.dp)
                        ) {
                            Row {
                                Icon(imageVector = Icons.Filled.Search, contentDescription = null)
                                ButtonText(text = stringResource(R.string.wallet_screen_open_filter_menu_button))
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                if (walletUiState is WalletUiState.Loaded)
                    AnimatedVisibility(
                        visible = !displayDeleteMenu,
                        enter = slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth + fullWidth / 2 }),
                        exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth + fullWidth / 2 })
                    ) {
                        FloatingActionButton(onClick = { onAddTransactionClicked(walletUiState.wallet.id) }) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = stringResource(R.string.wallet_screen_add_transaction_button_description)
                            )
                        }
                    }
            },
            isFloatingActionButtonDocked = true
        ) { contentPadding ->
            when (walletUiState) {
                WalletUiState.Loading -> {
                    LoadingTransactionsView()
                }

                WalletUiState.WalletNotFound -> {
                    LaunchedEffect(Unit) {
                        onNavigateToWalletNotFound(defaultScreenTitle)
                    }
                }

                is WalletUiState.Loaded -> {
                    val wallet = walletUiState.wallet

                    Column(
                        modifier = Modifier
                            .padding(contentPadding)
                            .padding(rootPadding)
                            .fillMaxWidth()
                    ) {
                        Card(modifier = Modifier.fillMaxWidth(), elevation = cardElevation) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                TitleText(wallet.name)
                                Text(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    text = stringResource(
                                        R.string.wallet_screen_balance_presentation,
                                        formatCurrency(wallet.currency, wallet.balance)
                                    )
                                )
                            }
                        }

                        Card(modifier = Modifier.padding(top = 16.dp).fillMaxHeight()) {
                            Column(modifier = cardContentModifier) {
                                when (walletUiState.transactionsState) {
                                    TransactionsUiState.Empty -> EmptyTransactionsView()
                                    TransactionsUiState.Loading -> LoadingTransactionsView()
                                    is TransactionsUiState.Loaded -> TransactionsView(walletUiState.transactionsState, onTransactionSelected)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingTransactionsView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AppCircularProgressIndicator()
    }
}

@Composable
private fun EmptyTransactionsView() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleText(stringResource(R.string.wallet_screen_empty_wallet_description))
    }
}

@Composable
private fun TransactionsView(
    transactionsUiState: TransactionsUiState.Loaded,
    onTransactionSelected: (TransactionUiState) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = cardElevation,
        shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 2.dp, bottomEnd = 2.dp),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(
                items = transactionsUiState.transactions,
                key = { it.data.id }
            ) { transactionItem ->
                TransactionItem(
                    transactionItem,
                    transactionsUiState.displayCheckbox,
                    onTransactionSelected
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TransactionItem(
    transaction: TransactionUiState,
    displayCheckbox: Boolean,
    onTransactionSelected: (TransactionUiState) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (transaction.displayDate) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colors.primaryVariant)
            ) {
                val date = transaction.data.date

                Text(
                    modifier = Modifier.padding(2.dp).height(IntrinsicSize.Min),
                    text = stringResource(R.string.transaction_date, date, date, date),
                    color = Color.White
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onLongClick = { onTransactionSelected(transaction) },
                    onClick = {
                        if (displayCheckbox)
                            onTransactionSelected(transaction)
                    }
                )
                .padding(16.dp)
                .heightIn(min = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val data = transaction.data

            Column {
                Text(text = data.description)

                if (data.category != null)
                    TransactionCategoryText(text = data.category.name)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                val color = if (data.value.signum() == 1) when (isSystemInDarkTheme()) {
                    true -> Color(0xff4fa54e)
                    false -> Color(0xff3f843e)
                } else
                    Color.Unspecified

                Text(
                    text = formatCurrency(data.currency, data.value),
                    color = color,
                    fontWeight = FontWeight.Bold
                )

                if (displayCheckbox)
                    Checkbox(
                        checked = transaction.selected,
                        onCheckedChange = { onTransactionSelected(transaction) }
                    )
            }
        }
    }
}

@Composable
private fun TransactionCategoryText(text: String) {
    Text(
        text = text,
        fontFamily = FontFamily.SansSerif,
        style = MaterialTheme.typography.caption,
    )
}

@Composable
private fun TransactionFilterScreen(
    wallet: Wallet,
    transactionFilterUiState: TransactionFilterUiState,
    onUpdateTransactionOrder: (TransactionOrdering) -> Unit,
    onUpdateStartDate: (String) -> Unit,
    onUpdateEndDate: (String) -> Unit,
    onUpdateMinValue: (String) -> Unit,
    onUpdateMaxValue: (String) -> Unit,
    onApplyButtonClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colors.background)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleText(
            modifier = Modifier.padding(4.dp),
            text = stringResource(R.string.wallet_screen_filter_menu_title)
        )

        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val orderingDescriptions = stringArrayResource(R.array.transaction_order)

            Text(
                modifier = Modifier.padding(end = 8.dp),
                text = stringResource(R.string.wallet_screen_filter_menu_transactions_order_text),
            )

            AppDropDownList(
                modifier = Modifier.width(IntrinsicSize.Min),
                items = TransactionOrdering.entries,
                selectedItem = transactionFilterUiState.ordering,
                itemDescription = { ordering -> orderingDescriptions[ordering.ordinal] },
                onItemSelected = onUpdateTransactionOrder
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            elevation = cardElevation,
        ) {
            Column {
                FilterForm(title = stringResource(R.string.wallet_screen_filter_menu_date_category), true) {
                    TransactionDateFilterForm(
                        startDate = transactionFilterUiState.minDate,
                        endDate = transactionFilterUiState.maxDate,
                        onUpdateStartDate = onUpdateStartDate,
                        onUpdateEndDate = onUpdateEndDate
                    )
                }

                FilterForm(title = stringResource(R.string.wallet_screen_filter_menu_value_category), false) {
                    TransactionValueFilterForm(
                        areValuesValid = transactionFilterUiState.areValuesValid,
                        maxDecimalDigits = wallet.currency.defaultFractionDigits,
                        onUpdateMinValue = onUpdateMinValue,
                        onUpdateMaxValue = onUpdateMaxValue,
                    )
                }
            }
        }

        Button(
            modifier = Modifier.padding(vertical = 16.dp),
            onClick = onApplyButtonClicked,
            enabled = transactionFilterUiState.isValid
        ) {
            ButtonText(stringResource(R.string.apply_action))
        }
    }
}

@Composable
fun FilterForm(title: String, drawBottomSeparator: Boolean, content: @Composable () -> Unit) {
    var formVisibility: Boolean by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { formVisibility = !formVisibility }
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = title,
                style = MaterialTheme.typography.h6,
            )

            if (formVisibility)
                content()
        }

        if (drawBottomSeparator) {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(1.2.dp)
                .background(color = MaterialTheme.colors.primaryVariant)
            )
        }
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionDateFilterForm(
    startDate: Long?,
    endDate: Long?,
    onUpdateStartDate: (String) -> Unit,
    onUpdateEndDate: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ClickableAppTextField(
            value = dateFormattedShort(startDate),
            label = stringResource(R.string.wallet_screen_filter_menu_start_date_hint),
            onClick = { displayDatePicker(context, calendar, maxDate = endDate, block = onUpdateStartDate) },
            onClearButtonClick = { onUpdateStartDate("") },
            clearButtonContentDescription = stringResource(R.string.wallet_screen_filter_menu_start_date_clear_button_description)
        )

        ClickableAppTextField(
            value = dateFormattedShort(endDate),
            label = stringResource(R.string.wallet_screen_filter_menu_end_date_hint),
            onClick = { displayDatePicker(context, calendar, minDate = startDate, block = onUpdateEndDate) },
            onClearButtonClick = { onUpdateEndDate("") },
            clearButtonContentDescription =  stringResource(R.string.wallet_screen_filter_menu_end_date_clear_button_description)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionValueFilterForm(
    areValuesValid: Boolean,
    maxDecimalDigits: Int,
    onUpdateMinValue: (String) -> Unit,
    onUpdateMaxValue: (String) -> Unit
) {
    val inputFilter = DecimalDigitsInputFilter(maxDecimalDigits)
    var textMinValue: String by rememberSaveable { mutableStateOf("") }
    var textMaxValue: String by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(textMinValue) {
        onUpdateMinValue(textMinValue)
    }

    LaunchedEffect(textMaxValue) {
        onUpdateMaxValue(textMaxValue)
    }

    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TransactionValueTextField(
            value = textMinValue,
            onValueChange = {
                if (inputFilter.matches(it)) textMinValue = it
            },
            label = stringResource(R.string.wallet_screen_filter_menu_minimum_value_hint),
            onClearButtonClick = { textMinValue = "" },
            clearButtonContentDescription = stringResource(R.string.wallet_screen_filter_menu_minimum_value_clear_button_description),
            displayInvalidValueMessage = !areValuesValid,
            invalidValueText = stringResource(R.string.wallet_screen_filter_menu_invalid_minimum_value_text)
        )

        AppTextField(
            value = textMaxValue,
            onValueChange = {
                if (inputFilter.matches(it)) textMaxValue = it
            },
            label = stringResource(R.string.wallet_screen_filter_menu_maximum_value_hint),
            onClearButtonClick = { textMaxValue = "" },
            clearButtonContentDescription = stringResource(R.string.wallet_screen_filter_menu_maximum_value_clear_button_description),
        )
    }
}

@Composable
private fun TransactionValueTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    onClearButtonClick: () -> Unit,
    clearButtonContentDescription: String,
    displayInvalidValueMessage: Boolean,
    invalidValueText: String,
) {
    Column(
        modifier = Modifier.width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            onClearButtonClick = onClearButtonClick,
            clearButtonContentDescription = clearButtonContentDescription
        )

        if (displayInvalidValueMessage) {
            Text(
                text = invalidValueText,
                color = Color.Red,
            )
        }
    }
}

@Preview
@Composable
fun LoadingWalletScreenPreview() {
    WalletScreenPreview(WalletUiState.Loading)
}

@Preview
@Composable
fun WalletNotFoundWalletScreenPreview() {
    WalletScreenPreview(WalletUiState.WalletNotFound)
}

@Preview
@Composable
fun LoadedWalletScreenPreview() {
    val wallet = Wallet(
        name = "Preview wallet",
        currency = Currency.getInstance(Locale.US),
        balance = BigDecimal("100")
    )

    val walletUiState = WalletUiState.Loaded(
        wallet = wallet,
        transactionsState = TransactionsUiState.Loaded(
            transactions = listOf(
                TransactionUiState(
                    data = Transaction(
                        walletId = 1,
                        currency = wallet.currency,
                        value = BigDecimal("50"),
                        date = "12/12/2023".toTimestamp().getOrNull()!!,
                        description = "Preview transaction"
                    ),
                    selected = false,
                    displayDate = true
                ),
                TransactionUiState(
                    data = Transaction(
                        walletId = 1,
                        currency = wallet.currency,
                        value = BigDecimal("50"),
                        date = "10/12/2023".toTimestamp().getOrNull()!!,
                        description = "Preview transaction",
                        id = 1
                    ),
                    selected = false,
                    displayDate = true
                ),
                TransactionUiState(
                    data = Transaction(
                        walletId = 1,
                        currency = wallet.currency,
                        value = BigDecimal("-50"),
                        date = "10/12/2023".toTimestamp().getOrNull()!!,
                        description = "Preview transaction",
                        category = TransactionCategory(name = "Loisirs"),
                        id = 2
                    ),
                    selected = false,
                    displayDate = false,
                ),
            ),
            displayCheckbox = false,
        )
    )

    WalletScreenPreview(walletUiState)
}

@Preview
@Composable
fun EmptyTransactionsWalletScreenPreview() {
    val wallet = Wallet(
        name = "Preview wallet",
        currency = Currency.getInstance(Locale.US),
        balance = BigDecimal("100")
    )

    WalletScreenPreview(WalletUiState.Loaded(wallet, TransactionsUiState.Empty))
}

@Preview
@Composable
fun LoadingTransactionsWalletScreenPreview() {
    val wallet = Wallet(
        name = "Preview wallet",
        currency = Currency.getInstance(Locale.US),
        balance = BigDecimal("100")
    )

    WalletScreenPreview(WalletUiState.Loaded(wallet, TransactionsUiState.Loading))
}

@Composable
private fun WalletScreenPreview(uiState: WalletUiState) {
    AppTheme {
        WalletScreen(
            walletUiState = uiState,
            transactionFilterUiState = TransactionFilterUiState(
                minDate = null,
                maxDate = null,
                minValue = null,
                maxValue = null,
                ordering = TransactionOrdering.DateDesc,
            ),
            onBackClicked = {},
            onNavigateToWalletNotFound = {},
            onWalletSettingsClicked = {},
            onAddTransactionClicked = {},
            onTransactionSelected = {},
            onClearSelectedTransactions = {},
            onDeleteSelectedTransactions = {},
            onUpdateTransactionOrder = {},
            onUpdateStartDateSearch = {},
            onUpdateEndDateSearch = {},
            onUpdateMinValueSearch = {},
            onUpdateMaxValueSearch = {},
            onFilterButtonClicked = {},
            onDeleteModeClicked = {},
        )
    }
}

@Preview
@Composable
fun TransactionFilterScreenPreview() {
    val state = TransactionFilterUiState(
        minDate = null,
        maxDate = null,
        minValue = null,
        maxValue = null,
        ordering = TransactionOrdering.DateDesc,
    )

    AppTheme {
        TransactionFilterScreen(
            wallet = Wallet("", currency = Currency.getInstance(Locale.US)),
            transactionFilterUiState = state,
            onUpdateTransactionOrder = {},
            onUpdateStartDate = {},
            onUpdateEndDate = {},
            onUpdateMinValue = {},
            onUpdateMaxValue = {},
            onApplyButtonClicked = {}
        )
    }
}