package com.ponichTech.pswdManager.ui.passwords.all_passwords

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
import com.ponichTech.pswdManager.data.repository.passwords_repository.PasswordFirebaseRepository
import com.ponichTech.pswdManager.data.repository.passwords_repository.PasswordLocalRepository
import com.ponichTech.pswdManager.data.repository.passwords_repository.PasswordsRepository
import com.ponichTech.pswdManager.data.repository.auth_repository_firebase.AuthRepository
import com.ponichTech.pswdManager.utils.Resource
import com.ponichTech.pswdManager.utils.safeCall
import kotlinx.coroutines.launch

class AllPasswordsViewModel(
    private val authRepository: AuthRepository,
    private val localRepository: PasswordsRepository,
    private val firebaseRepository: PasswordsRepository
) : ViewModel() {

    private val _selectedPasswordItem = MutableLiveData<PasswordItem?>()
    val selectedPasswordItem: LiveData<PasswordItem?> get() = _selectedPasswordItem

    private var _loggedInUser = MutableLiveData<Resource<User>>()
    val loggedInUser: LiveData<Resource<User>> get() = _loggedInUser

    private val _passwordItems = MutableLiveData<Resource<List<PasswordItem>>>()
    val passwordItems: LiveData<Resource<List<PasswordItem>>> get() = _passwordItems

    private val _operationStatus = MutableLiveData<Resource<Unit>>()
    val operationStatus: LiveData<Resource<Unit>> get() = _operationStatus

    fun setLoggedInUser(resource :Resource<User>){
        _loggedInUser.value = resource
        val userId:String = _loggedInUser.value!!.data?.userId.toString()
        fetchPasswordItems(userId)
    }

    init {
        fetchCurrentUser()
    }
    fun selectPasswordItem(item: PasswordItem?) {
        _selectedPasswordItem.value = item
    }

    private fun fetchCurrentUser() {
        viewModelScope.launch {
            _loggedInUser.value = Resource.Loading()
            val result = authRepository.getCurrentUser()
            _loggedInUser.value = result
            val userId = _loggedInUser.value?.data?.userId.toString()
            fetchPasswordItems(userId)

        }
    }

    fun fetchPasswordItems(userId: String) {
        viewModelScope.launch {
            localRepository.getPasswordsLiveData(userId).observeForever { resource ->
//        syncPasswords(userId)
                _passwordItems.value = resource
            }
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

                safeCall { firebaseRepository.addPassword(localResult.data as PasswordItem) }
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
                _operationStatus.value = firebaseRepository.deletePassword(passwordItem)
            } else {
                _operationStatus.value = localResult
            }
        }
    }
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loggedInUser.value = Resource.Loading()
            val result = authRepository.login(email, password)
            _loggedInUser.value = result

            if (result is Resource.Success) {
                result.data?.let { fetchPasswordItems(it.userId) }
            } else {
                _passwordItems.value = Resource.Error("Failed to login user")
            }
        }
    }


    fun logoutUser() {
        viewModelScope.launch {
            authRepository.logout()
            _loggedInUser.value = Resource.Error("User logged out")
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
        private val authRepository: AuthRepository,
        private val application: Application,
        private val passwordsRepository: PasswordsRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AllPasswordsViewModel::class.java)) {
                val localRepository =
                    PasswordLocalRepository.getInstance(application.applicationContext)
                @Suppress("UNCHECKED_CAST")
                return AllPasswordsViewModel(authRepository, localRepository, passwordsRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

