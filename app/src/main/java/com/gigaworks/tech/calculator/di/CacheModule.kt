package com.gigaworks.tech.calculator.di

import android.content.Context
import androidx.room.Room
import com.gigaworks.tech.calculator.cache.Database
import com.gigaworks.tech.calculator.cache.dao.HistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(context, Database::class.java, "calculator-plus-db").build()
    }

    @Provides
    fun provideUserDao(database: Database): HistoryDao {
        return database.historyDao()
    }

}