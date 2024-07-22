package com.ponichTech.pswdManager.data.repository.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ponichTech.pswdManager.data.model.User
import com.ponichTech.pswdManager.data.repository.interfaces.UserRepository
import com.ponichTech.pswdManager.utils.Resource
import com.ponichTech.pswdManager.utils.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepositoryFirebase : UserRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userRef = FirebaseFirestore.getInstance().collection("users")

    override suspend fun getCurrentUser(): Resource<User> {
       return withContext(Dispatchers.IO) {
           safeCall {
               val user = userRef.document(firebaseAuth.currentUser!!.uid).get().await().toObject(User::class.java)
               Resource.Success(user!!)
           }
       }
    }

    override suspend fun login(email: String, password: String): Resource<User> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result  = firebaseAuth.signInWithEmailAndPassword(email,password).await()
                val user = userRef.document(result.user?.uid!!).get().await().toObject(User::class.java)!!
                Resource.Success(user)
            }
        }
    }


    override suspend fun createUser(
        userName: String,
        userEmail: String,
        userPhone: String,
        userLoginPass: String
    ) : Resource<User> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val registrationResult  = firebaseAuth.createUserWithEmailAndPassword(userEmail,userLoginPass).await()
                val userId = registrationResult.user?.uid!!
                // here we are getting the id from kotlin constructor
                val newUser = User(name = userName,email = userEmail,phone = userPhone)
                userRef.document(userId).set(newUser).await()
                Resource.Success(newUser)
            }

        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}