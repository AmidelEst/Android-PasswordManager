package com.ponichTech.pswdManager.data.repository.passwords_repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.utils.Resource
import com.ponichTech.pswdManager.utils.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PasswordFirebaseRepository : PasswordsRepository {

    private val firestore = FirebaseFirestore.getInstance()

    private val data = MutableLiveData<Resource<List<PasswordItem>>>()

    override suspend fun addPassword(item: PasswordItem): Resource<PasswordItem> = withContext(Dispatchers.IO) {
        safeCall {
            // Generate a new ID from Firestore
            val id = firestore.collection("password_items").document().id

            // Create a new item with the generated ID
            val itemWithId = item.copy(id = id)

            // Save the new item to FireStore
            firestore.collection("password_items").document(id).set(itemWithId).await()

            // Return the newly created item with its ID
            Resource.Success(itemWithId)
        }
    }


    override suspend fun updatePassword(item: PasswordItem): Resource<Unit> = withContext(Dispatchers.IO) {
        safeCall {
            if (item.id.isNotEmpty()) {
                // Ensure the ID is not empty
                firestore.collection("password_items").document(item.id).set(item).await()
                Resource.Success(Unit)
            } else {
                Resource.Error("Item ID is empty, cannot update")
            }
        }
    }

    override suspend fun deletePassword(item: PasswordItem): Resource<Unit> = withContext(Dispatchers.IO) {
        safeCall {
            firestore.collection("password_items").document(item.id).delete().await()
            Resource.Success(Unit)
        }
    }

    // Retrieves PasswordItems and posts the result to the given LiveData
    override fun getPasswordsLiveData(userId: String): LiveData<Resource<List<PasswordItem>>> {
        data.postValue(Resource.Loading())

        // Add a snapshot listener to get real-time updates
        firestore.collection("password_items")
            .whereEqualTo("userId", userId)
            .orderBy("serviceName")
            .addSnapshotListener { snapshot, e ->
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
        val snapshotListener = firestore.collection("password_items").orderBy("serviceName").addSnapshotListener { value, error ->
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
