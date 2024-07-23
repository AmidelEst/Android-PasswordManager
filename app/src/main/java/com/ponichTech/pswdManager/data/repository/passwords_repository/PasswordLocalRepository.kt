package com.ponichTech.pswdManager.data.repository.passwords_repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.ponichTech.pswdManager.data.local_db.PasswordItemDao
import com.ponichTech.pswdManager.data.local_db.PasswordItemDatabase
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.utils.Resource

class PasswordLocalRepository(private val passwordItemDao: PasswordItemDao) : PasswordsRepository {

    companion object {
        @Volatile
        private var instance: PasswordLocalRepository? = null

        fun getInstance(context: Context): PasswordLocalRepository =
            instance ?: synchronized(this) {
                instance ?: PasswordLocalRepository(
                    PasswordItemDatabase.getInstance(context).passwordItemDao()
                ).also { instance = it }
            }
    }


    override suspend fun addPassword(item: PasswordItem): Resource<PasswordItem> {
        passwordItemDao.addPassword(item)
        return Resource.Success(item)
    }

    override fun getPasswordsLiveData(userId: String): LiveData<Resource<List<PasswordItem>>> {
        return passwordItemDao.getPasswordsLiveData(userId).switchMap { passwords ->
            val result = MutableLiveData<Resource<List<PasswordItem>>>()
            result.value = Resource.Success(passwords)
            result
        }
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
