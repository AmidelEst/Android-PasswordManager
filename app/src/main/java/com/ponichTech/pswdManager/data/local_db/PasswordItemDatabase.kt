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
        @Volatile
        private var INSTANCE: PasswordItemDatabase? = null

        fun getDatabase(context: Context): PasswordItemDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PasswordItemDatabase::class.java,
                    "password_item_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}