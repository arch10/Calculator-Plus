package com.gigaworks.tech.calculator.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gigaworks.tech.calculator.cache.dao.HistoryDao
import com.gigaworks.tech.calculator.cache.model.HistoryEntity

@Database(entities = [HistoryEntity::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {

    abstract fun historyDao(): HistoryDao

}