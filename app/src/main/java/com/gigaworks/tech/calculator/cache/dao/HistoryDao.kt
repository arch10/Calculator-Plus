package com.gigaworks.tech.calculator.cache.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gigaworks.tech.calculator.cache.model.HistoryEntity

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(history: HistoryEntity)

    @Query("SELECT * FROM history ORDER BY date DESC")
    fun getAllHistory(): LiveData<List<HistoryEntity>>

    @Query("DELETE FROM history")
    suspend fun clearHistory()

    @Query("DELETE FROM history WHERE expression=:expression")
    suspend fun deleteHistoryByExpression(expression: String)

    @Query("DELETE FROM history WHERE date < :date")
    suspend fun deleteHistoryBefore(date: Long)

}