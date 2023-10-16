package com.github.alventoor.walletmanager.core.repository

import com.github.alventoor.walletmanager.core.model.Wallet
import kotlinx.coroutines.flow.Flow

interface WalletsRepository {
    fun getWallets(): Flow<List<Wallet>>

    fun getWallet(walletId: Long): Flow<Wallet?>

    /**
     * Insert a new wallet inside the database.
     *
     * @return the newly inserted wallet with its database id.
     */
    suspend fun insertWallet(wallet: Wallet): Wallet

    suspend fun updateWalletName(wallet: Wallet, walletName: String)

    suspend fun deleteWallet(wallet: Wallet)

    suspend fun deleteWallets(wallets: List<Wallet>)
}