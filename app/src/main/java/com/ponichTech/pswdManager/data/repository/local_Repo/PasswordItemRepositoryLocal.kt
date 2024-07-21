package com.ponichTech.pswdManager.data.repository.local_Repo

import android.app.Application
import com.ponichTech.pswdManager.data.local_db.PasswordItemDao
import com.ponichTech.pswdManager.data.local_db.PasswordItemDataBase
import com.ponichTech.pswdManager.data.model.PasswordItem

class PasswordItemRepositoryLocal(application: Application) {

    private var passwordItemDao: PasswordItemDao?

    init{
        val db = PasswordItemDataBase.getDataBase((application.applicationContext))
        passwordItemDao = db.itemsDao()
    }

    fun getPassItems() = passwordItemDao?.getPassItems()

    suspend fun addPassItem(passItem: PasswordItem){
            passwordItemDao?.addPassItem(passItem)
    }
    suspend fun deletePassItem(passItem: PasswordItem){
            passwordItemDao?.deletePassItem(passItem)
    }
    suspend fun deleteAllItems(){
            passwordItemDao?.deleteAllPassItems()
    }
    suspend fun getPassItem(id: Int) = passwordItemDao?.getPassItem(id)
}