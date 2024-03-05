package com.memad.fciattendance.ui.login

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.memad.fciattendance.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        val signedInAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (signedInAccount?.account != null) {
            this.findNavController(R.id.nav_host_fragment).navigate(R.id.action_loginFragment_to_attendanceActivity)
            finish()
        }
    }
}