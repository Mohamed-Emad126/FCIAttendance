package com.memad.attendance.di

import android.content.Context
import androidx.room.Room
import com.memad.fciattendance.data.HistoryDB
import com.memad.fciattendance.data.HistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HistoryDBModule {

    @Singleton
    @Synchronized
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        HistoryDB::class.java,
        "History_DB"
    ).build()

    @Singleton
    @Provides
    fun provideMoviesDao(historyDB: HistoryDB): HistoryDao {
        return historyDB.getHistoryDao()
    }

}