package com.gigaworks.tech.calculator.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gigaworks.tech.calculator.R
import com.gigaworks.tech.calculator.domain.History
import com.gigaworks.tech.calculator.repository.HistoryRepository
import com.gigaworks.tech.calculator.ui.main.helper.addNumberSeparator
import com.gigaworks.tech.calculator.ui.main.helper.getResult
import com.gigaworks.tech.calculator.ui.main.helper.isExpressionBalanced
import com.gigaworks.tech.calculator.ui.main.helper.isNumber
import com.gigaworks.tech.calculator.ui.main.helper.prepareExpression
import com.gigaworks.tech.calculator.ui.main.helper.roundMyAnswer
import com.gigaworks.tech.calculator.ui.main.helper.tryBalancingBrackets
import com.gigaworks.tech.calculator.util.AngleType
import com.gigaworks.tech.calculator.util.AppPreference
import com.gigaworks.tech.calculator.util.AppTheme
import com.gigaworks.tech.calculator.util.CalculationException
import com.gigaworks.tech.calculator.util.CalculationMessage
import com.gigaworks.tech.calculator.util.NumberSeparator
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

    private val _error = MutableLiveData(-1)
    val error: LiveData<Int>
        get() = _error

    private fun setResult(result: String) {
        _result.value = result
    }

    private fun setError(error: Int) {
        _error.value = error
    }

    fun updateLaunchStatistics() {
        var launchCount = appPreference.getLongPreference(AppPreference.LAUNCH_COUNT)
        val lastLaunchDate = appPreference.getLongPreference(AppPreference.LAST_LAUNCH_DAY)
        if (lastLaunchDate == 0L) {
            val currentDateTime = System.currentTimeMillis()
            appPreference.setLongPreference(AppPreference.LAST_LAUNCH_DAY, currentDateTime)
        }
        launchCount += 1
        appPreference.setLongPreference(AppPreference.LAUNCH_COUNT, launchCount)
    }

    fun calculateExpression(expression: String) {
        val exp = if (isExpressionBalanced(expression)) {
            setError(-1)
            calculatedExpression = expression
            prepareExpression(expression)
        } else {
            val exp = tryBalancingBrackets(expression)
            if (getSmartCalculation() && isExpressionBalanced(exp)) {
                setError(-1)
                calculatedExpression = expression
                prepareExpression(exp)
            } else {
                setError(R.string.invalid)
                ""
            }
        }
        try {
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
        } catch (e: CalculationException) {
            val errorMessage = when (e.msg) {
                CalculationMessage.INVALID_EXPRESSION -> R.string.invalid
                CalculationMessage.DIVIDE_BY_ZERO -> R.string.divide_by_zero
                CalculationMessage.VALUE_TOO_LARGE -> R.string.value_too_large
                CalculationMessage.DOMAIN_ERROR -> R.string.domain_error
            }
            setError(errorMessage)
            setResult("")
        } catch (e: Exception) {
            setError(R.string.error)
            setResult("")
        }

    }

    private fun getResult(expression: String): String {
        return getResult(expression, getAngleType())
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
        return NumberSeparator.entries.find { it.name == numberSeparator }
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