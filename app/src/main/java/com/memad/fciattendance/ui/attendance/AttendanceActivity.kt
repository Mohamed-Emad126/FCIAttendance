package com.memad.fciattendance.ui.attendance

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.memad.fciattendance.R
import com.memad.fciattendance.di.annotations.Scopes
import com.memad.fciattendance.ui.login.LoginActivity
import com.memad.fciattendance.ui.login.LoginViewModel
import com.memad.fciattendance.utils.Constants
import com.memad.fciattendance.utils.SharedPreferencesHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AttendanceActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    @Scopes
    @Inject
    lateinit var scopes: List<String>

    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        val signedInAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (signedInAccount?.account == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val account = signedInAccount.account
            val credential = GoogleAccountCredential.usingOAuth2(this, scopes)
            credential?.setSelectedAccountName(account?.name)
            loginViewModel.checkExistingUser(account?.name!!, credential!!)
        }
        setContentView(R.layout.activity_attendance)
    }
}