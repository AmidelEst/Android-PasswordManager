package com.ponichTech.pswdManager.data.repository.interfaces

import androidx.lifecycle.LiveData
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.utils.Resource

// Interface defining the operations for managing PasswordItem data
interface PasswordsRepository {

    suspend fun addPassword(item: PasswordItem):Resource<Unit>
    suspend fun updatePassword(item: PasswordItem):Resource<Unit>
    suspend fun deletePassword(item: PasswordItem): Resource<Unit>
    suspend fun getPassword(id: String): Resource<PasswordItem>

    fun getPasswordsLiveData(userId:String): LiveData<Resource<List<PasswordItem>>>
}