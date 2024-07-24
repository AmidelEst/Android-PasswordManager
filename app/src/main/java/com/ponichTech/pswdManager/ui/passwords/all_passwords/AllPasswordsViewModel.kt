package com.ponichTech.pswdManager.ui.passwords.all_passwords

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.data.model.User
import com.ponichTech.pswdManager.data.repository.passwords_repository.PasswordFirebaseRepository
import com.ponichTech.pswdManager.data.repository.passwords_repository.PasswordLocalRepository
import com.ponichTech.pswdManager.data.repository.passwords_repository.PasswordsRepository
import com.ponichTech.pswdManager.data.repository.auth_repository_firebase.AuthRepository
import com.ponichTech.pswdManager.utils.Resource
import com.ponichTech.pswdManager.utils.SharedPreferencesUtil
import com.ponichTech.pswdManager.utils.safeCall
import kotlinx.coroutines.launch

class AllPasswordsViewModel(
    private val authRepository: AuthRepository,
    private val localRepository: PasswordsRepository,
    private val remoteRepository: PasswordsRepository
) : ViewModel() {

    private var _userId:String?=null

    private val _selectedPasswordItem = MutableLiveData<PasswordItem?>()
    val selectedPasswordItem: LiveData<PasswordItem?> get() = _selectedPasswordItem

    private val _currentUser = MutableLiveData<Resource<User>>()
    val currentUser: LiveData<Resource<User>> get() = _currentUser

    private val _passwordItems = MutableLiveData<Resource<List<PasswordItem>>>()
    val passwordItems: LiveData<Resource<List<PasswordItem>>> get() = _passwordItems

    private val _operationStatus = MutableLiveData<Resource<Unit>>()

    init {
        fetchCurrentUser()
    }

    fun selectPasswordItem(item: PasswordItem?) {
        _selectedPasswordItem.value = item
    }

    private fun fetchCurrentUser() {
        if(_userId==null){
            _currentUser.value = Resource.Error("login or register")
        }
        viewModelScope.launch {
            _currentUser.value = Resource.Loading()
            val result = authRepository.getCurrentUser()
            if (result is Resource.Success) {
                result.data?.let {
                    _userId = result.data.userId
                    fetchPasswordItems(_userId!!)
                }
                _currentUser.value = result
            } else {
                _currentUser.value = Resource.Error("Failed to fetch user")
            }
        }
    }
    fun fetchPasswordItems(userId: String) {
        _passwordItems.value = Resource.Loading()
//        syncPasswords(userId)
//        localRepository.getPasswordsLiveData(userId).observeForever { resource ->
//            _passwordItems.value = resource
//        }
        remoteRepository.getPasswordsLiveData(userId).observeForever { resource ->
            _passwordItems.value = resource
        }
    }

    fun addPasswordItem(passwordItem: PasswordItem) {
        viewModelScope.launch {
            _operationStatus.value = Resource.Loading()

            // Step 1: Generate ID from FireStore and create the item with this ID
            val remoteResult = remoteRepository.addPassword(passwordItem)

            if (remoteResult is Resource.Success) {
                // Step 2: Retrieve the newly created item with its FireStore ID
                val createdItem = remoteResult.data as PasswordItem

                // Step 3: Save the item locally in Room
                val localResult = localRepository.addPassword(createdItem)

                if (localResult is Resource.Success) {
                    // Update the local list and UI
                    val updatedList = _passwordItems.value?.data?.toMutableList() ?: mutableListOf()
                    updatedList.add(createdItem)
                    _passwordItems.value = Resource.Success(updatedList)
                    _operationStatus.value = Resource.Success(Unit)
                } else {
                    _operationStatus.value = Resource.Error("Failed to save item locally")
                }
            } else {
                _operationStatus.value = Resource.Error("Failed to create item remotely")
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

                val firebaseResult = safeCall { remoteRepository.updatePassword(passwordItem) }
                _operationStatus.value = firebaseResult
            } else {
                _operationStatus.value = localResult
            }
        }
    }
    fun logoutUser(context: Context) {
        viewModelScope.launch {
            SharedPreferencesUtil.updateLoginState(context, false)
            authRepository.logout()
            _currentUser.value = Resource.Error("User logged out")
            _passwordItems.value = Resource.Error("User logged out")
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

                val firebaseResult = safeCall { remoteRepository.deletePassword(passwordItem) }
                _operationStatus.value = firebaseResult
            } else {
                _operationStatus.value = localResult
            }
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
            val remotePasswordsLiveData = remoteRepository.getPasswordsLiveData(userId)

            // Observe both LiveData objects simultaneously
            val mediatorLiveData =
                MediatorLiveData<Pair<List<PasswordItem>?, List<PasswordItem>?>>()

            mediatorLiveData.addSource(localPasswordsLiveData) { localPasswords ->
                mediatorLiveData.value = Pair(localPasswords, mediatorLiveData.value?.second)
            }

            mediatorLiveData.addSource(remotePasswordsLiveData) { remoteResource ->
                if (remoteResource is Resource.Success) {
                    mediatorLiveData.value =
                        Pair(mediatorLiveData.value?.first, remoteResource.data)
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
                    val updatedList =
                        localPasswords.toMutableList().apply { addAll(missingPasswords) }
                    _passwordItems.value = Resource.Success(updatedList)

                    // Clear sources to avoid memory leaks
                    mediatorLiveData.removeSource(localPasswordsLiveData)
                    mediatorLiveData.removeSource(remotePasswordsLiveData)
                }
            }

            _operationStatus.value = Resource.Success(Unit)
        }
    }

    class Factory(
        private val application: Application,
        private val authRepository: AuthRepository,
        private val firebaseRepository: PasswordFirebaseRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AllPasswordsViewModel::class.java)) {
                val localRepository =
                    PasswordLocalRepository.getInstance(application.applicationContext)
                @Suppress("UNCHECKED_CAST")
                return AllPasswordsViewModel(authRepository, localRepository, firebaseRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


