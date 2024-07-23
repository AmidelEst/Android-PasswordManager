package com.ponichTech.pswdManager.data.repository.passwords_repository

import androidx.lifecycle.LiveData
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.utils.Resource

// interface for managing password items.
interface PasswordsRepository {

    suspend fun addPassword(item: PasswordItem):Resource<*>
    suspend fun updatePassword(item: PasswordItem):Resource<Unit>
    suspend fun deletePassword(item: PasswordItem): Resource<Unit>
    suspend fun getPassword(id: String): Resource<PasswordItem>

    fun getPasswordsLiveData(userId:String): LiveData<Resource<List<PasswordItem>>>
}