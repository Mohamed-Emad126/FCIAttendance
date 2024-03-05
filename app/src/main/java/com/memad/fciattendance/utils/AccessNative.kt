package com.memad.fciattendance.utils

object AccessNative {
    init {
        System.loadLibrary("api-keys")
    }

    external fun getAESKey(): String
    external fun getAESIV(): String
    external fun getResponsesSheetId(): String

}