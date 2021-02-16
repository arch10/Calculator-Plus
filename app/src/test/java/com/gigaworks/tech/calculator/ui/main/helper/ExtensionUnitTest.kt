package com.gigaworks.tech.calculator.ui.main.helper

import org.junit.Assert.assertEquals
import org.junit.Test

class ExtensionUnitTest {

    @Test
    fun testIsOperator() {
        assertEquals(true, '^'.isOperator())
        assertEquals(true, '-'.isOperator())
        assertEquals(true, '\u2212'.isOperator())
        assertEquals(true, '+'.isOperator())
        assertEquals(true, '\u002B'.isOperator())
        assertEquals(true, 'x'.isOperator())
        assertEquals(true, '*'.isOperator())
        assertEquals(true, '\u00d7'.isOperator())
        assertEquals(true, '/'.isOperator())
        assertEquals(true, '\u00f7'.isOperator())
        assertEquals(true, '%'.isOperator())
        assertEquals(true, '!'.isOperator())
        assertEquals(true, 'x'.isOperator())
        assertEquals(true, '\u221a'.isOperator())
        assertEquals(true, '\u221b'.isOperator())
        assertEquals(false, 'c'.isOperator())
        assertEquals(false, ')'.isOperator())
    }

    @Test
    fun testIsNumber() {
        assertEquals(true, '5'.isNumber())
        assertEquals(false, '*'.isNumber())
        assertEquals(true, "2554.65".isNumber())
        assertEquals(true, "-2554.65".isNumber())
        assertEquals(false, "+2,554.65".isNumber())
    }

    @Test
    fun testIsConstant() {
        assertEquals(true, '\u03C0'.isConstant())
        assertEquals(true, 'e'.isConstant())
        assertEquals(false, 's'.isConstant())
        assertEquals(false, '9'.isConstant())
    }

    @Test
    fun testIsTrigonometricChar() {
        assertEquals(true, 's'.isTrigonometricChar())
        assertEquals(true, 'c'.isTrigonometricChar())
        assertEquals(true, 't'.isTrigonometricChar())
        assertEquals(true, 'n'.isTrigonometricChar())
        assertEquals(true, 'o'.isTrigonometricChar())
        assertEquals(false, 'y'.isTrigonometricChar())
        assertEquals(false, '^'.isTrigonometricChar())
        assertEquals(false, '8'.isTrigonometricChar())
    }

}