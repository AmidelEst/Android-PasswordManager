package com.ponichTech.pswdManager.ui.items.passwords_view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.data.repository.local_Repo.PasswordItemRepositoryLocal
import kotlinx.coroutines.launch

// ViewModel class for managing PasswordItems
class LocalPasswordItemViewModel(application: Application) : AndroidViewModel(application) {

    // Repository instance for local data operations
    private val repository = PasswordItemRepositoryLocal(application)

    // LiveData holding a list of PasswordItems
    val passPassItems: LiveData<List<PasswordItem>>? = repository.getPassItems()

    // Private mutable LiveData to hold a single PasswordItem
    private val _passItem = MutableLiveData<PasswordItem>()
    // Public LiveData to expose the PasswordItem
    val assPassItem: LiveData<PasswordItem> get() = _passItem

    // Sets the value of the PasswordItem LiveData
    fun setPassItem(passItem: PasswordItem) {
        _passItem.value = passItem
    }

    // Adds a new PasswordItem using the repository
    fun addPassItem(passItem: PasswordItem) {
        viewModelScope.launch {
            repository.addPassItem(passItem)
        }
    }

    // Deletes a PasswordItem using the repository
    fun deletePassItem(passItem: PasswordItem) {
        viewModelScope.launch {
            repository.deletePassItem(passItem)
        }
    }

    // Deletes all PasswordItems using the repository
    fun deleteAllPassItems() {
        viewModelScope.launch {
            repository.deleteAllItems()
        }
    }
}
