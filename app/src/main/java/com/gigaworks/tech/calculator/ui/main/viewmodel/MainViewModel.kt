package com.gigaworks.tech.calculator.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gigaworks.tech.calculator.domain.History
import com.gigaworks.tech.calculator.repository.HistoryRepository
import com.gigaworks.tech.calculator.ui.main.helper.*
import com.gigaworks.tech.calculator.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
            setError("")
            calculatedExpression = expression
            prepareExpression(expression)
        } else {
            val exp = tryBalancingBrackets(expression)
            if (getSmartCalculation() && isExpressionBalanced(exp)) {
                setError("")
                calculatedExpression = expression
                prepareExpression(exp)
            } else {
                setError("Invalid expression")
                ""
            }
        }
        val rawResult = getResult(exp)
        val result = roundMyAnswer(rawResult, getAnswerPrecision())
        val formattedResult = if (getNumberSeparator() != NumberSeparator.OFF) {
            addNumberSeparator(
                expression = result,
                isIndian = (getNumberSeparator() == NumberSeparator.INDIAN)
            )
        } else {
            result
        }
        setResult(formattedResult)
    }

    private fun getResult(expression: String): String {
        return try {
            getResult(expression, getAngleType())
        } catch (e: CalculationException) {
            val errorMessage = when (e.msg) {
                CalculationMessage.INVALID_EXPRESSION -> "Invalid expression"
                CalculationMessage.DIVIDE_BY_ZERO -> "Cannot divide by 0"
                CalculationMessage.VALUE_TOO_LARGE -> "Value too large"
                CalculationMessage.DOMAIN_ERROR -> "Domain error"
            }
            setError(errorMessage)
            ""
        } catch (e: Exception) {
            setError("Error")
            ""
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