package com.gigaworks.tech.calculator.util

import android.util.Log
import com.gigaworks.tech.calculator.BuildConfig.DEBUG
import com.google.firebase.crashlytics.FirebaseCrashlytics

// Tag for logs
const val TAG = "CalculatorPlus"

fun printLogD(className: String, message: String?) {
    if (DEBUG) {
        Log.d("$TAG:DEBUG", "$className: $message")
    }
}

fun printLogW(className: String, message: String?) {
    if(DEBUG) {
        Log.w("$TAG:WARN", "$className: $message")
    }
}

fun printLogE(className: String, message: String?) {
    if (DEBUG) {
        Log.d("$TAG:DEBUG", "$className: $message")
    } else {
        message?.let {
            FirebaseCrashlytics.getInstance().log("$className: $it")
        }
    }
}