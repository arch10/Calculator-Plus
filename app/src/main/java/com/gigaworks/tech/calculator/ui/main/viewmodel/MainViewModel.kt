package com.gigaworks.tech.calculator.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.obermuhlner.math.big.BigDecimalMath
import com.gigaworks.tech.calculator.domain.History
import com.gigaworks.tech.calculator.repository.HistoryRepository
import com.gigaworks.tech.calculator.ui.main.helper.*
import com.gigaworks.tech.calculator.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appPreference: AppPreference,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    var isPrevResult: Boolean = false

    private var calculatedExpression: String = ""

    private val _result = MutableLiveData<String>()
    val result: LiveData<String>
        get() = _result

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private fun setResult(result: String) {
        _result.value = result
    }

    private fun setError(error: String) {
        _error.value = error
    }

    fun calculateExpression(expression: String) {
        val exp = if (isExpressionBalanced(expression)) {
            _error.value = ""
            prepareExpression(expression)
        } else {
            val exp = tryBalancingBrackets(expression)
            if (getSmartCalculation() && isExpressionBalanced(exp)) {
                _error.value = ""
                prepareExpression(exp)
            } else {
                _error.value = "Invalid expression"
                ""
            }
        }
        try {
            val rawResult = getResult(exp)
            val result = roundMyAnswer(rawResult)
            val formattedResult = if (getNumberSeparator() != NumberSeparator.OFF) {
                addNumberSeparator(
                    expression = result,
                    isIndian = (getNumberSeparator() == NumberSeparator.INDIAN)
                )
            } else {
                result
            }
            setResult(formattedResult)
        } catch (e: Exception) {
            when (e) {
                is TimeoutCancellationException, is OutOfMemoryError -> {
                    setError("Timed Out")
                }
                else -> {
                    setError("Error")
                }
            }
        }
    }

    private fun prepareExpression(expression: String): String {
        if (expression.isEmpty()) return ""

        var exp = expression
        calculatedExpression = exp

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

    private fun getResult(expression: String): String {
        if (expression.isEmpty()) return ""
        var exp = expression
        var lastChar = exp.last()

        while (!canBeLastChar(lastChar)) {
            exp = exp.dropLast(1)
            if (exp.isNotEmpty()) {
                lastChar = exp.last()
            } else {
                setError("Invalid expression")
                return ""
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
                try {
                    stack.push(solveExpression(newStack, getAngleType()))
                } catch (e: CalculationException) {
                    setError(e.msg)
                    return ""
                } catch (e: Exception) {
                    setError("Invalid expression")
                    return ""
                }
            } else {
                temp.append(char)
            }
        }
        if (temp.isNotEmpty()) {
            stack.push(temp.toString())
        }
        stack.reverse()

        return try {
            solveExpression(stack, getAngleType())
        } catch (e: Exception) {
            when (e) {
                is CalculationException -> setError(e.msg)
                else -> setError("Invalid expression")
            }
            ""
        }
    }

    //rounds the provided number to user preference digits
    private fun roundMyAnswer(ans: String): String {
        if (ans.isEmpty())
            return ""
        var num = BigDecimalMath.toBigDecimal(ans)
        return if (ans.contains("E"))
            formatNumber(num, 7)
        else {
            val precision = getAnswerPrecision()
            num = num.setScale(precision, RoundingMode.HALF_UP)
            num = num.stripTrailingZeros()
            if (num.compareTo(BigDecimal.ZERO) == 0) {
                "0"
            } else {
                num.toPlainString()
            }
        }
    }

    fun getAppTheme(): String {
        return appPreference.getStringPreference(
            AppPreference.APP_THEME,
            AppTheme.SYSTEM_DEFAULT.name
        )
    }

    fun getCalculatedExpression() = calculatedExpression

    fun getMemory(): String {
        return appPreference.getStringPreference(AppPreference.MEMORY, "")
    }

    fun setMemory(value: String) {
        if (value.isNumber()) {
            appPreference.setStringPreference(AppPreference.MEMORY, value)
        }
    }

    fun getSavedExpression(): String {
        return appPreference.getStringPreference(AppPreference.EXPRESSION, "")
    }

    fun saveExpression(value: String) {
        appPreference.setStringPreference(AppPreference.EXPRESSION, value)
    }

    private fun getAnswerPrecision(): Int {
        return appPreference.getIntPreference(AppPreference.ANSWER_PRECISION, 6)
    }

    private fun getSmartCalculation(): Boolean {
        return appPreference.getBooleanPreference(AppPreference.SMART_CALCULATION, true)
    }

    fun getNumberSeparator(): NumberSeparator {
        val numberSeparator = appPreference.getStringPreference(
            AppPreference.NUMBER_SEPARATOR,
            NumberSeparator.INTERNATIONAL.name
        )
        return NumberSeparator.values().find { it.name == numberSeparator }
            ?: NumberSeparator.INTERNATIONAL
    }

    fun getAngleType(): String {
        return appPreference.getStringPreference(AppPreference.ANGLE_TYPE, AngleType.DEG.name)
    }

    fun changeAngleType(angleType: AngleType) {
        appPreference.setStringPreference(AppPreference.ANGLE_TYPE, angleType.name)
    }

    fun insertHistory(history: History) {
        viewModelScope.launch {
            historyRepository.saveHistory(history)
        }
    }

}