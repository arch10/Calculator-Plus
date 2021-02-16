package com.gigaworks.tech.calculator.util


class CalculationException(
    val msg: CalculationMessage
) : ArithmeticException(msg.name)

enum class CalculationMessage {
    INVALID_EXPRESSION,
    DIVIDE_BY_ZERO,
    VALUE_TOO_LARGE,
    DOMAIN_ERROR
}