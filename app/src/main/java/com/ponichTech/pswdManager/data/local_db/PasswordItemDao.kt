package com.ponichTech.pswdManager.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.ponichTech.pswdManager.data.model.PasswordItem

@Dao
interface PasswordItemDao {
    @Upsert
    suspend fun addPassword(item: PasswordItem)

    @Delete
    suspend fun deletePassword(vararg items: PasswordItem)

    @Update
    suspend fun updatePassword(item: PasswordItem)

    @Query("SELECT * FROM password_items WHERE userId = :userId order by serviceName ASC")
    fun getPasswordsLiveData(userId: String):LiveData<List<PasswordItem>>

    @Query("SELECT * from password_items where id like :id")
    suspend fun getPassword(id:String): PasswordItem

    @Query("DELETE from password_items")
    suspend fun deleteAllPasswords()
}