package com.ponichTech.pswdManager.ui.users.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ponichTech.pswdManager.data.model.User
import com.ponichTech.pswdManager.data.repository.user_repository.UserRepository
import com.ponichTech.pswdManager.data.repository.user_repository.UserRepositoryFirebase
import com.ponichTech.pswdManager.ui.passwords.all_passwords.PasswordsViewModel
import com.ponichTech.pswdManager.utils.Resource
import com.ponichTech.pswdManager.utils.safeCall
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _currentUser = MutableLiveData<Resource<User>>()
    val currentUser: LiveData<Resource<User>> get() = _currentUser

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _currentUser.value = Resource.Loading()
            val result = safeCall { userRepository.login(email, password) }
            _currentUser.value = result
        }
    }
    class Factory(private val userRepository: UserRepositoryFirebase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}