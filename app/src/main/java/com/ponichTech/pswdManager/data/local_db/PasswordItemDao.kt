package com.ponichTech.pswdManager.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ponichTech.pswdManager.data.model.PasswordItem

@Dao
interface PasswordItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPassItem(item: PasswordItem)

    @Delete
    suspend fun deletePassItem(vararg items: PasswordItem)

    @Update
    suspend fun updatePassItem(item: PasswordItem)

    @Query("SELECT * from password_items order by serviceName ASC")
    fun getPassItems():LiveData<List<PasswordItem>>

    @Query("SELECT * from password_items where id like :id")
    suspend fun getPassItem(id:Int): PasswordItem

    @Query("DELETE from password_items")
    suspend fun deleteAllPassItems()
}