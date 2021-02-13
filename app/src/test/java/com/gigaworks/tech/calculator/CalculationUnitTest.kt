package com.gigaworks.tech.calculator

import com.gigaworks.tech.calculator.ui.main.helper.addNumberSeparator
import com.gigaworks.tech.calculator.ui.main.helper.isOperator
import com.gigaworks.tech.calculator.ui.main.helper.removeFromEndUntil
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CalculationUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testNumberSeparatorInternational() {
        val expression = "6554656455"
        assertEquals("6,554,656,455", addNumberSeparator(expression = expression, isIndian = false))
    }

    @Test
    fun testNumberSeparatorIndian() {
        val expression = "6554656455"
        assertEquals("6,55,46,56,455", addNumberSeparator(expression = expression, isIndian = true))
    }

    @Test
    fun testRemoveFromEnd() {
        val expression = "9+15.25*5+-"
        val result = removeFromEndUntil(expression) {
            it.isOperator()
        }
        assertEquals("9+15.25*5", result)
    }
}