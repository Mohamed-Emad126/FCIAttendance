package com.memad.fciattendance.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "History_TABLE")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "History_id")
    val historyId: Int?,
    @ColumnInfo(name = "qr_data") val qrData: String,
    @ColumnInfo(name = "subject") val subject: String,
    @ColumnInfo(name = "week") val week: String,
    @ColumnInfo(name = "attend_or_assign") val attendOrAssign: String,
    @ColumnInfo(name = "grade") val grade: String,
    @ColumnInfo(name = "assign_number") val assignNumber: String
)