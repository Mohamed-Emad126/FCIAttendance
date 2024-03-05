package com.memad.fciattendance.ui.login.loginui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memad.fciattendance.utils.NetworkStatus
import com.memad.fciattendance.utils.NetworkStatusHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val networkStatusHelper: NetworkStatusHelper
) : ViewModel() {
    val networkStatus: StateFlow<NetworkStatus> = networkStatusHelper.networkStatus.stateIn(
        initialValue = NetworkStatus.Unknown,
        scope = viewModelScope,
        started = WhileSubscribed(5000)
    )
}