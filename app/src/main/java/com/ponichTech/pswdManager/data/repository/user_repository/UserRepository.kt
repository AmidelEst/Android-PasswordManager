package com.ponichTech.pswdManager.data.repository.user_repository

import com.ponichTech.pswdManager.data.model.User
import com.ponichTech.pswdManager.utils.Resource


interface UserRepository {

    suspend fun registerUser(userName:String,
                             userEmail:String,
                             userPhone:String,
                             userLoginPass:String) : Resource<User>
    suspend fun login(email:String, password:String) : Resource<User>
    suspend fun getCurrentUser() : Resource<User>
    fun logout()
}