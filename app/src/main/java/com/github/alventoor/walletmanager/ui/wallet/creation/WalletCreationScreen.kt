package com.github.alventoor.walletmanager.ui.wallet.creation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.alventoor.walletmanager.ui.component.*
import com.github.alventoor.walletmanager.R
import com.github.alventoor.walletmanager.core.util.listAllCountryCurrencies
import com.github.alventoor.walletmanager.ui.theme.AppTheme
import com.github.alventoor.walletmanager.ui.util.DecimalDigitsInputFilter
import java.math.BigDecimal
import java.util.*

@Composable
fun WalletCreationScreen(
    onBackClicked: () -> Unit,
    onNavigateToWallet: (walletId: Long) -> Unit,
    viewModel: WalletCreationViewModel
) {
    val navigateToWallet by viewModel.navigateToWallet.collectAsStateWithLifecycle()

    LaunchedEffect(navigateToWallet) {
        val walletId = navigateToWallet
        if (walletId != null) {
            onNavigateToWallet(walletId)
            viewModel.onCreateWalletNavigated()
        }
    }

    val walletCreationUiState by viewModel.walletCreationUiState.collectAsStateWithLifecycle()

    WalletCreationScreen(
        walletCreationUiState = walletCreationUiState,
        onBackClicked = onBackClicked,
        onWalletNameUpdated = viewModel::setWalletName,
        onWalletCurrencySelected = viewModel::setWalletCurrency,
        onWalletBalanceUpdated = viewModel::setWalletBalance,
        onWalletCreationClicked = viewModel::onCreateWallet
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WalletCreationScreen(
    walletCreationUiState: WalletCreationUiState,
    onBackClicked: () -> Unit,
    onWalletNameUpdated: (String) -> Unit,
    onWalletCurrencySelected: (Currency) -> Boolean,
    onWalletBalanceUpdated: (String) -> Unit,
    onWalletCreationClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = stringResource(R.string.wallet_creation_screen_title),
                icon = Icons.Filled.ArrowBack,
                iconDescription = stringResource(R.string.wallet_creation_screen_navigate_up_description),
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
            Column(
                modifier = Modifier.padding(cardContentPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FlowRow(
                    modifier = Modifier.width(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    maxItemsInEachRow = 2
                ) {
                    val inputFilter = DecimalDigitsInputFilter(walletCreationUiState.currency.defaultFractionDigits)
                    var textBalance: String by rememberSaveable { mutableStateOf("") }

                    LaunchedEffect(textBalance) {
                        onWalletBalanceUpdated(textBalance)
                    }

                    AppTextField(
                        modifier = Modifier.width(IntrinsicSize.Min),
                        value = walletCreationUiState.name,
                        label = stringResource(R.string.wallet_creation_screen_wallet_name_hint),
                        onValueChange = onWalletNameUpdated,
                        onClearButtonClick = { onWalletNameUpdated("") },
                        clearButtonContentDescription = stringResource(R.string.wallet_creation_screen_wallet_name_clear_button_description)
                    )

                    AppDropDownList(
                        modifier = Modifier.width(IntrinsicSize.Min),
                        items = listAllCountryCurrencies(),
                        selectedItem = walletCreationUiState.currency,
                        itemDescription = {
                            val displayName = it.displayName
                            "${
                                displayName.substring(0..0).uppercase()
                            }${displayName.substring(1 until displayName.length)}"
                        },
                        onItemSelected = {
                            val balanceIsInvalid = onWalletCurrencySelected(it)

                            if (balanceIsInvalid)
                                textBalance = ""
                        },
                        label = stringResource(R.string.wallet_creation_screen_wallet_currency_hint)
                    )

                    AppTextField(
                        modifier = Modifier.width(IntrinsicSize.Min),
                        value = textBalance,
                        label = stringResource(R.string.wallet_creation_screen_wallet_balance_hint),
                        onValueChange = { if (inputFilter.matches(it)) textBalance = it },
                        onClearButtonClick = { textBalance = "" },
                        clearButtonContentDescription = stringResource(R.string.wallet_creation_screen_wallet_balance_clear_button_description),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    modifier = Modifier.padding(top = 32.dp),
                    onClick = onWalletCreationClicked,
                    enabled = walletCreationUiState.isValid
                ) {
                    ButtonText(stringResource(R.string.create_action))
                }
            }
        }
    }
}

@Composable
@Preview
private fun WalletCreationScreenPreview() {
    val state = WalletCreationUiState(
        name = "",
        currency = Currency.getInstance(Locale.getDefault()),
        balance = BigDecimal.ZERO
    )

    AppTheme {
        WalletCreationScreen(
            walletCreationUiState = state,
            onBackClicked = {},
            onWalletNameUpdated = {},
            onWalletCurrencySelected = { false },
            onWalletBalanceUpdated = {},
            onWalletCreationClicked = {}
        )
    }
}