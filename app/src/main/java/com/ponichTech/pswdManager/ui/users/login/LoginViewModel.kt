package com.ponichTech.pswdManager.ui.users.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ponichTech.pswdManager.data.model.User
import com.ponichTech.pswdManager.data.repository.user_repository.UserRepository
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
            _currentUser.value =  userRepository.login(email, password)
        }
    }
}