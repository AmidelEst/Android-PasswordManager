package com.ponichTech.pswdManager.ui.users.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ponichTech.pswdManager.data.model.User
import com.ponichTech.pswdManager.data.repository.user_repository.UserRepository
import com.ponichTech.pswdManager.utils.Resource
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {

    private val _registerStatus = MutableLiveData<Resource<User>>()
    val registerStatus: LiveData<Resource<User>> = _registerStatus

    fun registerUser(userName:String, userEmail:String, userPhone:String, userPass:String) {
        val error = if(userEmail.isEmpty() || userName.isEmpty() || userPass.isEmpty() || userPhone.isEmpty())
            "Empty Strings"
        else if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            "Not a valid email"
        }else null
        error?.let {
            _registerStatus.postValue(Resource.Error(it))
        }
        _registerStatus.value = Resource.Loading()
        viewModelScope.launch {
            val registrationResult = repository.registerUser(userName,userEmail,userPhone,userPass)
            _registerStatus.postValue(registrationResult)
        }
    }

    class Factory(private val repo: UserRepository) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                return RegisterViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }

    }
}
