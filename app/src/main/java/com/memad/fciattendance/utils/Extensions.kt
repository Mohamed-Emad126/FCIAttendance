package com.memad.fciattendance.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import org.bouncycastle.util.encoders.Base64
import java.security.MessageDigest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

fun Context.createDialog(dialogBinding: View, cancelable: Boolean): Dialog {
    return Dialog(this).apply {
        window?.apply {
            requestFeature(Window.FEATURE_NO_TITLE)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        setContentView(dialogBinding)
        setCanceledOnTouchOutside(cancelable)
    }
}

fun String.encryptWithAES(secretKey: String, iv: String): String? {
    return try {
        val keyBytes =
            MessageDigest.getInstance("SHA-256").digest(secretKey.toByteArray(Charsets.UTF_8))
        val secretKeySpec = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivParameterSpec = IvParameterSpec(iv.toByteArray(Charsets.UTF_8))
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        val cipherText = cipher.doFinal(this.toByteArray(Charsets.UTF_8))
        Base64.encode(cipherText).toString()
    } catch (e: Exception) {
        println(e.message)
        null
    }
}

fun String.decryptWithAES(secretKey: String, iv: String): String? {
    return try {
        val keyBytes =
            MessageDigest.getInstance("SHA-256").digest(secretKey.toByteArray(Charsets.UTF_8))
        val secretKeySpec = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivParameterSpec = IvParameterSpec(iv.toByteArray(Charsets.UTF_8))
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        val decodedValue = Base64.decode(this)
        val decryptedValue = cipher.doFinal(decodedValue)
        String(decryptedValue, Charsets.UTF_8).trim()
    } catch (e: Exception) {
        println(e.message)
        null
    }
}

val weekNum: () -> Long = {
    val startDate = LocalDate.of(2024, 2, 17)
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("y:M:d").withZone(ZoneId.of("UTC"))
    val dat = current.format(formatter).split(":")
    val date = LocalDate.of(dat[0].toInt(), dat[1].toInt(), dat[2].toInt())

    ChronoUnit.DAYS.between(startDate, date) / 7
}


val Int.attendance: Char
    get() {
        return Char(this + 2)
    }

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun GoogleAccountCredential.getSpreadSheet(
    httpTransport: NetHttpTransport
): Sheets.Spreadsheets {
    return Sheets.Builder(httpTransport, GsonFactory.getDefaultInstance(), this)
        .setApplicationName("FCI Attendance")
        .build().Spreadsheets()
}