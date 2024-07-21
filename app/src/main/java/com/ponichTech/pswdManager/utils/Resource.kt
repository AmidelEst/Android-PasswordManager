package com.ponichTech.pswdManager.utils

// A generic class Resource used to represent different states of data
open class Resource<T>(val data: T? = null, val message: String? = null) {
    // Success subclass to represent a successful data state
    class Success<T>(data: T) : Resource<T>(data)

    // Loading subclass to represent a loading state, optional data can be provided
    class Loading<T>(data: T? = null) : Resource<T>(data)

    // Error subclass to represent an error state, includes an error message and optional data
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}
