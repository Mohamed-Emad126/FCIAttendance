package com.memad.fciattendance.ui.history

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.gson.Gson
import com.memad.fciattendance.data.HistoryEntity
import com.memad.fciattendance.utils.Constants
import com.memad.fciattendance.utils.SharedPreferencesHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ProcessAllWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    val sharedPreferencesHelper: SharedPreferencesHelper

    init {
        val mainKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val shared = EncryptedSharedPreferences.create(
            context,
            Constants.APP_PREFERENCES,
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        sharedPreferencesHelper = SharedPreferencesHelper(Gson(), shared)
    }

    override suspend fun doWork(): Result {
        val value = inputData.getString("history") ?: return Result.failure()
        val it = Gson().fromJson(value, HistoryEntity::class.java)
        val sheetId = Constants.SPREADSHEET_IDs[Constants.NAMES_OF_SHEETS.indexOf(it.subject)]

        val SCOPES = listOf(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE_FILE, SheetsScopes.DRIVE)
        val history = Gson().fromJson(value, HistoryEntity::class.java)
        val signedInAccount = GoogleSignIn.getLastSignedInAccount(applicationContext)
        val account = signedInAccount!!.account
        val credential = GoogleAccountCredential.usingOAuth2(
            applicationContext, SCOPES
        )
        credential!!.setSelectedAccountName(account!!.name)

        return sendToSheet(
            credential,
            history.qrData.split(":").reversed(),
            sheetId,
            history.attendOrAssign,
            history.assignNumber,
            history.subject,
            history.grade,
            history.week
        )
    }


    private fun sendToSheet(
        credential: GoogleAccountCredential,
        decryptWithAES: List<String>,
        sheetId: String,
        assnOrAttend: String,
        assignNum: String,
        subject: String,
        assignGrade: String = "1",
        week: String
    ): Result {
        val isDoctor = sharedPreferencesHelper.readBoolean(Constants.IS_DOCTOR)
        val range = calculateRange(assnOrAttend, assignNum, isDoctor, week)
        val whereToPutData = "$subject!${range}${decryptWithAES[1]}"
        val spreadsheet = getSpreadSheet(credential)

        val values = listOf(
            listOf(assignGrade)
        )
        val body = ValueRange().setValues(values)
        val successMessage = "Success: ${decryptWithAES[0]}"
        val outputData = Data.Builder()
            .putString("message", successMessage)
            .build()

        return try {
            val result = spreadsheet.values().update(sheetId, whereToPutData, body)
                .setValueInputOption("RAW")
                .execute()
            if (result.updatedCells == 1) {
                Result.success(outputData)
            } else {
                Result.failure(outputData)
            }
        } catch (e: UserRecoverableAuthIOException) {
            Result.retry()
        }
    }

    private fun getSpreadSheet(credential: GoogleAccountCredential): Sheets.Spreadsheets {
        val service = Sheets.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), credential)
            .setApplicationName("Google Sheets API")
            .build()
        return service.spreadsheets()
    }

    private fun calculateRange(
        assnOrAttend: String,
        assignNum: String,
        isDoctor: Boolean,
        week: String
    ): String {
        var range = 2
        range += if (assnOrAttend == "Attendance") {
            if (isDoctor) {
                (week.toInt() * 2) + 2
            } else {
                (week.toInt() * 2) + 1
            }
        } else {
            21 + (assignNum.toInt() - 1)
        }
        return getExcelColumnName(range)
    }

    private fun getExcelColumnName(columnNumber: Int): String {
        var colNumber = columnNumber
        val stringBuilder = StringBuilder()

        while (colNumber > 0) {
            val mod = (colNumber - 1) % 26
            stringBuilder.insert(0, (mod + 'A'.code).toChar())
            colNumber = (colNumber - mod) / 26
        }

        return stringBuilder.toString()
    }

}