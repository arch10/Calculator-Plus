package com.gigaworks.tech.calculator.util

enum class HistoryAutoDelete(val days: Int) {
    NEVER(-1),
    THREE_MONTHS(90),
    THIRTY_DAYS(30),
    FIFTEEN_DAYS(15),
    SEVEN_DAYS(7)
}