package com.gigaworks.tech.calculator.cache

sealed class Response<out T> {
    data class Success<T>(val response: T) : Response<T>()
    data class Failure(val message: String?) : Response<Nothing>()
}