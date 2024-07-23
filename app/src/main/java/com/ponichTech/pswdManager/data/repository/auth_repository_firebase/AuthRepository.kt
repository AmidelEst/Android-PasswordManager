package com.ponichTech.pswdManager.data.repository.auth_repository_firebase

import com.ponichTech.pswdManager.data.model.User
import com.ponichTech.pswdManager.utils.Resource


interface AuthRepository {

    suspend fun registerUser(userName:String,
                             userEmail:String,
                             userPhone:String,
                             userLoginPass:String) : Resource<User>
    suspend fun login(email:String, password:String) : Resource<User>
    suspend fun getCurrentUser() : Resource<User>
    fun logout()
    suspend fun updateUser(user: User): Resource<User>
}