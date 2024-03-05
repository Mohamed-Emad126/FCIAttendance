package com.memad.fciattendance.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [HistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class HistoryDB : RoomDatabase() {
    abstract fun getHistoryDao(): HistoryDao
}