package com.gigaworks.tech.calculator.ui.main.helper

import ch.obermuhlner.math.big.BigDecimalMath
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculationUnitTest {

    @Test
    fun testNumberSeparatorInternational() {
        val expression = "6554656455"
        assertEquals("6,554,656,455", addNumberSeparator(expression = expression, isIndian = false))
        assertEquals(
            "1.5457758E10",
            addNumberSeparator(expression = "1.5457758E10", isIndian = false)
        )
        assertEquals(
            "6,566+65,688",
            addNumberSeparator(expression = "6566+65688", isIndian = false)
        )
    }

    @Test
    fun testNumberSeparatorIndian() {
        val expression = "6554656455"
        assertEquals("6,55,46,56,455", addNumberSeparator(expression = expression, isIndian = true))
        assertEquals(
            "6,66,566+65,688",
            addNumberSeparator(expression = "666566+65688", isIndian = true)
        )
    }

    @Test
    fun testRemoveNumberSeparator() {
        val expression = "6,55,46,56,455"
        assertEquals("6554656455", removeNumberSeparator(expression = expression))
    }

    @Test
    fun testRemoveFromEnd() {
        val expression = "9+15.25*5+-"
        val result = removeFromEndUntil(expression) {
            it.isOperator()
        }
        assertEquals("9+15.25*5", result)
        assertEquals("", removeFromEndUntil("") { it.isOperator() })
    }

    @Test
    fun testFormatNumber() {
        val num1 = BigDecimalMath.toBigDecimal("65456554646.65656")
        assertEquals("6.5456555E10", formatNumber(num1, 7))
        val num2 = BigDecimalMath.toBigDecimal("65656")
        assertEquals("6.5656000E4", formatNumber(num2, 7))
        val num3 = BigDecimalMath.toBigDecimal("-5655455454")
        assertEquals("-5.6554555E9", formatNumber(num3, 7))
    }

    @Test
    fun testIsExpressionBalanced() {
        assertEquals(true, isExpressionBalanced("(66+6*((4-2)/2))"))
        assertEquals(false, isExpressionBalanced("(66+6*(4-2)/2))"))
        assertEquals(false, isExpressionBalanced("(66+6*((4-2)/2)"))
    }

    @Test
    fun testTryBalancingBrackets() {
        assertEquals("65+(8-2)", tryBalancingBrackets("65+(8-2"))
        assertEquals("(6)+2", tryBalancingBrackets("6)+2"))
        assertEquals("6+2*2*", tryBalancingBrackets("6+2*2*("))
        assertEquals("(6)*(6)", tryBalancingBrackets("6)*(6"))
    }
}