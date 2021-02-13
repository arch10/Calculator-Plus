package com.gigaworks.tech.calculator.ui.settings.helper

import com.gigaworks.tech.calculator.util.HistoryAutoDelete

fun HistoryAutoDelete.getDays(): Int {
    return when (this) {
        HistoryAutoDelete.SEVEN_DAYS -> 7
        HistoryAutoDelete.FIFTEEN_DAYS -> 15
        HistoryAutoDelete.THIRTY_DAYS -> 30
        HistoryAutoDelete.THREE_MONTHS -> 90
        else -> -1
    }
}

fun HistoryAutoDelete.getString(): String {
    return when (this) {
        HistoryAutoDelete.SEVEN_DAYS -> "7 Days"
        HistoryAutoDelete.FIFTEEN_DAYS -> "15 Days"
        HistoryAutoDelete.THIRTY_DAYS -> "30 Days"
        HistoryAutoDelete.THREE_MONTHS -> "90 Days"
        else -> "Never"
    }
}