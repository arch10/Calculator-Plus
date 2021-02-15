package com.gigaworks.tech.calculator.ui.main.helper

import org.junit.Assert.assertEquals
import org.junit.Test

class HandleButtonClickTest {

    @Test
    fun testHandleBinaryOperatorClick() {
        assertEquals("", handleClick("", "+"))
        assertEquals("5+", handleClick("5", "+"))
        assertEquals("5!+", handleClick("5!", "+"))
        assertEquals("5+4)+", handleClick("5+4)", "+"))
        assertEquals("5.0+", handleClick("5.", "+"))
        assertEquals("5+", handleClick("5-", "+"))
        assertEquals("-", handleClick("-", "+"))
        assertEquals("(-", handleClick("(-", "+"))
        assertEquals("5+(-", handleClick("5+(-", "+"))
        assertEquals("√", handleClick("√", "+"))
        assertEquals("√-", handleClick("√-", "+"))
    }

    @Test
    fun testHandleMinusClick() {
        assertEquals("-", handleClick("", "-"))
        assertEquals("5-", handleClick("5", "-"))
        assertEquals("5!-", handleClick("5!", "-"))
        assertEquals("5+4)-", handleClick("5+4)", "-"))
        assertEquals("5.0-", handleClick("5.", "-"))
        assertEquals("5+", handleClick("5-", "-"))
        assertEquals("-", handleClick("-", "-"))
        assertEquals("(-", handleClick("(-", "-"))
        assertEquals("5+(-", handleClick("5+(-", "-"))
        assertEquals("√-", handleClick("√", "-"))
    }

    @Test
    fun testHandleNumberClick() {
        assertEquals("5", handleClick("", "5"))
        assertEquals("55", handleClick("5", "5"))
        assertEquals("5!×5", handleClick("5!", "5"))
        assertEquals("5+4)×5", handleClick("5+4)", "5"))
        assertEquals("5.5", handleClick("5.", "5"))
        assertEquals("5-5", handleClick("5-", "5"))
        assertEquals("-5", handleClick("-", "5"))
        assertEquals("(-5", handleClick("(-", "5"))
        assertEquals("5+(-5", handleClick("5+(-", "5"))
        assertEquals("√5", handleClick("√", "5"))
    }

    @Test
    fun testHandleTrigonometricClick() {
        assertEquals("sin(", handleClick("", "sin"))
        assertEquals("5×sin(", handleClick("5", "sin"))
        assertEquals("5!×sin(", handleClick("5!", "sin"))
        assertEquals("5+4)×sin(", handleClick("5+4)", "sin"))
        assertEquals("5.0×sin(", handleClick("5.", "sin"))
        assertEquals("5-sin(", handleClick("5-", "sin"))
        assertEquals("-sin(", handleClick("-", "sin"))
        assertEquals("(-sin(", handleClick("(-", "sin"))
        assertEquals("5+(-sin(", handleClick("5+(-", "sin"))
        assertEquals("√sin(", handleClick("√", "sin"))
    }

    @Test
    fun testHandleRightUnaryClick() {
        assertEquals("", handleClick("", "!"))
        assertEquals("5!", handleClick("5", "!"))
        assertEquals("5!", handleClick("5!", "!"))
        assertEquals("5+4)!", handleClick("5+4)", "!"))
        assertEquals("5.0!", handleClick("5.", "!"))
        assertEquals("5!", handleClick("5-", "!"))
        assertEquals("-", handleClick("-", "!"))
        assertEquals("(-", handleClick("(-", "!"))
        assertEquals("5+(-", handleClick("5+(-", "!"))
        assertEquals("√", handleClick("√", "!"))
    }

    @Test
    fun testHandleLeftUnaryClick() {
        assertEquals("√", handleClick("", "√"))
        assertEquals("5×√", handleClick("5", "√"))
        assertEquals("5!×√", handleClick("5!", "√"))
        assertEquals("5+4)×√", handleClick("5+4)", "√"))
        assertEquals("5.0×√", handleClick("5.", "√"))
        assertEquals("5-√", handleClick("5-", "√"))
        assertEquals("-√", handleClick("-", "√"))
        assertEquals("(-√", handleClick("(-", "√"))
        assertEquals("5+(-√", handleClick("5+(-", "√"))
        assertEquals("√√", handleClick("√", "√"))
    }

    @Test
    fun testHandleConstantClick() {
        assertEquals("π", handleClick("", "π"))
        assertEquals("5×π", handleClick("5", "π"))
        assertEquals("5!×π", handleClick("5!", "π"))
        assertEquals("5+4)×π", handleClick("5+4)", "π"))
        assertEquals("5.0×π", handleClick("5.", "π"))
        assertEquals("5-π", handleClick("5-", "π"))
        assertEquals("-π", handleClick("-", "π"))
        assertEquals("(-π", handleClick("(-", "π"))
        assertEquals("5+(-π", handleClick("5+(-", "π"))
        assertEquals("√π", handleClick("√", "π"))
    }

    @Test
    fun testHandleOpenBracketClick() {
        assertEquals("(", handleClick("", "("))
        assertEquals("5×(", handleClick("5", "("))
        assertEquals("5!×(", handleClick("5!", "("))
        assertEquals("5+4)×(", handleClick("5+4)", "("))
        assertEquals("5.0×(", handleClick("5.", "("))
        assertEquals("5-(", handleClick("5-", "("))
        assertEquals("-(", handleClick("-", "("))
        assertEquals("(-(", handleClick("(-", "("))
        assertEquals("5+(-(", handleClick("5+(-", "("))
        assertEquals("√(", handleClick("√", "("))
    }

    @Test
    fun testHandleCloseBracketClick() {
        assertEquals("", handleClick("", ")"))
        assertEquals("5)", handleClick("5", ")"))
        assertEquals("5!)", handleClick("5!", ")"))
        assertEquals("5+4))", handleClick("5+4)", ")"))
        assertEquals("5.0)", handleClick("5.", ")"))
        assertEquals("5-", handleClick("5-", ")"))
        assertEquals("-", handleClick("-", ")"))
        assertEquals("(-", handleClick("(-", ")"))
        assertEquals("5+(-", handleClick("5+(-", ")"))
        assertEquals("√", handleClick("√", ")"))
    }

    @Test
    fun testHandleDecimalClick() {
        assertEquals("0.", handleClick("", "."))
        assertEquals("5.", handleClick("5", "."))
        assertEquals("5!×0.", handleClick("5!", "."))
        assertEquals("5+4)×0.", handleClick("5+4)", "."))
        assertEquals("5.", handleClick("5.", "."))
        assertEquals("5-0.", handleClick("5-", "."))
        assertEquals("-0.", handleClick("-", "."))
        assertEquals("(-0.", handleClick("(-", "."))
        assertEquals("5+(-0.", handleClick("5+(-", "."))
        assertEquals("√0.", handleClick("√", "."))
    }

    @Test
    fun testHandleDelete() {
        assertEquals("", handleDelete(""))
        assertEquals("", handleDelete("5"))
        assertEquals("5", handleDelete("5!"))
        assertEquals("5+4", handleDelete("5+4)"))
        assertEquals("5", handleDelete("5."))
        assertEquals("5", handleDelete("5-"))
        assertEquals("", handleDelete("-"))
        assertEquals("(", handleDelete("(-"))
        assertEquals("5+(", handleDelete("5+(-"))
        assertEquals("", handleDelete("√"))
        assertEquals("", handleDelete("sin("))
        assertEquals("", handleDelete("sin⁻¹("))
    }

}