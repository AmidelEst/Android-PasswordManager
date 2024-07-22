package com.ponichTech.pswdManager.data.repository.interfaces

import com.ponichTech.pswdManager.data.model.User
import com.ponichTech.pswdManager.utils.Resource


interface UserRepository {


    suspend fun getCurrentUser() : Resource<User>
    suspend fun login(email:String, password:String) : Resource<User>
    suspend fun createUser(userName:String,
                           userEmail:String,
                           userPhone:String,
                           userLoginPass:String) : Resource<User>
    fun logout()
}