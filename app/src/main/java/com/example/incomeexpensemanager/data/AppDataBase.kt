package com.example.incomeexpensemanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Transaction::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "TransactionDB"
                ).build().also { INSTANCE = it }
            }
        }
    }
}