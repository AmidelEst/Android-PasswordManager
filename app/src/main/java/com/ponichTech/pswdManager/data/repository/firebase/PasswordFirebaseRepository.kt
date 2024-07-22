package com.ponichTech.pswdManager.data.repository.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.data.repository.interfaces.PasswordsRepository
import com.ponichTech.pswdManager.utils.Resource
import com.ponichTech.pswdManager.utils.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PasswordFirebaseRepository : PasswordsRepository {

    private val passwordsCollection = FirebaseFirestore.getInstance().collection("password_items")
    private val data = MutableLiveData<Resource<List<PasswordItem>>>()

    override suspend fun addPassword(item: PasswordItem): Resource<Unit> = withContext(Dispatchers.IO) {
        safeCall {
            val passId = passwordsCollection.document().id // Generate From FireStore
            val newPass = item.copy(id = passId)
            passwordsCollection.document(passId).set(newPass).await() // Set the new document with the generated ID
            Resource.Success(Unit)
        }
    }

    override suspend fun updatePassword(item: PasswordItem): Resource<Unit> = withContext(Dispatchers.IO) {
        safeCall {
            passwordsCollection.document(item.id).set(item).await()
            Resource.Success(Unit)
        }
    }

    override suspend fun deletePassword(item: PasswordItem): Resource<Unit> = withContext(Dispatchers.IO) {
        safeCall {
            passwordsCollection.document(item.id).delete().await()
            Resource.Success(Unit)
        }
    }

    // Retrieves PasswordItem from Firestore by ID
    override suspend fun getPassword(id: String): Resource<PasswordItem> = withContext(Dispatchers.IO) {
        safeCall {
            // Get the document from Firestore, convert it to PasswordItem
            val result = passwordsCollection.document(id).get().await()
            val password = result.toObject(PasswordItem::class.java)
            Resource.Success(password!!)
        }
    }

    // Retrieves PasswordItems and posts the result to the given LiveData
    override fun getPasswordsLiveData(userId: String):LiveData<Resource<List<PasswordItem>>>{
        data.postValue(Resource.Loading())

        // Add a snapshot listener to get real-time updates
        passwordsCollection.orderBy("serviceName").addSnapshotListener { snapshot, e ->
            if (e != null) {
                data.postValue(Resource.Error(e.localizedMessage ?: "Unknown error"))
            } else if (snapshot != null && !snapshot.isEmpty) {
                data.postValue(Resource.Success(snapshot.toObjects(PasswordItem::class.java)))
            } else {
                data.postValue(Resource.Error("No Data"))
            }
        }

        return data
    }

    // Function to retrieve password items as a flow
    fun getAllPasswordsFlow(): Flow<Resource<List<PasswordItem>>> = callbackFlow {
        val snapshotListener = passwordsCollection.orderBy("serviceName").addSnapshotListener { value, error ->
            val response = if (value != null) {
                val passwords = value.toObjects(PasswordItem::class.java)
                Resource.Success(passwords)
            } else {
                Resource.Error(error?.message ?: "Unknown error")
            }
            trySend(response)
        }
        awaitClose {
            snapshotListener.remove()
        }
    }
}
