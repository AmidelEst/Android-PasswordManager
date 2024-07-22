package com.ponichTech.pswdManager.ui.items.all_password_items

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.data.model.User
import com.ponichTech.pswdManager.data.repository.interfaces.PasswordsRepository
import com.ponichTech.pswdManager.data.repository.interfaces.UserRepository
import com.ponichTech.pswdManager.utils.Resource
import com.ponichTech.pswdManager.utils.safeCall
import kotlinx.coroutines.launch

class PasswordsViewModel(
    private val userRepository: UserRepository,
    private val localRepository: PasswordsRepository,
    private val firebaseRepository: PasswordsRepository
) : ViewModel() {

    private val _selectedPasswordItem = MutableLiveData<PasswordItem?>()
    val selectedPasswordItem: LiveData<PasswordItem?> get() = _selectedPasswordItem

    private val _currentUser = MutableLiveData<Resource<User>>()
    val currentUser: LiveData<Resource<User>> get() = _currentUser

    private val _passwordItems = MutableLiveData<Resource<List<PasswordItem>>>()
    val passwordItems: LiveData<Resource<List<PasswordItem>>> get() = _passwordItems

    private val _operationStatus = MutableLiveData<Resource<Unit>>()
    val operationStatus: LiveData<Resource<Unit>> get() = _operationStatus

    init {
        fetchCurrentUser()
    }

    fun selectPasswordItem(item: PasswordItem?) {
        _selectedPasswordItem.value = item
    }

    private fun fetchCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = Resource.Loading()
            val result = userRepository.getCurrentUser()
            _currentUser.value = result

            if (result is Resource.Success) {
                result.data?.let { fetchPasswordItems(it.userId) }
            } else {
                _passwordItems.value = Resource.Error("Failed to fetch user")
            }
        }
    }

    fun fetchPasswordItems(userId: String) {
        _passwordItems.value = Resource.Loading()
        localRepository.getPasswordsLiveData(userId).observeForever { resource ->
            _passwordItems.value = resource
        }
    }

    fun addPasswordItem(passwordItem: PasswordItem) {
        viewModelScope.launch {
            _operationStatus.value = Resource.Loading()
            val localResult = safeCall { localRepository.addPassword(passwordItem) }
            if (localResult is Resource.Success) {
                val firebaseResult = safeCall { firebaseRepository.addPassword(passwordItem) }
                _operationStatus.value = firebaseResult
            } else {
                _operationStatus.value = localResult
            }
        }
    }

    fun updatePasswordItem(passwordItem: PasswordItem) {
        viewModelScope.launch {
            _operationStatus.value = Resource.Loading()
            val localResult = safeCall { localRepository.updatePassword(passwordItem) }
            if (localResult is Resource.Success) {
                val firebaseResult = safeCall { firebaseRepository.updatePassword(passwordItem) }
                _operationStatus.value = firebaseResult
            } else {
                _operationStatus.value = localResult
            }
        }
    }

    fun deletePasswordItem(passwordItem: PasswordItem) {
        viewModelScope.launch {
            _operationStatus.value = Resource.Loading()
            val localResult = safeCall { localRepository.deletePassword(passwordItem) }
            if (localResult is Resource.Success) {
                val firebaseResult = safeCall { firebaseRepository.deletePassword(passwordItem) }
                _operationStatus.value = firebaseResult
            } else {
                _operationStatus.value = localResult
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _currentUser.value = Resource.Loading()
            val result = userRepository.login(email, password)
            _currentUser.value = result

            if (result is Resource.Success) {
                result.data?.let { fetchPasswordItems(it.userId) }
            } else {
                _passwordItems.value = Resource.Error("Failed to login user")
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            userRepository.logout()
            _currentUser.value = Resource.Error("User logged out")
            _passwordItems.value = Resource.Error("User logged out")
        }
    }

    class PasswordsViewModelFactory(
        private val userRepository: UserRepository,
        private val localRepository: PasswordsRepository,
        private val firebaseRepository: PasswordsRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PasswordsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PasswordsViewModel(userRepository, localRepository, firebaseRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}