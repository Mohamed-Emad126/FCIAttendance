package com.memad.fciattendance.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface HistoryDao {

    // select all from history_table
    @Query("SELECT * FROM History_TABLE")
    fun getAllHistory(): LiveData<List<HistoryEntity>>

    // insert into history_table
    @Query("INSERT INTO History_TABLE (qr_data, subject, week, attend_or_assign, grade, assign_number) VALUES (:qrData, :subject, :week, :attendOrAssign, :grade, :assignNumber)")
    suspend fun insertHistory(
        qrData: String,
        subject: String,
        week: String,
        attendOrAssign: String,
        grade: String,
        assignNumber: String
    )

    // delete all from history_table return int (number of rows deleted)
    @Query("DELETE FROM History_TABLE")
    suspend fun deleteAllHistory(): Int

    // delete from history_table where history_id = :history
    @Query("DELETE FROM History_TABLE WHERE History_id = :historyId")
    suspend fun deleteHistory(historyId: Int): Int

}