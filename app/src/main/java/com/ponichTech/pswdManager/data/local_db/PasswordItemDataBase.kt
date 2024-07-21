package com.ponichTech.pswdManager.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ponichTech.pswdManager.data.model.PasswordItem


@Database(entities = [PasswordItem::class], version = 1, exportSchema = false)
abstract class PasswordItemDataBase:RoomDatabase() {

    abstract  fun itemsDao(): PasswordItemDao

    companion object{
        @Volatile //  variable to ensure instance is always up-to-date and visible to all threads
        private var instance: PasswordItemDataBase? = null
        // get a singleton instance of the database
        fun getDataBase(context:Context)= instance ?: synchronized(this){
                        Room.databaseBuilder(context.applicationContext,
                            PasswordItemDataBase::class.java,"password_items")
                            .build()
        }
    }
}