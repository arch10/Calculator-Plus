package com.gigaworks.tech.calculator.ui.main.helper

import com.gigaworks.tech.calculator.util.AngleType
import com.gigaworks.tech.calculator.util.CalculationException
import com.gigaworks.tech.calculator.util.CalculationMessage
import com.gigaworks.tech.calculator.util.NumberSeparator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class AsyncEvaluateUnitTest {

    private val deg = AngleType.DEG.name

    @Test
    fun testAsyncEvaluationMatchesSynchronousEvaluation() = runBlocking {
        assertEquals("", evaluateExpression("", deg))
        assertEquals("10", evaluateExpression("5+5", deg))
        assertEquals("3.333333", evaluateExpression("10/3", deg))
        assertEquals("120", evaluateExpression("5!", deg))
        assertEquals("1", evaluateExpression("sin(90)", deg))
        assertEquals("56", evaluateExpression("(5+9)*8/2", deg))
    }

    @Test
    fun testAsyncEvaluationHonoursPrecisionAndSeparator() = runBlocking {
        assertEquals("3.33", evaluateExpression("10/3", deg, precision = 2))
        assertEquals(
            "1,000,000",
            evaluateExpression("1000*1000", deg, separator = NumberSeparator.INTERNATIONAL)
        )
        assertEquals(
            "10,00,000",
            evaluateExpression("1000*1000", deg, separator = NumberSeparator.INDIAN)
        )
    }

    @Test
    fun testCalculationErrorsStillSurface() = runBlocking {
        assertMessage(CalculationMessage.DIVIDE_BY_ZERO) { evaluateExpression("5/0", deg) }
        assertMessage(CalculationMessage.DOMAIN_ERROR) { evaluateExpression("5.5!", deg) }
    }

    @Test
    fun testEvaluationTimesOut() = runBlocking {
        assertMessage(CalculationMessage.TIMEOUT) {
            evaluateExpression(SLOW_EXPRESSION, deg, timeoutMillis = TEST_TIMEOUT_MS)
        }
    }

    @Test
    fun testTimedOutCalculationDoesNotBlockTheNextOne() = runBlocking {
        val start = System.currentTimeMillis()
        assertMessage(CalculationMessage.TIMEOUT) {
            evaluateExpression(SLOW_EXPRESSION, deg, timeoutMillis = TEST_TIMEOUT_MS)
        }
        assertEquals("10", evaluateExpression("5+5", deg))
        assertTrue(System.currentTimeMillis() - start < CALCULATION_TIMEOUT_MS)
    }

    @Test
    fun testInterruptedEvaluationStopsEarly() {
        //what makes the timeout more than a stopwatch: the evaluator drops out of its loops
        //once the thread running it is interrupted
        Thread.currentThread().interrupt()
        try {
            getResult("5+5", deg)
            fail("Expected the interrupted evaluation to stop")
        } catch (_: InterruptedException) {
        } finally {
            //clear the flag so it cannot leak into the next test on this thread
            Thread.interrupted()
        }
    }

    @Test
    fun testCancellingTheCallerIsNotReportedAsATimeout() = runBlocking {
        val scope = CoroutineScope(Job())
        val deferred = scope.async { evaluateExpression(SLOW_EXPRESSION, deg) }
        deferred.cancel()
        try {
            deferred.await()
            fail("Expected the cancelled evaluation to produce no result")
        } catch (e: CalculationException) {
            fail("Cancellation must not be reported as ${e.msg}")
        } catch (_: Exception) {
            //a cancelled caller gets cancellation, not a calculation error
        }
    }

    private inline fun assertMessage(expected: CalculationMessage, block: () -> Unit) {
        try {
            block()
            fail("Expected a CalculationException with $expected")
        } catch (e: CalculationException) {
            assertEquals(expected, e.msg)
        }
    }

    companion object {
        //this factorial is exact internally, so it builds a number with hundreds of
        //thousands of digits: quick to type, slow enough to freeze a UI thread
        private const val SLOW_EXPRESSION = "50000!"
        private const val TEST_TIMEOUT_MS = 10L
    }
}
