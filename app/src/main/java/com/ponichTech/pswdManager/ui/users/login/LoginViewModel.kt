package com.ponichTech.pswdManager.ui.users.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ponichTech.pswdManager.data.repository.auth_repository_firebase.AuthRepository

import com.ponichTech.pswdManager.ui.passwords.all_passwords.AllPasswordsViewModel


class LoginViewModel(private val userRepository: AuthRepository) : ViewModel() {

    fun loginUser(email: String, password: String, allPasswordsViewModel: AllPasswordsViewModel) {
        allPasswordsViewModel.loginUser(email, password)
    }

    class Factory(private val authRepository: AuthRepository) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return  LoginViewModel(authRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}