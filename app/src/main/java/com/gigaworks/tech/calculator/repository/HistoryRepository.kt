package com.gigaworks.tech.calculator.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gigaworks.tech.calculator.cache.Response
import com.gigaworks.tech.calculator.cache.dao.HistoryDao
import com.gigaworks.tech.calculator.cache.model.HistoryEntity
import com.gigaworks.tech.calculator.cache.safeCacheCall
import com.gigaworks.tech.calculator.domain.History
import com.gigaworks.tech.calculator.domain.toEntity
import com.gigaworks.tech.calculator.util.printLogD
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val cache: HistoryDao
) {

    suspend fun saveHistory(history: History) {
        when (val response = safeCacheCall { cache.insertHistory(history.toEntity()) }) {
            is Response.Failure -> printLogD(
                this.javaClass.simpleName,
                "saveHistory() : ${response.message}"
            )
            is Response.Success -> printLogD(
                this.javaClass.simpleName,
                "saveHistory() : expression saved to history"
            )
        }
    }

    fun getAllHistory(): LiveData<List<HistoryEntity>> {
        return try {
            cache.getAllHistory()
        } catch (e: Exception) {
            printLogD(
                this.javaClass.simpleName,
                "getAllHistory() : ${e.message}"
            )
            MutableLiveData()
        }
    }

    suspend fun clearHistory() {
        try {
            cache.clearHistory()
        } catch (e: Exception) {
            printLogD(
                this.javaClass.simpleName,
                "clearHistory() : ${e.message}"
            )
        }
    }

    suspend fun deleteHistory(expression: String) {
        when (val response =
            safeCacheCall { cache.deleteHistoryByExpression(expression) }) {
            is Response.Failure -> printLogD(
                this.javaClass.simpleName,
                "deleteHistory() : ${response.message}"
            )
            is Response.Success -> printLogD(
                this.javaClass.simpleName,
                "deleteHistory() : deleted a history"
            )
        }
    }

    suspend fun deleteHistoryBefore(date: Long) {
        when (val response =
            safeCacheCall { cache.deleteHistoryBefore(date) }) {
            is Response.Failure -> printLogD(
                this.javaClass.simpleName,
                "deleteHistoryBefore() : ${response.message}"
            )
            is Response.Success -> printLogD(
                this.javaClass.simpleName,
                "deleteHistoryBefore() : deleted all history before $date"
            )
        }
    }

}