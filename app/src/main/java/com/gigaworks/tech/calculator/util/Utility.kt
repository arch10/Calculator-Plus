package com.gigaworks.tech.calculator.util

import com.gigaworks.tech.calculator.R

const val LAUNCHES_UNTIL_PROMPT = 10L
const val DAYS_UNTIL_PROMPT = 5L

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