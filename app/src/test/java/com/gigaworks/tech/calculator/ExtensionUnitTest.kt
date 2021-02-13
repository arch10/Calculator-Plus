package com.gigaworks.tech.calculator

import com.gigaworks.tech.calculator.ui.main.helper.isOperator
import org.junit.Assert.assertEquals
import org.junit.Test

class ExtensionUnitTest {

    @Test
    fun testIsOperator() {
        assertEquals(true, '+'.isOperator())
    }

}