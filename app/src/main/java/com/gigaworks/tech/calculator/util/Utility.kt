package com.gigaworks.tech.calculator.util

import com.gigaworks.tech.calculator.R

fun getAccentTheme(accentTheme: String): Int {
    return when (accentTheme) {
        AccentTheme.GREEN.name -> {
            R.style.MaterialGreenTheme
        }
        AccentTheme.PURPLE.name -> {
            R.style.MaterialPurpleTheme
        }
        AccentTheme.PINK.name -> {
            R.style.MaterialPinkTheme
        }
        AccentTheme.RED.name -> {
            R.style.MaterialRedTheme
        }
        AccentTheme.GREY.name -> {
            R.style.MaterialGreyTheme
        }
        else -> {
            R.style.BaseTheme
        }
    }
}