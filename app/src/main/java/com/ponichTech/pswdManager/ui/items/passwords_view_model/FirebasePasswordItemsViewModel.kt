package com.ponichTech.pswdManager.ui.items.passwords_view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.data.repository.interfaces.AuthRepository
import com.ponichTech.pswdManager.data.repository.interfaces.PasswordItemRepository
import com.ponichTech.pswdManager.utils.Resource
import kotlinx.coroutines.launch

class FirebasePasswordItemsViewModel(
    val authRepo: AuthRepository,
    val passItemRepo: PasswordItemRepository
) : ViewModel() {

    private val _passwordItems = MutableLiveData<Resource<List<PasswordItem>>>()
    val passwordItems: LiveData<Resource<List<PasswordItem>>> get() = _passwordItems

    // LiveData to observe the status of adding a password item
    private val _addPassStatus = MutableLiveData<Resource<PasswordItem>>()
    val addPassStatus: LiveData<Resource<PasswordItem>>  get()=_addPassStatus

    // LiveData to observe the status of deleting a password item
    private val _deletePassStatus = MutableLiveData<Resource<Void>>()
    val deletePassStatus: LiveData<Resource<Void>> get()= _deletePassStatus

    // LiveData to observe the logged-in userId
    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> get() = _userId

    init {
        fetchPasswordItems()
        getCurrentUser()
    }

    // Add a new password item
    fun addPassItem(passItem: PasswordItem) {
        viewModelScope.launch {
            if(passItem.password.isEmpty())
                _addPassStatus.postValue(Resource.Error("Empty task title"))
            else {
                _addPassStatus.postValue(Resource.Loading())
                _addPassStatus.postValue(passItemRepo.addPassItem(passItem))
            }
        }
    }

    fun fetchPasswordItems() {
        viewModelScope.launch {
            _passwordItems.value = Resource.Loading()
            _passwordItems.value = passItemRepo.getPassItems()
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            val user = authRepo.getCurrentUser()
            _userId.postValue(user.data?.userId)
        }
    }

    fun updatePassword(password: PasswordItem) {
        // Update the password in Firebase
        viewModelScope.launch {
            passItemRepo.updatePassword(password)
        }
    }

    // Delete a password item
    fun deletePassItem(passId: String) {
        viewModelScope.launch {
            if(passId.isEmpty())
                _deletePassStatus.postValue(Resource.Error("Empty task id"))
            else {
                _deletePassStatus.postValue(Resource.Loading())
                _deletePassStatus.postValue(passItemRepo.deletePassItem(passId))
            }
        }
    }

    // Sign out the user
    fun signOut() {
        authRepo.logout()
        _userId.postValue(null) // Clear the userId when signed out
    }

    // Factory class for creating an instance of AllPasswordsViewModel
    class FirebasePasswordItemsViewModelFactory(
        private val authRepo: AuthRepository,
        private val passItemRepo: PasswordItemRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FirebasePasswordItemsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FirebasePasswordItemsViewModel(authRepo, passItemRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
