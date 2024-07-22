package com.ponichTech.pswdManager.ui.items.all_password_items

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.data.model.User
import com.ponichTech.pswdManager.data.repository.firebase.PasswordFirebaseRepository
import com.ponichTech.pswdManager.data.repository.interfaces.PasswordsRepository
import com.ponichTech.pswdManager.data.repository.interfaces.UserRepository
import com.ponichTech.pswdManager.data.repository.local_Repo.PasswordLocalRepository
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
            if (result is Resource.Success) {
                result.data?.let { fetchPasswordItems(it.userId) }
                _currentUser.value = result
            } else {
                _currentUser.value = Resource.Error("Failed to fetch user")
            }
        }
    }

    fun fetchPasswordItems(userId: String) {
        _passwordItems.value = Resource.Loading()
        syncPasswords( userId)
        localRepository.getPasswordsLiveData(userId).observeForever { resource ->
            _passwordItems.value = resource
        }
    }

    fun addPasswordItem(passwordItem: PasswordItem) {
        viewModelScope.launch {
            _operationStatus.value = Resource.Loading()
            val localResult = safeCall { localRepository.addPassword(passwordItem) }
            if (localResult is Resource.Success) {
                val updatedList = _passwordItems.value?.data?.toMutableList() ?: mutableListOf()
                updatedList.add(passwordItem)
                _passwordItems.value = Resource.Success(updatedList)

                val firebaseResult = safeCall { firebaseRepository.addPassword(localResult.data as PasswordItem) }
                _operationStatus.value = Resource.Success(Unit)
            } else {
                _operationStatus.value = Resource.Error("")
            }
        }
    }

    fun updatePasswordItem(passwordItem: PasswordItem) {
        viewModelScope.launch {
            _operationStatus.value = Resource.Loading()
            val localResult = safeCall { localRepository.updatePassword(passwordItem) }
            if (localResult is Resource.Success) {
                val updatedList = _passwordItems.value?.data?.toMutableList()?.map {
                    if (it.id == passwordItem.id) passwordItem else it
                } ?: listOf(passwordItem)
                _passwordItems.value = Resource.Success(updatedList)

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
                val updatedList = _passwordItems.value?.data?.toMutableList()?.apply {
                    remove(passwordItem)
                } ?: listOf()
                _passwordItems.value = Resource.Success(updatedList)

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
    private fun syncPasswords(userId: String) {
        viewModelScope.launch {
            _operationStatus.value = Resource.Loading()

            // Fetch local passwords
            val localPasswordsResource = localRepository.getPasswordsLiveData(userId)
            val localPasswordsLiveData = localPasswordsResource.switchMap { resource ->
                val result = MutableLiveData<List<PasswordItem>>()
                result.value = resource.data ?: emptyList() // Handle null case
                result
            }

            // Fetch remote passwords
            val remotePasswordsLiveData = firebaseRepository.getPasswordsLiveData(userId)

            // Observe both LiveData objects simultaneously
            val mediatorLiveData = MediatorLiveData<Pair<List<PasswordItem>?, List<PasswordItem>?>>()

            mediatorLiveData.addSource(localPasswordsLiveData) { localPasswords ->
                mediatorLiveData.value = Pair(localPasswords, mediatorLiveData.value?.second)
            }

            mediatorLiveData.addSource(remotePasswordsLiveData) { remoteResource ->
                if (remoteResource is Resource.Success) {
                    mediatorLiveData.value = Pair(mediatorLiveData.value?.first, remoteResource.data)
                } else {
                    _operationStatus.value = Resource.Error("Failed to fetch remote passwords")
                }
            }

            mediatorLiveData.observeForever { (localPasswords, remotePasswords) ->
                if (localPasswords != null && remotePasswords != null) {
                    // Check for any remote password items not in local
                    val localPasswordIds = localPasswords.map { it.id }.toSet()
                    val missingPasswords = remotePasswords.filter { it.id !in localPasswordIds }

                    // Add missing passwords to local database
                    missingPasswords.forEach { missingPassword ->
                        viewModelScope.launch {
                            localRepository.addPassword(missingPassword)
                        }
                    }

                    // Update LiveData to notify UI
                    val updatedList = localPasswords.toMutableList().apply { addAll(missingPasswords) }
                    _passwordItems.value = Resource.Success(updatedList)

                    // Clear sources to avoid memory leaks
                    mediatorLiveData.removeSource(localPasswordsLiveData)
                    mediatorLiveData.removeSource(remotePasswordsLiveData)
                }
            }

            _operationStatus.value = Resource.Success(Unit)
        }
    }


}

class PasswordsViewModelFactory(
    private val application: Application,
    private val userRepository: UserRepository,
    private val firebaseRepository: PasswordFirebaseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PasswordsViewModel::class.java)) {
            val localRepository = PasswordLocalRepository.getInstance(application.applicationContext)
            @Suppress("UNCHECKED_CAST")
            return PasswordsViewModel(userRepository, localRepository, firebaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}


