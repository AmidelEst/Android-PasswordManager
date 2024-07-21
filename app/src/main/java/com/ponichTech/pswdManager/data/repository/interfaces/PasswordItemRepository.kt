package com.ponichTech.pswdManager.data.repository.interfaces

import androidx.lifecycle.MutableLiveData
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.utils.Resource

// Interface defining the operations for managing PasswordItem data
interface PasswordItemRepository {

        // Sus function - add password item, returning the result wrapped in a Resource
        suspend fun addPassItem(passItem: PasswordItem): Resource<PasswordItem>

        // Sus function - delete password by its ID, returning the result wrapped in a Resource
        suspend fun deletePassItem(passId: String): Resource<Void>

        // Sus function - retrieve password by its ID, returning the result wrapped in a Resource
        suspend fun getPassItem(passId: String): Resource<PasswordItem>

        // Sus function - retrieve all password items, returning the result wrapped in a Resource
        suspend fun getPassItems(): Resource<List<PasswordItem>>

        // Function to retrieve password items and post the result to the given LiveData
        fun getPassItemLiveData(data: MutableLiveData<Resource<List<PasswordItem>>>)

        suspend fun updatePassword(password: PasswordItem)
}