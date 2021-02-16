package com.gigaworks.tech.calculator.ui.main.helper

fun handleDelete(expression: String): String {
    var tempExpression = expression
    var lastCharacter: Char
    do {
        tempExpression = tempExpression.dropLast(1)
        if (tempExpression.isEmpty()) break
        lastCharacter = tempExpression.last()
    } while (lastCharacter.isTrigonometricChar())

    return tempExpression
}

fun handleClick(expression: String, string: String, isPrevResult: Boolean = false): String {
    val numberSet = setOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    val trigonometricSet = setOf(
        "sin",
        "cos",
        "tan",
        "sin⁻¹",
        "cos⁻¹",
        "tan⁻¹",
        "log",
        "ln"
    )
    val rightUnarySet = setOf("%", "!")
    val leftUnarySet = setOf("√", "∛")
    val constantSet = setOf("π", "e")
    val binaryOperatorSet = setOf("÷", "×", "+", "^")
    return when (string) {
        in numberSet -> handleNumberClick(expression, string, isPrevResult)
        in trigonometricSet -> handleTrigonometricClick(expression, string)
        in rightUnarySet -> handleRightUnaryClick(expression, string, isPrevResult)
        in leftUnarySet -> handleLeftUnaryClick(expression, string)
        in constantSet -> handleConstantClick(expression, string, isPrevResult)
        in binaryOperatorSet -> handleBinaryOperatorClick(expression, string)
        "-", "\u2212" -> handleMinusClick(expression, string)
        "(" -> handleOpenBracketClick(expression)
        ")" -> handleCloseBracketClick(expression)
        "." -> handleDecimalClick(expression)
        else -> "$expression$string"
    }
}

private fun handleMinusClick(expression: String, string: String): String {
    if (expression.isEmpty()) return string
    val lastChar = expression.last()
    if (lastChar.isNumber() || lastChar.isUnaryOperator() || lastChar == ')' || lastChar == '(')
        return "$expression$string"
    if (lastChar == '.') return "${expression}0$string"
    if (lastChar.isBinaryOperator() && expression.length > 1 && expression[expression.lastIndex - 1].isNumber()) {
        if (lastChar == '\u2212' || lastChar == '-') {
            val exp = expression.dropLast(1)
            return "$exp+"
        }
        return "$expression$string"

    }
    return expression
}


private fun handleBinaryOperatorClick(expression: String, string: String): String {
    if (expression.isEmpty()) return expression
    val lastChar = expression.last()
    if (lastChar.isNumber() || lastChar.isRightUnaryOperator() || lastChar == ')') return "$expression$string"
    if (lastChar == '.') return "${expression}0$string"
    if (lastChar.isOperator() && expression.length > 1) {
        if (lastChar == '\u2212' || lastChar == '-') {
            val secondLast = expression[expression.lastIndex - 1]
            if (secondLast == '(' || secondLast.isLeftUnaryOperator())
                return expression
        }
        val exp = removeFromEndUntil(expression) {
            it.isBinaryOperator() || it.isLeftUnaryOperator()
        }
        return "$exp$string"
    }
    return expression
}

private fun handleDecimalClick(expression: String): String {
    if (expression.isEmpty()) return "0."
    val lastChar = expression.last()
    if (lastChar.isDigit() && canPlaceDecimal(expression)) return "$expression."
    if (lastChar.isRightUnaryOperator() || lastChar == ')') return "$expression×0."
    if (lastChar.isOperator() || lastChar == '(') return "${expression}0."
    return expression
}

private fun handleCloseBracketClick(expression: String): String {
    if (expression.isEmpty()) return ""
    val lastChar = expression.last()
    if (lastChar.isNumber() || lastChar.isRightUnaryOperator() || lastChar == ')') return "$expression)"
    if (lastChar == '.') return "${expression}0)"
    return expression
}

fun handleConstantClick(expression: String, string: String, isPrevResult: Boolean): String {
    if (expression.isEmpty() || isPrevResult) return string
    val lastChar = expression.last()
    if (lastChar.isRightUnaryOperator() || lastChar.isNumber() || lastChar == ')')
        return "$expression\u00D7$string"
    if (lastChar == '.') return "${expression}0×$string"
    return "$expression$string"
}

private fun handleOpenBracketClick(expression: String): String {
    if (expression.isEmpty()) return "("
    val lastChar = expression.last()
    if (lastChar.isNumber() || lastChar.isRightUnaryOperator() || lastChar == ')') return "$expression×("
    if (lastChar.isOperator() || lastChar == '(') return "$expression("
    if (lastChar == '.') return "${expression}0×("
    return expression
}

private fun handleRightUnaryClick(
    expression: String,
    string: String,
    isPrevResult: Boolean
): String {
    if (expression.isEmpty()) return ""
    if (isPrevResult) return "$expression$string"
    val lastChar = expression.last()
    if (lastChar.isNumber() || lastChar == ')') return "$expression$string"
    if (lastChar.isRightUnaryOperator()) return "${expression.dropLast(1)}$string"
    if (lastChar == '.') return "${expression}0$string"
    if (lastChar.isOperator() && expression.length > 1) {
        if (lastChar == '\u2212' || lastChar == '-') {
            val secondLast = expression[expression.lastIndex - 1]
            if (secondLast == '(' || secondLast.isLeftUnaryOperator())
                return expression
        }
        val exp = removeFromEndUntil(expression) { it.isOperator() }
        if (exp.isNotEmpty())
            return "$exp$string"
    }
    return expression
}

private fun handleLeftUnaryClick(expression: String, string: String): String {
    if (expression.isEmpty()) return string
    val lastChar = expression.last()
    if (lastChar.isNumber() || lastChar.isRightUnaryOperator() || lastChar == ')')
        return "$expression×$string"
    if (lastChar.isOperator() || lastChar == '(') return "$expression$string"
    if (lastChar == '.') return "${expression}0×$string"
    return expression
}

private fun handleTrigonometricClick(expression: String, string: String): String {
    return if (expression.isNotEmpty()) {
        val lastChar = expression.last()
        if (lastChar.isNumber() || lastChar.isRightUnaryOperator() || lastChar == ')') {
            "$expression×$string("
        } else if (lastChar == '.') {
            "${expression}0×$string("
        } else {
            "$expression$string("
        }
    } else {
        "$string("
    }
}

private fun handleNumberClick(expression: String, string: String, isPrevResult: Boolean): String {
    if (expression.isNotEmpty()) {
        if (isPrevResult) return string
        val lastChar = expression.last()
        if (lastChar.isRightUnaryOperator() || lastChar.isConstant() || lastChar == ')')
            return "$expression\u00D7$string"
    }
    return "$expression$string"
}