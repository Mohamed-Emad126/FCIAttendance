package com.memad.fciattendance.ui.login.subjects

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.sheets.v4.model.ValueRange
import com.memad.fciattendance.di.annotations.Scopes
import com.memad.fciattendance.utils.AccessNative
import com.memad.fciattendance.utils.Constants.rangeOfResponses
import com.memad.fciattendance.utils.getSpreadSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    @Scopes
    val scopes: List<String>,
    private val httpTransport: NetHttpTransport
) : ViewModel() {
    private val _resultFlow = MutableSharedFlow<Any?>()
    val resultFlow = _resultFlow.asSharedFlow()

    private val _emailsFlow = MutableSharedFlow<List<String>>()
    val emailsFlow = _emailsFlow.asSharedFlow()

    private val _subjectsFlow = MutableSharedFlow<List<String>>()
    val subjectsFlow = _subjectsFlow.asSharedFlow()


    fun getSubjects(credential: GoogleAccountCredential) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = credential.getSpreadSheet(httpTransport).Values().get(AccessNative.getResponsesSheetId(),rangeOfResponses).execute()
                extractData(result.getValues(), credential.selectedAccountName)
                /*if (result.isNotEmpty()) {
                    _resultFlow.emit(Pair("Success", result.getValues()))
                } else {
                    _resultFlow.emit("Error")
                }*/
            } catch (e: UserRecoverableAuthIOException) {
                _resultFlow.emit("Error")
            }
        }
    }

    private suspend fun extractData(result: List<List<Any>>, email: String){
        val emails = mutableListOf<String>()
        val subjects = mutableListOf<String>()
        result.forEach {
            emails.add(it[0].toString())
            subjects.addAll(it[1].toString().split(','))
        }
        if (email !in emails){
            _resultFlow.emit("You are not Authorized to use this app!")
            return
        }
        else{
            _resultFlow.emit(Pair("Success", result))
        }
        _emailsFlow.emit(emails)
        _subjectsFlow.emit(subjects)
    }

}