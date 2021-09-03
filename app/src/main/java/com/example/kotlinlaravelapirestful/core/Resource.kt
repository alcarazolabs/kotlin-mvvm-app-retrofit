package com.example.demo.core

import okhttp3.ResponseBody

//Clase de tipo generica
sealed class Resource<out T> {
    class AnyRes<out T> (val data: T)
    class Loading<out T> : Resource<T>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure(val exception: Exception) : Resource<Nothing>()
    data class FailureHttp(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorBody: ResponseBody?
    ) : Resource<Nothing>()
}
