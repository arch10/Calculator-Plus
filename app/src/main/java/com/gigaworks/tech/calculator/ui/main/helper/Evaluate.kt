package com.gigaworks.tech.calculator.ui.main.helper

import ch.obermuhlner.math.big.BigDecimalMath
import com.gigaworks.tech.calculator.util.CalculationException
import com.gigaworks.tech.calculator.util.CalculationMessage.INVALID_EXPRESSION
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

//cleans up expression before evaluating
fun prepareExpression(expression: String): String {
    if (expression.isEmpty()) return ""

    var exp = expression

    //remove number separator
    exp = removeNumberSeparator(exp)

    //replace human readable operator characters with computer readable operator characters
    exp = exp.replace("÷", "/")
    exp = exp.replace("×", "*")
    exp = exp.replace("+", "+")
    exp = exp.replace("−", "-")

    //replace constants with their values
    exp = exp.replace("e", Math.E.toString())
    exp = exp.replace("π", Math.PI.toString())

    exp = exp.replace("-", "+-")

    //corrective replace
    exp = exp.replace("^+-", "^-")
    exp = exp.replace("(+-", "(-")
    exp = exp.replace("*+", "*")
    exp = exp.replace("/+", "/")
    exp = exp.replace("++", "+")

    exp = exp.replace("+)", ")")
    exp = exp.replace("-)", ")")
    exp = exp.replace("/)", ")")
    exp = exp.replace("*)", ")")
    exp = exp.replace(".)", ")")
    exp = exp.replace("^)", ")")

    return exp
}

//solves the given expression and returns the result
fun getResult(expression: String, angleType: String): String {
    if (expression.isEmpty()) return ""
    var exp = expression
    var lastChar = exp.last()

    while (!canBeLastChar(lastChar)) {
        exp = exp.dropLast(1)
        if (exp.isNotEmpty()) {
            lastChar = exp.last()
        } else {
            throw CalculationException(INVALID_EXPRESSION)
        }
    }

    if (exp[0] == '+' || exp[0] == '-') {
        exp = "0$exp"
    }

    val stack = Stack<String>()
    val temp = StringBuilder()
    for (i in exp.indices) {
        val char = exp[i]
        if (char.isOperator() || char == '(') {
            if (temp.isNotEmpty()) {
                stack.push(temp.toString())
                temp.clear()
            }
            stack.push(char.toString())
        } else if (char == ')') {
            if (temp.isNotEmpty()) {
                stack.push(temp.toString())
                temp.clear()
            }
            val newStack = Stack<String>()
            while (!stack.empty() && stack.peek() != "(") {
                newStack.push(stack.pop())
            }
            stack.pop()
            stack.push(solveExpression(newStack, angleType))
        } else {
            temp.append(char)
        }
    }
    if (temp.isNotEmpty()) {
        stack.push(temp.toString())
    }
    stack.reverse()

    return solveExpression(stack, angleType)
}

//rounds the provided number to the given precision
fun roundMyAnswer(ans: String, precision: Int = 6): String {
    if (ans.isEmpty())
        return ""
    var num = BigDecimalMath.toBigDecimal(ans)
    return if (ans.contains("E"))
        formatNumber(num, 7)
    else {
        num = num.setScale(precision, RoundingMode.HALF_UP)
        num = num.stripTrailingZeros()
        if (num.compareTo(BigDecimal.ZERO) == 0) {
            "0"
        } else {
            num.toPlainString()
        }
    }
}