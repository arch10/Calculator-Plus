package com.gigaworks.tech.calculator.domain

data class HistoryAdapterItem(
    val date: String,
    val expression: String,
    val result: String,
    val isPrevSame: Boolean,
    val isNextSame: Boolean
)