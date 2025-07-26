package com.gigaworks.tech.calculator.util

import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import java.util.Locale

fun Fragment.getClassName(): String {
    return this.javaClass.simpleName
}

fun Fragment.logD(msg: String?) {
    printLogD(this.getClassName(), msg)
}

fun Fragment.logE(msg: String?) {
    printLogE(this.getClassName(), msg)
}

fun ViewModel.getClassName(): String {
    return this.javaClass.simpleName
}

fun ViewModel.logD(msg: String?) {
    printLogD(this.getClassName(), msg)
}

fun ViewModel.logE(msg: String?) {
    printLogE(this.getClassName(), msg)
}

fun Activity.getClassName(): String {
    return this.javaClass.simpleName
}

fun Activity.logD(msg: String?) {
    printLogD(this.getClassName(), msg)
}

fun Activity.logW(msg: String?) {
    printLogW(this.getClassName(), msg)
}

fun Activity.logE(msg: String?) {
    printLogE(this.getClassName(), msg)
}

fun View.visible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun String.capitalize(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase(Locale.ROOT)
        else
            it.toString()
    }
}