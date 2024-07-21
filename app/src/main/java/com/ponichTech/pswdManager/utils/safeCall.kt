package com.ponichTech.pswdManager.utils

// function that takes a lambda action, which returns a Resource<T>
// T is a generic type parameter, allowing this function to work with any type
inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        // Executes the provided lambda function and returns its result
        action()
    } catch (e: Exception) {
        // If an exception is caught, returns a Resource.Error with the exception message
        Resource.Error(e.message ?: "An unknown Error Occurred")
    }
}
