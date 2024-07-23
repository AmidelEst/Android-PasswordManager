package com.ponichTech.pswdManager.utils

import com.google.firebase.auth.FirebaseAuthException

// function that takes a lambda action, which returns a Resource<T>
// T is a generic type parameter, allowing this function to work with any type
inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        // Executes the provided lambda function and returns its result
        action()
    } catch (e: FirebaseAuthException) {
        // Check if the error code is in the map and return the corresponding custom message
        Resource.Error(mapFirebaseErrorCodeToMessage(e.errorCode))
    } catch (e: Exception) {
        // If an exception is caught, returns a Resource.Error with the exception message
        Resource.Error(e.message ?: "An unknown Error Occurred")
    }
}
fun mapFirebaseErrorCodeToMessage(errorCode: String): String {
    return when (errorCode) {
        "ERROR_INVALID_CUSTOM_TOKEN" -> "The custom token format is incorrect. Please check the documentation."
        "ERROR_CUSTOM_TOKEN_MISMATCH" -> "The custom token corresponds to a different audience."
        "ERROR_INVALID_CREDENTIAL" -> "The supplied credential are malformed."
        "ERROR_INVALID_EMAIL" -> "email address is badly formatted."
        "ERROR_WRONG_PASSWORD" -> "The password is invalid or the user does not have a password."
        "ERROR_USER_MISMATCH" -> "The supplied credentials do not correspond to the previously signed in user."
        "ERROR_REQUIRES_RECENT_LOGIN" -> "This operation is sensitive and requires recent authentication. Log in again before retrying this request."
        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address."
        "ERROR_EMAIL_ALREADY_IN_USE" -> "The email address is already in use by another account."
        "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "This credential is already associated with a different user account."
        "ERROR_USER_DISABLED" -> "The user account has been disabled by an administrator."
        "ERROR_USER_TOKEN_EXPIRED" -> "The user\\'s credential is no longer valid. The user must sign in again."
        "ERROR_USER_NOT_FOUND" -> "There is no user record corresponding to this identifier. The user may have been deleted."
        "ERROR_INVALID_USER_TOKEN" -> "The user\\'s credential is no longer valid. The user must sign in again."
        "ERROR_OPERATION_NOT_ALLOWED" -> "This operation is not allowed. You must enable this service in the console."
        "ERROR_WEAK_PASSWORD" -> "The given password is too weak."
        else -> "An unknown error occurred."
    }
}
