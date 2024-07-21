package com.ponichTech.pswdManager.ui.users.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ponichTech.pswdManager.data.model.User
import com.ponichTech.pswdManager.data.repository.interfaces.AuthRepository
import com.ponichTech.pswdManager.ui.items.all_password_items.SharedViewModel
import com.ponichTech.pswdManager.utils.Resource
import kotlinx.coroutines.launch

class LoginViewModel(private val authRep: AuthRepository) : ViewModel() {

    private val _userSignInStatus = MutableLiveData<Resource<User>>()
    val userSignInStatus: LiveData<Resource<User>> = _userSignInStatus

    private val _currentUser = MutableLiveData<Resource<User>>()
    val currentUser:LiveData<Resource<User>> = _currentUser

    init {
        viewModelScope.launch {
            _currentUser.postValue(Resource.Loading())
            _currentUser.postValue(authRep.getCurrentUser())
        }
    }

    fun signInUser(userEmail:String, userPass:String) {
        if(userEmail.isEmpty() || userPass.isEmpty())
            _userSignInStatus.postValue(Resource.Error("Empty email or password"))
        else{
            _userSignInStatus.postValue(Resource.Loading())
            viewModelScope.launch {
                val loginResult = authRep.login(userEmail,userPass)
                Log.d("d","The id SignInUser is" +loginResult.data?.userId!!)
                _userSignInStatus.postValue(loginResult)
            }
        }
    }


    class LoginViewModelFactory(private val repo: AuthRepository) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}