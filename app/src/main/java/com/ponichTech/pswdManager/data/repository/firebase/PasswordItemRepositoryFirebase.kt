package com.ponichTech.pswdManager.data.repository.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.data.repository.interfaces.PasswordItemRepository
import com.ponichTech.pswdManager.utils.Resource
import com.ponichTech.pswdManager.utils.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// implementation of PasswordItemRepository interface
class PasswordItemRepositoryFirebase : PasswordItemRepository {

    // Reference to the Firestore instance and the password_items collection
    private val fireRepo = FirebaseFirestore.getInstance().collection("password_items")

    // Adds a PasswordItem to Firestore
    override suspend fun addPassItem(passItem: PasswordItem) = withContext(Dispatchers.IO) {
        safeCall {
            // Generate a new document ID and create a new PasswordItem with it
            val passId = fireRepo.document().id // Generate From FireStore
            val newPass = passItem.copy(id = passId)
            fireRepo.document(passId).set(newPass).await() // Set the new document with the generated ID
            Resource.Success(newPass)
        }
    }

    override suspend fun updatePassword(password: PasswordItem) {
        safeCall {
            val result: Void = fireRepo.document(password.id).set(password).await()

            Resource.Success(result)
        }
    }


    // Deletes a PasswordItem from Firestore by its ID
    override suspend fun deletePassItem(passId: String) = withContext(Dispatchers.IO) {
        safeCall {
            // Delete the PasswordItem from Firestore
            val result = fireRepo.document(passId).delete().await()
            Resource.Success(result)
        }
    }

    // Retrieves PasswordItem from Firestore by ID
    override suspend fun getPassItem(passId: String) = withContext(Dispatchers.IO) {
        safeCall {
            // Get the document from Firestore, convert it to PasswordItem
            val result = fireRepo.document(passId).get().await()
            val task = result.toObject(PasswordItem::class.java)
            Resource.Success(task!!)
        }
    }

    // Retrieves all PasswordItems from Firestore
    override suspend fun getPassItems() = withContext(Dispatchers.IO) {
        safeCall {
            // Get all documents from the collection and convert them to PasswordItem objects
            val result = fireRepo.get().await()
            val passItem = result.toObjects(PasswordItem::class.java)
            Resource.Success(passItem)
        }
    }

    // Retrieves PasswordItems and posts the result to the given LiveData
    override fun getPassItemLiveData(data: MutableLiveData<Resource<List<PasswordItem>>>) {
        data.postValue(Resource.Loading())

        // Add a snapshot listener to get real-time updates
        fireRepo.orderBy("serviceName").addSnapshotListener { snapshot, e ->
            if (e != null) {
                data.postValue(e.localizedMessage?.let { Resource.Error(it) })
            }
            if (snapshot != null && !snapshot.isEmpty) {
                data.postValue(Resource.Success(snapshot.toObjects(PasswordItem::class.java)))
            } else {
                data.postValue(Resource.Error("No Data"))
            }
        }
    }

    // Function to retrieve password items as a flow (commented out)
    // override fun getPassItemsFlow(): Flow<Resource<List<PasswordItem>>> = callbackFlow {
    //     val snapshotListener = passItemRef.orderBy("title").addSnapshotListener { value, error ->
    //         val response = if (value != null) {
    //             val tasks = value.toObjects(Task::class.java)
    //             Resource.Success(tasks)
    //         } else {
    //             Resource.Error(error?.message ?: error.toString())
    //         }
    //         trySend(response)
    //     }
    //     awaitClose {
    //         snapshotListener.remove()
    //     }
    // }
}