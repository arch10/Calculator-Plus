package com.gigaworks.tech.calculator.ui.main.helper

import android.util.Log
import ch.obermuhlner.math.big.BigDecimalMath
import com.gigaworks.tech.calculator.util.AngleType
import com.gigaworks.tech.calculator.util.CalculationException
import java.lang.Math.cbrt
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.*

fun removeNumberSeparator(expression: String, separator: Char = ','): String {
    return expression.replace(separator.toString(), "")
}

fun addNumberSeparator(
    expression: String,
    separator: Char = ',',
    isIndian: Boolean = false
): String {
    if (expression.contains("E"))
        return expression
    val expressionList = separateOutExpression(expression).map {
        if (it.isNumber()) {
            addSeparator(it, separator, isIndian)
        } else {
            it
        }
    }
    val stringBuilder = StringBuilder()
    expressionList.forEach { stringBuilder.append(it) }
    return stringBuilder.toString()
}

private fun addSeparator(string: String, separator: Char, isIndian: Boolean): String {
    var decimalIndex = string.indexOf('.')
    if (decimalIndex == -1)
        decimalIndex = string.length
    var str = string
    var temp = 0
    var switch = true
    for (i in decimalIndex - 1 downTo 1) {
        temp++
        if (switch && temp % 3 == 0) {
            temp = 0
            switch = !isIndian
            if (i == 1 && (string[0] == '-' || string[0] == '\u2212')) break
            str = str.substring(0, i) + separator + str.substring(i)
            continue
        }
        if (!switch && temp % 2 == 0) {
            temp = 0
            if (i == 1 && (string[0] == '-' || string[0] == '\u2212')) break

            str = str.substring(0, i) + separator + str.substring(i)
        }
    }
    return str
}

fun separateOutExpression(expression: String): List<String> {
    val list = mutableListOf<String>()
    val temp = StringBuilder()
    for (i in expression.indices) {
        val char = expression[i]
        if (char.isOperator() || char == '(' || char == ')') {
            if (temp.isNotEmpty()) {
                list.add(temp.toString())
                temp.clear()
            }
            list.add(char.toString())
        } else {
            temp.append(char)
        }
    }
    if (temp.isNotEmpty()) {
        list.add(temp.toString())
    }
    return list
}

fun removeFromEndUntil(expression: String, condition: (Char) -> Boolean): String {
    if (expression.isNotEmpty()) {
        var lastChar = expression.last()
        var exp = expression
        while (condition(lastChar)) {
            exp = exp.dropLast(1)
            if (exp.isEmpty()) return ""
            lastChar = exp.last()
        }
        return exp
    }
    return ""
}

fun formatNumber(x: BigDecimal, scale: Int): String {
    val formatter = DecimalFormat("0.0E0")
    formatter.roundingMode = RoundingMode.HALF_UP
    formatter.minimumFractionDigits = scale
    formatter.minimumIntegerDigits = 1
    return formatter.format(x)
}

fun BigDecimal.toScientific(): String {
    var temp = this.toPlainString()
    var scale = 8
    Log.d("DEBUG", "converting to scientific: $temp")
    val numberLength = temp.length
    if (numberLength > 20) {
        temp = temp.substring(0, 21)
    }
    while (temp.length > 13) {
        temp = formatNumber(BigDecimalMath.toBigDecimal(temp), scale)
        scale--
        if (scale == 1) break
    }
    Log.d("DEBUG", "scientific formatting done. ${numberLength - 20}")
    return temp
}

fun isExpressionBalanced(expression: String): Boolean {
    val stack = Stack<Char>()
    for (char in expression) {
        if (char == '(') {
            stack.push(char)
        } else if (char == ')') {
            if (stack.isEmpty()) return false
            stack.pop()
        }
    }
    return stack.isEmpty()
}

//tries to balance the equations with smart balancing
fun tryBalancingBrackets(expression: String): String {
    var exp = expression
    var a = 0
    var b = 0

    if (exp.last() == '(') {
        while (exp.last() == '(') {
            exp = exp.dropLast(1)
            if (exp.isEmpty()) return exp
        }
    }

    for (element in exp) {
        if (element == '(') a++
        if (element == ')') b++
    }

    val numOfPairs = min(a, b)
    var reqOpen = b - numOfPairs
    var reqClose = a - numOfPairs
    while (reqOpen > 0) {
        exp = "($exp"
        reqOpen--
    }
    while (reqClose > 0) {
        exp = "$exp)"
        reqClose--
    }
    return exp
}

//checks if the char can be a last character in a valid equation
fun canBeLastChar(char: Char): Boolean {
    return (char.isNumber() || char.isRightUnaryOperator() || char == ')')
}

fun solveExpression(
    expressionStack: Stack<String>,
    angleType: String = AngleType.DEG.name
): String {
    var stack: Stack<String>

    if (expressionStack.contains("-")) {
        stack = Stack()
        while (expressionStack.isNotEmpty()) {
            if (expressionStack.peek() == "-") {
                expressionStack.pop()
                val negValue = "-${expressionStack.pop()}"
                stack.push(negValue)
                continue
            }
            stack.push(expressionStack.pop())
        }
        stack.reverse()
    } else {
        stack = expressionStack
    }

    //check if solved
    if (stack.size == 1) return stack.pop()

    //solve trigonometric operators
    stack = solveTrigonometricExpression(stack, angleType)

    //check if solved
    if (stack.size == 1) return stack.pop()

    //solve left unary operators
    stack = solveLeftUnary(stack)

    //check if solved
    if (stack.size == 1) return stack.pop()

    //solve right unary operators
    stack = solveRightUnary(stack)

    //check if solved
    if (stack.size == 1) return stack.pop()

    stack = solvePower(stack)

    //check if solved
    if (stack.size == 1) return stack.pop()

    stack = solveDivision(stack)

    //check if solved
    if (stack.size == 1) return stack.pop()

    stack = solveMultiplication(stack)

    //check if solved
    if (stack.size == 1) return stack.pop()

    stack = solveAddition(stack)

    //check if solved
    if (stack.size == 1) return stack.pop()

    throw CalculationException("Invalid expression")
}

fun solveAddition(stack: Stack<String>): Stack<String> {
    val tempStack = Stack<String>()
    var temp: String
    while (stack.isNotEmpty()) {
        temp = stack.pop()
        if (temp == "+") {
            val precision = MathContext(20)
            val num1 = BigDecimalMath.toBigDecimal(tempStack.pop())
            val num2 = BigDecimalMath.toBigDecimal(stack.pop())
            val result = num1.add(num2, precision)
            tempStack.push(result.toString())
        } else {
            tempStack.push(temp)
        }
    }
    tempStack.reverse()
    return tempStack
}

fun solveMultiplication(stack: Stack<String>): Stack<String> {
    val tempStack = Stack<String>()
    var temp: String
    while (stack.isNotEmpty()) {
        temp = stack.pop()
        if (temp == "*") {
            val precision = MathContext(20)
            val num1 = BigDecimalMath.toBigDecimal(tempStack.pop())
            val num2 = BigDecimalMath.toBigDecimal(stack.pop())
            val result = num1.multiply(num2, precision)
            tempStack.push(result.toString())
        } else {
            tempStack.push(temp)
        }
    }
    tempStack.reverse()
    return tempStack
}

fun solveDivision(stack: Stack<String>): Stack<String> {
    val tempStack = Stack<String>()
    var temp: String
    while (stack.isNotEmpty()) {
        temp = stack.pop()
        if (temp == "/") {
            val precision = MathContext(20)
            val num1 = BigDecimalMath.toBigDecimal(tempStack.pop())
            val num2 = BigDecimalMath.toBigDecimal(stack.pop())
            if (num2.compareTo(BigDecimal.ZERO) == 0)
                throw CalculationException("Cannot divide by 0")
            val result = num1.divide(num2, precision)
            tempStack.push(result.toString())
        } else {
            tempStack.push(temp)
        }
    }
    tempStack.reverse()
    return tempStack
}

private fun solveTrigonometricExpression(stack: Stack<String>, angleType: String): Stack<String> {
    val tempStack = Stack<String>()
    val isDegree = (angleType == AngleType.DEG.name)
    var temp: String
    while (stack.isNotEmpty()) {
        temp = stack.pop()
        when (temp) {
            "sin", "-sin" -> {
                var num = stack.pop().toDouble()
                if (!num.isFinite())
                    throw CalculationException("Value too large")
                if (isDegree) {
                    if (num.rem(180) == 0.0) {
                        tempStack.push("0.0")
                        continue
                    }
                    num = Math.toRadians(num)
                } else {
                    if (num.rem(Math.PI) == 0.0) {
                        tempStack.push("0.0")
                        continue
                    }
                }
                num = sin(num)
                if (temp == "-sin")
                    num = num.unaryMinus()
                tempStack.push(num.toString())
            }
            "cos", "-cos" -> {
                var num = stack.pop().toDouble()
                if (!num.isFinite())
                    throw CalculationException("Value too large")
                if (isDegree) {
                    if (num.rem(90) == 0.0 && num.rem(180) != 0.0) {
                        tempStack.push("0.0")
                        continue
                    }
                    num = Math.toRadians(num)
                } else {
                    if (num.rem(Math.PI / 2) == 0.0 && num.rem(Math.PI) != 0.0) {
                        tempStack.push("0.0")
                        continue
                    }
                }
                num = cos(num)
                if (temp == "-cos")
                    num = num.unaryMinus()
                tempStack.push(num.toString())
            }
            "tan", "-tan" -> {
                var num = stack.pop().toDouble()
                if (!num.isFinite())
                    throw CalculationException("Value too large")
                if (isDegree) {
                    if (num.rem(180) == 0.0) {
                        tempStack.push("0.0")
                        continue
                    } else if (num.rem(90) == 0.0)
                        throw CalculationException("Domain error")
                    num = Math.toRadians(num)
                } else {
                    if (num.rem(Math.PI) == 0.0) {
                        tempStack.push("0.0")
                        continue
                    } else if (num.rem(Math.PI / 2) == 0.0)
                        throw CalculationException("Domain error")
                }
                num = tan(num)
                if (temp == "-tan")
                    num = num.unaryMinus()
                tempStack.push(num.toString())
            }
            "sin⁻¹", "-sin⁻¹" -> {
                var num = stack.pop().toDouble()
                if (num > 1 || num < -1)
                    throw CalculationException("Domain error")
                if (!num.isFinite())
                    throw CalculationException("Value too large")
                num = if (isDegree) {
                    Math.toDegrees(asin(num))
                } else {
                    asin(num)
                }
                if (temp == "-sin⁻¹")
                    num = num.unaryMinus()
                tempStack.push(num.toString())
            }
            "cos⁻¹", "-cos⁻¹" -> {
                var num = stack.pop().toDouble()
                if (num > 1 || num < -1)
                    throw CalculationException("Domain error")
                num = if (isDegree) {
                    Math.toDegrees(acos(num))
                } else {
                    acos(num)
                }
                if (temp == "-cos⁻¹")
                    num = num.unaryMinus()
                tempStack.push(num.toString())
            }
            "tan⁻¹", "-tan⁻¹" -> {
                var num = stack.pop().toDouble()
                if (!num.isFinite())
                    throw CalculationException("Value too large")
                num = if (isDegree) {
                    Math.toDegrees(atan(num))
                } else {
                    atan(num)
                }
                if (temp == "-tan⁻¹")
                    num = num.unaryMinus()
                tempStack.push(num.toString())
            }
            "log", "-log" -> {
                var num = stack.pop().toDouble()
                if (num < 0)
                    throw CalculationException("Domain error")
                if (!num.isFinite())
                    throw CalculationException("Value too large")
                num = log10(num)
                if (temp == "-log")
                    num = num.unaryMinus()
                tempStack.push(num.toString())
            }
            "ln", "-ln" -> {
                var num = stack.pop().toDouble()
                if (num < 0)
                    throw CalculationException("Domain error")
                if (!num.isFinite())
                    throw CalculationException("Value too large")
                num = ln(num)
                if (temp == "-ln")
                    num = num.unaryMinus()
                tempStack.push(num.toString())
            }
            else -> tempStack.push(temp)
        }
    }
    tempStack.reverse()
    return tempStack
}

private fun solveLeftUnary(stack: Stack<String>): Stack<String> {
    val tempStack = Stack<String>()
    var temp: String
    while (stack.isNotEmpty()) {
        temp = stack.pop()
        when (temp) {
            "√", "-√", "∛", "-∛" -> {
                var nextToken = stack.peek()
                val num = if (nextToken[0].isLeftUnaryOperator()) {
                    val gg = Stack<String>()
                    while (stack.isNotEmpty() && nextToken[0].isLeftUnaryOperator()) {
                        gg.push(stack.pop())
                        if (stack.isNotEmpty())
                            nextToken = stack.peek()
                    }
                    if (stack.peek().isNumber()) {
                        gg.push(stack.pop())
                    }
                    if (temp == "∛" || temp == "-∛") {
                        cbrt(solveRoots(gg)).toString()
                    } else {
                        sqrt(solveRoots(gg)).toString()
                    }
                } else {
                    val number = stack.pop().toDouble()
                    if (!number.isFinite())
                        throw CalculationException("Value too large")
                    if (temp == "∛" || temp == "-∛") {
                        cbrt(number).toString()
                    } else {
                        sqrt(number).toString()
                    }
                }
                if (temp == "-√" || temp == "-∛")
                    tempStack.push("-$num")
                else
                    tempStack.push(num)
            }
            else -> tempStack.push(temp)
        }
    }
    tempStack.reverse()
    return tempStack
}

private fun solveRightUnary(stack: Stack<String>): Stack<String> {
    val tempStack = Stack<String>()
    var temp: String
    while (stack.isNotEmpty()) {
        temp = stack.pop()
        when (temp) {
            "%" -> {
                val precision = MathContext(20)
                var num = BigDecimalMath.toBigDecimal(tempStack.pop())
                num = if (tempStack.size >= 2 && tempStack.peek() == "+") {
                    tempStack.pop()
                    val s = Stack<String>()
                    while (tempStack.isNotEmpty()) {
                        s.push(tempStack.pop())
                    }
                    val stepResult = BigDecimalMath.toBigDecimal(solveExpression(s))
                    num = num.divide(BigDecimal.valueOf(100), precision)
                    num = num.multiply(stepResult, precision)
                    num.add(stepResult, precision)
                } else {
                    num.divide(BigDecimal.valueOf(100), precision)
                }
                tempStack.push(num.toString())
            }
            "!" -> {
                if (!Pattern.matches("-?\\d+(\\.0)?", tempStack.peek())) {
                    throw CalculationException("Domain error")
                }
                val precision = MathContext(20)
                var number = tempStack.pop()
                val result = if (number.startsWith("-")) {
                    number = number.substring(1)
                    val num = BigDecimalMath.toBigDecimal(number)
                    "-${BigDecimalMath.factorial(num, precision)}"
                } else {
                    val num = BigDecimalMath.toBigDecimal(number)
                    BigDecimalMath.factorial(num, precision).toString()
                }
                tempStack.push(result)
            }
            else -> {
                tempStack.push(temp)
            }
        }
    }
    tempStack.reverse()
    return tempStack
}

private fun solvePower(stack: Stack<String>): Stack<String> {
    val tempStack = Stack<String>()
    var temp: String
    while (stack.isNotEmpty()) {
        temp = stack.pop()
        if (temp == "^") {
            val precision = MathContext(20)
            val num1 = BigDecimalMath.toBigDecimal(tempStack.pop())
            val num2 = BigDecimalMath.toBigDecimal(stack.pop())
            if (num2.compareTo(BigDecimal.ONE) == 0) {
                tempStack.push(num1.toString())
            } else {
                val result = BigDecimalMath.pow(num1, num2, precision)
                if (num1.compareTo(BigDecimal.ZERO) == -1) {
                    tempStack.push("-$result")
                } else {
                    tempStack.push(result.toString())
                }
            }
        } else {
            tempStack.push(temp)
        }
    }
    tempStack.reverse()
    return tempStack
}

//returns factorial of a number
private fun factorial(num: Int): BigInteger {
    var offset = BigInteger("1")
    var n = num
    if (n < 0) {
        offset = BigInteger("-1")
        n = -num
    }
    // Initialize result
    var result = BigInteger.ONE

    // Multiply f with 2, 3, ...N
    for (i in 2..n)
        result = result.multiply(BigInteger.valueOf(i.toLong()))

    return result.multiply(offset)
}

private fun solveRoots(stack: Stack<String>): Double {
    var num = stack.pop().toDouble()
    while (stack.isNotEmpty()) {
        val kk = stack.pop()
        if (kk == "√") {
            num = sqrt(num)
        }
        if (kk == "∛") {
            num = cbrt(num)
        }
    }
    return num
}

fun canPlaceDecimal(expression: String): Boolean {
    var j = expression.lastIndex
    var count = 0
    while (j >= 0 && !expression[j].isOperator()) {
        if (expression[j] == '.') {
            count++
            break
        }
        j--
    }
    return count == 0
}

fun String.containsNumber(): Boolean {
    for (i in this.indices) {
        val char = this[i]
        if (char.isNumber()) {
            return true
        }
    }
    return false
}

fun String.isNumber(): Boolean {
    return Pattern.matches("-?\\d+(\\.\\d+)?", this)
}

fun Char.isNumber(): Boolean {
    return (isDigit() || isConstant())
}

fun Char.isOperator(): Boolean {
    return (isUnaryOperator() || isBinaryOperator())
}

fun Char.isUnaryOperator(): Boolean {
    return (isLeftUnaryOperator() || isRightUnaryOperator())
}

fun Char.isLeftUnaryOperator(): Boolean {
    val charSet = setOf('\u221a', '\u221b')
    return when (this) {
        in charSet -> true
        else -> false
    }
}

fun Char.isRightUnaryOperator(): Boolean {
    val charSet = setOf('%', '!')
    return when (this) {
        in charSet -> true
        else -> false
    }
}

fun Char.isBinaryOperator(): Boolean {
    val charSet = setOf('^', '-', '\u2212', '+', '\u002B', 'x', '*', '\u00D7', '/', '\u00F7')
    return when (this) {
        in charSet -> true
        else -> false
    }
}

fun Char.isConstant(): Boolean {
    return (isPi() || isEulerNumber())
}

fun Char.isPi(): Boolean {
    return this == '\u03C0'
}

fun Char.isEulerNumber(): Boolean {
    return this == 'e'
}

fun Char.isTrigonometricChar(): Boolean {
    return when (this) {
        '\u00b9', '\u207B', 's', 'i', 'n', 'c', 'o', 't', 'a', 'l', 'g' -> true
        else -> false
    }
}