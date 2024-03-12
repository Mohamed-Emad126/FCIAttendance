package com.memad.fciattendance.ui.login

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.javanet.NetHttpTransport
import com.memad.fciattendance.di.annotations.Scopes
import com.memad.fciattendance.utils.AccessNative
import com.memad.fciattendance.utils.Constants
import com.memad.fciattendance.utils.NetworkStatus
import com.memad.fciattendance.utils.NetworkStatusHelper
import com.memad.fciattendance.utils.Resource
import com.memad.fciattendance.utils.SharedPreferencesHelper
import com.memad.fciattendance.utils.getSpreadSheet
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
class LoginViewModel @Inject constructor(
    networkStatusHelper: NetworkStatusHelper,
    @Scopes
    val scopes: List<String>,
    private val httpTransport: NetHttpTransport,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ViewModel() {

    private val _resultFlow = MutableSharedFlow<Resource<List<String>>>()
    val resultFlow = _resultFlow.asSharedFlow()

    private val _authExceptionFlow = MutableSharedFlow<Intent>()
    val authExceptionFlow = _authExceptionFlow.asSharedFlow()


    val networkStatus: StateFlow<NetworkStatus> = networkStatusHelper.networkStatus.stateIn(
        initialValue = NetworkStatus.Unknown,
        scope = viewModelScope,
        started = WhileSubscribed(5000)
    )

    fun checkExistingUser(email: String, credential: GoogleAccountCredential) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = credential.getSpreadSheet(httpTransport)
                    .Values()
                    .get(AccessNative.getResponsesSheetId(), Constants.RANGE_OF_RESPONSES)
                    .execute()
                extractData(result.getValues(), email)
            }
            catch (e: UserRecoverableAuthIOException) {
                _authExceptionFlow.emit(e.intent)
            }
            catch (e: Exception) {
                _resultFlow.emit(Resource.error("Error"))
            }
        }
    }

    private suspend fun extractData(result: List<List<Any>>, email: String) {
        _resultFlow.emit(Resource.loading())
        val emails = mutableListOf<String>()
        result.forEach {
            emails.add(it[0].toString())
        }
        if (email !in emails) {
            _resultFlow.emit(Resource.error("You are not Authorized to use this app!"))
            return
        } else {
            emails.indexOf(email).takeIf { it != -1 }?.let {
                val subjects = result[it][1].toString().split(',')
                sharedPreferencesHelper.saveStringSet(Constants.SUBJECTS, subjects.toSet())
                sharedPreferencesHelper.save(Constants.INDEX, it)
                Log.e("AttendanceActivity", "onCreate:1 " + result[it][2].toString() + "-----" + result[it][3].toString())
                sharedPreferencesHelper.saveBoolean(
                    Constants.IS_VERIFIED,
                    result[it][2].toString() == "1"
                )
                if (result[it].size > 3) {
                    sharedPreferencesHelper.saveBoolean(
                        Constants.IS_DOCTOR,
                        result[it][3].toString() == "1"
                    )
                }
                _resultFlow.emit(Resource.success(subjects))
            }
        }
    }
}