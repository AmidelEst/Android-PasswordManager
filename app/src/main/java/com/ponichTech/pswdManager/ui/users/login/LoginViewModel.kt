package com.ponichTech.pswdManager.ui.users.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ponichTech.pswdManager.data.repository.interfaces.UserRepository
import com.ponichTech.pswdManager.ui.items.all_password_items.PasswordsViewModel

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun loginUser(email: String, password: String, passwordsViewModel: PasswordsViewModel) {
        passwordsViewModel.loginUser(email, password)
    }

    class Factory(private val userRepository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return  LoginViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}