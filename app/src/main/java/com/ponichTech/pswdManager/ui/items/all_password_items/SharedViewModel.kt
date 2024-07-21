package com.ponichTech.pswdManager.ui.items.all_password_items

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ponichTech.pswdManager.data.model.PasswordItem

class SharedViewModel : ViewModel() {
    private val _selectedPasswordItem = MutableLiveData<PasswordItem?>()
    val selectedPasswordItem: LiveData<PasswordItem?> get() = _selectedPasswordItem

    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> get() = _userId

    fun setUserId(userId: String) {
        _userId.value = userId
    }

    fun selectPasswordItem(item: PasswordItem?) {
        _selectedPasswordItem.value = item
    }
}
