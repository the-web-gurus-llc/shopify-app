package com.john.shopifyApplication.extensions

import android.text.TextUtils
import android.util.Base64

fun String.isValidEmail(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isNumeric(): Boolean {
    try {
        java.lang.Double.parseDouble(this)
    } catch (nfe: NumberFormatException) {
        return false
    }

    return true
}