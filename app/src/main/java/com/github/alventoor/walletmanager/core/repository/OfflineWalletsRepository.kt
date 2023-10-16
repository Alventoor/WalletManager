package com.github.alventoor.walletmanager.core.repository

import com.github.alventoor.walletmanager.core.database.WalletManagerDao
import com.github.alventoor.walletmanager.core.database.asEntity
import com.github.alventoor.walletmanager.core.database.asModel
import com.github.alventoor.walletmanager.core.model.Wallet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineWalletsRepository(private val database: WalletManagerDao): WalletsRepository {
    override fun getWallets(): Flow<List<Wallet>> {
        return database.getWallets().map { it.asModel() }
    }

    override fun getWallet(walletId: Long): Flow<Wallet?> {
        return database.getWallet(walletId).map { it?.asModel() }
    }

    override suspend fun insertWallet(wallet: Wallet): Wallet {
        val walletId = database.insertWallet(wallet.asEntity())

        return wallet.copy(id = walletId)
    }

    override suspend fun updateWalletName(wallet: Wallet, walletName: String) {
        database.updateWalletName(wallet.id, walletName)
    }

    override suspend fun deleteWallet(wallet: Wallet) {
        database.deleteWallet(wallet.asEntity())
    }

    override suspend fun deleteWallets(wallets: List<Wallet>) {
        database.deleteWallets(wallets.map { it.id })
    }
}