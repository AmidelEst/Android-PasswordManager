package com.ponichTech.pswdManager.data.repository.local_Repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.ponichTech.pswdManager.data.local_db.PasswordItemDao
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.data.repository.interfaces.PasswordsRepository
import com.ponichTech.pswdManager.utils.Resource

class PasswordLocalRepository(private val passwordItemDao: PasswordItemDao) : PasswordsRepository {

    override suspend fun getPassword(id: String): Resource<PasswordItem> {
        return Resource.Success(passwordItemDao.getPassword(id))
    }

    override suspend fun addPassword(item: PasswordItem): Resource<Unit> {
        passwordItemDao.addPassword(item)
        return Resource.Success(Unit)
    }

    override fun getPasswordsLiveData(userId: String): LiveData<Resource<List<PasswordItem>>> {
        return passwordItemDao.getPasswordsLiveData(userId).map { Resource.Success(it) }
    }


    override suspend fun updatePassword(item: PasswordItem): Resource<Unit> {
        passwordItemDao.updatePassword(item)
        return Resource.Success(Unit)
    }

    override suspend fun deletePassword(item: PasswordItem): Resource<Unit> {
        passwordItemDao.deletePassword(item)
        return Resource.Success(Unit)
    }

}
