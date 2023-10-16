package com.github.alventoor.walletmanager.ui.wallet.creation

import androidx.lifecycle.*
import java.util.Currency
import com.github.alventoor.walletmanager.core.model.Transaction
import com.github.alventoor.walletmanager.core.model.Wallet
import com.github.alventoor.walletmanager.core.repository.TransactionsRepository
import com.github.alventoor.walletmanager.core.repository.WalletsRepository
import com.github.alventoor.walletmanager.core.util.combineState
import com.github.alventoor.walletmanager.core.util.toBigDecimalResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Locale

class WalletCreationViewModel(
    private val startCreditDesc: String,
    private val walletsRepository: WalletsRepository,
    private val transactionsRepository: TransactionsRepository
) : ViewModel() {
    private val _navigateToWallet: MutableStateFlow<Long?> = MutableStateFlow(null)
    val navigateToWallet: StateFlow<Long?>
        get() = _navigateToWallet

    private val walletName: MutableStateFlow<String> = MutableStateFlow("")

    private val walletCurrency: MutableStateFlow<Currency> = MutableStateFlow(Currency.getInstance(Locale.getDefault()))

    private val walletBalance: MutableStateFlow<BigDecimal> = MutableStateFlow(BigDecimal.ZERO)

    val walletCreationUiState: StateFlow<WalletCreationUiState> = combineState(walletName, walletCurrency, walletBalance) { name, currency, balance ->
        WalletCreationUiState(name, currency, balance)
    }

    fun setWalletName(name: String) {
        walletName.value = name
    }

    /**
     * Set the wallet currency. The wallet balance is set to 0 if it's number of fraction
     * digits exceeds the currency's number.
     *
     * @param currency the currency of the wallet.
     *
     * @return true if the wallet balance has been reset, false otherwise.
     */
    fun setWalletCurrency(currency: Currency): Boolean {
        walletCurrency.value = currency

        val invalidBalance = walletBalance.value.scale() > currency.defaultFractionDigits

        if (invalidBalance)
            walletBalance.value = BigDecimal.ZERO

        return invalidBalance
    }

    /**
     * Set the wallet balance. If the string is empty, the balance is set to zero. If it's
     * not a valid representation of a number, no change occur.
     *
     * @param balance the balance of the wallet.
     */
    fun setWalletBalance(balance: String) {
        walletBalance.value = when (balance) {
            "" -> BigDecimal.ZERO
            else -> balance.toBigDecimalResult().getOrElse { return }
        }
    }

    fun onCreateWallet() {
        viewModelScope.launch {
            val wallet = walletsRepository.insertWallet(Wallet(walletName.value, walletCurrency.value))

            if (walletBalance.value != BigDecimal.ZERO) {
                val startTransaction = Transaction(wallet.id, wallet.currency, walletBalance.value, description = startCreditDesc)
                transactionsRepository.insertTransaction(startTransaction)
            }

            _navigateToWallet.value = wallet.id
        }
    }

    fun onCreateWalletNavigated() {
        _navigateToWallet.value = null
    }
}

data class WalletCreationUiState(val name: String, val currency: Currency, val balance: BigDecimal) {
    val isValid: Boolean = name.isNotBlank()
}