package com.gigaworks.tech.calculator.cache

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> safeCacheCall(
    cacheCall: suspend () -> T
): Response<T> {
    return withContext(Dispatchers.IO) {
        try {
            Response.Success(cacheCall.invoke())
        } catch (e: Exception) {
            Response.Failure(e.message)
        }
    }
}

//fun <T> safeCacheCall(
//    cacheCall: () -> T
//): Response<T> {
//    return try {
//        Response.Success(cacheCall.invoke())
//    } catch (e: Exception) {
//        Response.Failure(e.message)
//    }
//}