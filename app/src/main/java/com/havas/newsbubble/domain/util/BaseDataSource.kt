package com.havas.newsbubble.domain.util

import android.util.Log
import retrofit2.Response

/**
 * Handles the conversion of http response to Resource
 */
private const val TAG = "BaseDataSource"
abstract class BaseDataSource {
    protected suspend fun <T> getResult(call: suspend ()->Response<T?>): Resource<T> {
        runCatching{
            val response = call()
            if (response.isSuccessful){
                val body = response.body()
                Log.d(TAG, "getResult: ${response.code()} ${response.message()} ${body.toString()}")
                if (body !=null) return Resource.success(body)
            }
            return  Resource.error(response.message())
        }.getOrElse { ex ->
            ex.printStackTrace()
            return Resource.error(ex.message ?: ex.toString())
        }
    }
}