package com.ponichTech.pswdManager.data.repository.user_repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.ponichTech.pswdManager.data.model.User
import com.ponichTech.pswdManager.utils.Resource
import com.ponichTech.pswdManager.utils.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepositoryFirebase : UserRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebase = FirebaseFirestore.getInstance()

    // registerUser
    override suspend fun registerUser(
        userName: String,
        userEmail: String,
        userPhone: String,
        userLoginPass: String
    ): Resource<User> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val registrationResult =
                    firebaseAuth.createUserWithEmailAndPassword(userEmail, userLoginPass).await()
                val userId = registrationResult.user?.uid!!
                // here we are getting the id from kotlin constructor
                val newUser = User(name = userName, email = userEmail, phone = userPhone)
                firebase.document(userId).set(newUser).await()
                Resource.Success(newUser)
            }

        }
    }

    // login
    override suspend fun login(email: String, password: String): Resource<User> {
        return withContext(Dispatchers.IO) {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid
            if (userId != null) {
                val user = firebase.collection("users").document(userId)
                    .get().await().toObject(User::class.java)
                if (user != null) {
                    Resource.Success(user)
                } else {
                    Resource.Error("User data not found")
                }
            } else {
                Resource.Error("User ID is null")
            }
        }
    }

    // getCurrentUser
    override suspend fun getCurrentUser(): Resource<User> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val user = firebase.collection("users").document(firebaseAuth.currentUser!!.uid)
                    .get().await().toObject(User::class.java)
                Resource.Success(user!!)
            }
        }
    }

    //logout
    override fun logout() {
        firebaseAuth.signOut()
    }
}