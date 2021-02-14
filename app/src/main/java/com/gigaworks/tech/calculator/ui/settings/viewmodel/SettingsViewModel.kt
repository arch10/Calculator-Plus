package com.gigaworks.tech.calculator.ui.settings.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gigaworks.tech.calculator.ui.settings.helper.getDays
import com.gigaworks.tech.calculator.util.AppPreference
import com.gigaworks.tech.calculator.util.AppPreference.Companion.APP_THEME
import com.gigaworks.tech.calculator.util.AppTheme
import com.gigaworks.tech.calculator.util.HistoryAutoDelete
import com.gigaworks.tech.calculator.util.NumberSeparator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreference: AppPreference
) : ViewModel() {

    private val _selectedTheme = MutableLiveData(getSelectedTheme())
    val selectedTheme: LiveData<AppTheme>
        get() = _selectedTheme

    private val _smartCalculation = MutableLiveData(getSmartCalculation())
    val smartCalculation: LiveData<Boolean>
        get() = _smartCalculation

    private val _numberSeparator = MutableLiveData(getNumberSeparator())
    val numberSeparator: LiveData<NumberSeparator>
        get() = _numberSeparator

    private val _autoDeleteHistory = MutableLiveData(getAutoDeleteHistory())
    val autoDeleteHistory: LiveData<HistoryAutoDelete>
        get() = _autoDeleteHistory

    private val _precision = MutableLiveData(getAnswerPrecision())
    val precision: LiveData<Int>
        get() = _precision

    fun changeTheme(themeId: Int) {
        val theme = getAppThemeByOrdinal(themeId)
        appPreference.setStringPreference(APP_THEME, theme.name)
        val themeMode = when (theme) {
            AppTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            AppTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(themeMode)
        _selectedTheme.value = theme
    }

    private fun getSelectedTheme(): AppTheme {
        val themeName = appPreference.getStringPreference(APP_THEME, AppTheme.SYSTEM_DEFAULT.name)
        return try {
            AppTheme.valueOf(themeName)
        } catch (e: IllegalArgumentException) {
            AppTheme.SYSTEM_DEFAULT
        }
    }

    private fun getAppThemeByOrdinal(ordinal: Int): AppTheme {
        return AppTheme.values().find { it.ordinal == ordinal } ?: AppTheme.SYSTEM_DEFAULT
    }

    fun getAutoDeleteHistory(): HistoryAutoDelete {
        val days = appPreference.getIntPreference(AppPreference.HISTORY_AUTO_DELETE, -1)
        return HistoryAutoDelete.values().find { it.days == days } ?: HistoryAutoDelete.NEVER
    }

    fun setAutoDeleteHistory(value: HistoryAutoDelete) {
        _autoDeleteHistory.value = value
        appPreference.setIntPreference(AppPreference.HISTORY_AUTO_DELETE, value.getDays())
    }

    fun getAnswerPrecision(): Int {
        return appPreference.getIntPreference(AppPreference.ANSWER_PRECISION, 6)
    }

    fun setAnswerPrecision(precision: Int) {
        if (precision in 2..10) {
            _precision.value = precision
            appPreference.setIntPreference(AppPreference.ANSWER_PRECISION, precision)
        }
    }

    fun getSmartCalculation(): Boolean {
        return appPreference.getBooleanPreference(AppPreference.SMART_CALCULATION, true)
    }

    fun setSmartCalculation(isEnabled: Boolean) {
        _smartCalculation.value = isEnabled
        appPreference.setBooleanPreference(AppPreference.SMART_CALCULATION, isEnabled)
    }

    fun getNumberSeparator(): NumberSeparator {
        val numberSeparator = appPreference.getStringPreference(
            AppPreference.NUMBER_SEPARATOR,
            NumberSeparator.INTERNATIONAL.name
        )
        return NumberSeparator.values().find { it.name == numberSeparator }
            ?: NumberSeparator.INTERNATIONAL
    }

    fun changeNumberSeparator(numberSeparator: NumberSeparator) {
        _numberSeparator.value = numberSeparator
        appPreference.setStringPreference(AppPreference.NUMBER_SEPARATOR, numberSeparator.name)
    }

}