package com.memad.fciattendance.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.gson.Gson
import com.memad.fciattendance.di.annotations.Scopes
import com.memad.fciattendance.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UtilsModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val mainKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            Constants.appPreferences,
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    @Singleton
    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Scopes
    @Singleton
    @Provides
    fun provideScopes(): List<String>{
        return listOf(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE_FILE, SheetsScopes.DRIVE)
    }
    @Singleton
    @Provides
    fun provideHttpTransport(): NetHttpTransport {
        return GoogleNetHttpTransport.newTrustedTransport()
    }

    /*    @SessionKey
        @Singleton
        @Provides
        fun provideSessionKey(sharedPreferencesHelper: SharedPreferencesHelper, gson: Gson): String {
            val session = sharedPreferencesHelper.read(
                Constants.SESSION,
                gson.toJson(AuthResponse("", "", false))
            )
            return gson.fromJson(session, AuthResponse::class.java).guest_session_id.trim()
        }*/

}