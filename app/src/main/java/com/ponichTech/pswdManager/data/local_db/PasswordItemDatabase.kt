package com.ponichTech.pswdManager.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ponichTech.pswdManager.data.model.PasswordItem

@Database(entities = [PasswordItem::class], version = 1, exportSchema = false)
abstract class PasswordItemDatabase : RoomDatabase() {

    abstract fun passwordItemDao(): PasswordItemDao

    companion object {
        private const val DATABASE_NAME = "password_item_database"

        @Volatile
        private var INSTANCE: PasswordItemDatabase? = null

        fun getInstance(context: Context): PasswordItemDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PasswordItemDatabase::class.java,
                    DATABASE_NAME
                ).build().also { INSTANCE = it }
            }
    }
}
