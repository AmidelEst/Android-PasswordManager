package com.ponichTech.pswdManager.utils

import com.google.firebase.auth.FirebaseAuthException
import com.ponichTech.pswdManager.R



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
//        "ERROR_INVALID_CUSTOM_TOKEN" -> context.getString(R.string.error_invalid_custom_token)
//        "ERROR_CUSTOM_TOKEN_MISMATCH" -> context.getString(R.string.error_custom_token_mismatch)
//        "ERROR_INVALID_CREDENTIAL" -> context.getString(R.string.error_invalid_credential)
//        "ERROR_INVALID_EMAIL" -> context.getString(R.string.error_invalid_email)
//        "ERROR_WRONG_PASSWORD" -> context.getString(R.string.error_wrong_password)
//        "ERROR_USER_MISMATCH" -> context.getString(R.string.error_user_mismatch)
//        "ERROR_REQUIRES_RECENT_LOGIN" -> context.getString(R.string.error_requires_recent_login)
//        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> context.getString(R.string.error_account_exists_with_different_credential)
//        "ERROR_EMAIL_ALREADY_IN_USE" -> context.getString(R.string.error_email_already_in_use)
//        "ERROR_CREDENTIAL_ALREADY_IN_USE" -> context.getString(R.string.error_credential_already_in_use)
//        "ERROR_USER_DISABLED" -> context.getString(R.string.error_user_disabled)
//        "ERROR_USER_TOKEN_EXPIRED" -> context.getString(R.string.error_user_token_expired)
//        "ERROR_USER_NOT_FOUND" -> context.getString(R.string.error_user_not_found)
//        "ERROR_INVALID_USER_TOKEN" -> context.getString(R.string.error_invalid_user_token)
//        "ERROR_OPERATION_NOT_ALLOWED" -> context.getString(R.string.error_operation_not_allowed)
//        "ERROR_WEAK_PASSWORD" -> context.getString(R.string.error_weak_password)


        else -> "An unknown error occurred."
    }
}
