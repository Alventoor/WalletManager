package com.tutamail.julienm99.walletmanager.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

private const val DATABASE_NAME: String = "wallet_manager_database"

@Database(entities = [WalletEntity::class, TransactionEntity::class, TransactionCategoryEntity::class], version = 1, exportSchema = false)
abstract class WalletManagerDatabase: RoomDatabase() {
    abstract fun walletManagerDao(): WalletManagerDao

    companion object {
        @Volatile
        private var INSTANCE: WalletManagerDatabase? = null

        fun getInstance(context: Context): WalletManagerDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room
                        .databaseBuilder(context.applicationContext, WalletManagerDatabase::class.java, DATABASE_NAME)
                        .addCallback(CALLBACK_WALLET_TRANSACTION)
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }

        val CALLBACK_WALLET_TRANSACTION = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                db.execSQL("""
                    CREATE TRIGGER IF NOT EXISTS transaction_added AFTER INSERT ON wallet_transaction
                    BEGIN
                    UPDATE wallet
                    SET balance = balance + new.value
                    WHERE id = new.wallet_id;
                    END
                """.trimIndent())

                db.execSQL("""
                    CREATE TRIGGER IF NOT EXISTS transaction_updated AFTER UPDATE ON wallet_transaction
                    BEGIN
                    UPDATE wallet
                    SET balance = balance + (new.value - old.value)
                    WHERE id = new.wallet_id;
                    END
                """.trimIndent())

                db.execSQL("""
                    CREATE TRIGGER IF NOT EXISTS transaction_deleted AFTER DELETE ON wallet_transaction
                    BEGIN
                    UPDATE wallet
                    SET balance = balance - old.value
                    WHERE id = old.wallet_id;
                    END
                """.trimIndent())
            }
        }
    }
}