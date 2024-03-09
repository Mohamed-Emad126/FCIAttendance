package com.memad.fciattendance.ui.attendance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import com.memad.fciattendance.data.HistoryDao
import com.memad.fciattendance.utils.Constants
import com.memad.fciattendance.utils.NetworkStatus
import com.memad.fciattendance.utils.NetworkStatusHelper
import com.memad.fciattendance.utils.SharedPreferencesHelper
import com.memad.fciattendance.utils.weekNum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val historyDao: HistoryDao,
    networkStatusHelper: NetworkStatusHelper,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ViewModel() {

    private val _resultFlow = MutableSharedFlow<Any?>()
    val resultFlow = _resultFlow.asSharedFlow()
    private val firstTime = MutableLiveData(true)
    private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    val networkStatus: StateFlow<NetworkStatus> = networkStatusHelper.networkStatus.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5000),
        initialValue = NetworkStatus.Unknown
    )

    fun sendToSheet(
        credential: GoogleAccountCredential,
        decryptWithAES: List<String>,
        sheetId: String,
        assnOrAttend: String,
        assignNum: String,
        subject: String,
        assignGrade: String = "1"
    ) {
        firstTime.value = false
        val isDoctor = sharedPreferencesHelper.readBoolean(Constants.IS_DOCTOR)
        val range = calculateRange(assnOrAttend, assignNum, isDoctor)
        val whereToPutData = "$subject!${range}${decryptWithAES[1]}"
        val spreadsheet = getSpreadSheet(credential)
        val values = listOf(listOf(assignGrade))
        insertHistory(
            decryptWithAES.reversed().joinToString(": "),
            subject,
            weekNum().toString(),
            assnOrAttend,
            assignNum,
            assignGrade
        )
        val body = ValueRange().setValues(values)
        updateSpreadSheet(sheetId, whereToPutData, body, spreadsheet, decryptWithAES)
    }

    private fun calculateRange(assnOrAttend: String, assignNum: String, isDoctor: Boolean): String {
        var range = 2
        range += if (assnOrAttend == "Attendance") {
            if (isDoctor) {
                (weekNum().toInt() * 2) + 2
            } else {
                (weekNum().toInt() * 2) + 1
            }
        } else {
            21 + (assignNum.toInt() - 1)
        }
        return getExcelColumnName(range)
    }

    private fun getSpreadSheet(credential: GoogleAccountCredential): Sheets.Spreadsheets {
        val service = Sheets.Builder(httpTransport, GsonFactory.getDefaultInstance(), credential)
            .setApplicationName("Google Sheets API")
            .build()
        return service.spreadsheets()
    }

    private fun updateSpreadSheet(
        sheetId: String,
        whereToPutData: String,
        body: ValueRange,
        spreadsheet: Sheets.Spreadsheets,
        decryptWithAES: List<String>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = spreadsheet.values().update(sheetId, whereToPutData, body)
                    .setValueInputOption("RAW")
                    .execute()
                if (result.updatedCells == 1) {
                    _resultFlow.emit(Pair("Success", decryptWithAES[0]))
                } else {
                    _resultFlow.emit("Error")
                }
            } catch (e: UserRecoverableAuthIOException) {
                _resultFlow.emit(e.intent)
            }
        }
    }

    fun insertHistory(
        qrData: String,
        subject: String,
        week: String,
        assnOrAttend: String,
        assignNum: String,
        assignGrade: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.insertHistory(qrData, subject, week, assnOrAttend, assignGrade, assignNum)
        }
    }

    fun setFirstTime() {
        firstTime.value = true
    }

    override fun onCleared() {
        super.onCleared()
        setFirstTime()
    }

    private fun getExcelColumnName(columnNumber: Int): String {
        var columnNumber = columnNumber
        val stringBuilder = StringBuilder()

        while (columnNumber > 0) {
            val mod = (columnNumber - 1) % 26
            stringBuilder.insert(0, (mod + 'A'.code).toChar())
            columnNumber = (columnNumber - mod) / 26
        }

        return stringBuilder.toString()
    }
}