package com.havas.newsbubble.domain.util

/**
 * Resource class for handling the different request states
 */
data class Resource<out T>(
    val status: STATUS,
    val data: T?,
    val message: String,
    val errorCode: String? = null,
) {
    enum class STATUS {
        SUCCESS, ERROR, LOADING,
    }

    companion object {
        fun <T> success(data: T) = Resource(STATUS.SUCCESS, data, "Success")
        fun loading() = Resource(STATUS.LOADING, null, "")
        fun error(message: String, errorCode: String? = null) =
            Resource(STATUS.ERROR, null, message, errorCode)
    }
}