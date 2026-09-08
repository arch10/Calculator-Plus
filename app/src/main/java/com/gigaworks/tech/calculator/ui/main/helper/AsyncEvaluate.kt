package com.gigaworks.tech.calculator.ui.main.helper

import com.gigaworks.tech.calculator.util.CalculationException
import com.gigaworks.tech.calculator.util.CalculationMessage.TIMEOUT
import com.gigaworks.tech.calculator.util.NumberSeparator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.concurrent.Executors

const val CALCULATION_TIMEOUT_MS = 10_000L

//A timed out calculation is abandoned rather than stopped: the evaluator only notices
//cancellation where it polls ensureCalculationActive(), and a single big-math call in
//between can outlive the timeout. Giving those threads their own elastic pool keeps them
//from occupying Dispatchers.Default, which the rest of the app shares.
private val calculationExecutor = Executors.newCachedThreadPool { runnable ->
    Thread(runnable, "calculation").apply { isDaemon = true }
}

val calculationDispatcher: CoroutineDispatcher = calculationExecutor.asCoroutineDispatcher()

/**
 * Evaluates [expression] off the calling thread and formats the answer.
 *
 * The whole pipeline runs in the background because every stage of it scales with the size
 * of the answer, not the size of the expression: `50000!` is quick to type and produces a
 * number with hundreds of thousands of digits that then has to be rounded and separated.
 *
 * @throws CalculationException with [TIMEOUT] when the work outlives [timeoutMillis].
 */
suspend fun evaluateExpression(
    expression: String,
    angleType: String,
    precision: Int = 6,
    separator: NumberSeparator = NumberSeparator.OFF,
    timeoutMillis: Long = CALCULATION_TIMEOUT_MS,
    dispatcher: CoroutineDispatcher = calculationDispatcher
): String = withContext(dispatcher) {
    try {
        withTimeout(timeoutMillis) {
            runInterruptible {
                val rawResult = getResult(expression, angleType)
                val result = roundMyAnswer(rawResult, precision)
                if (separator == NumberSeparator.OFF) {
                    result
                } else {
                    addNumberSeparator(
                        expression = result,
                        isIndian = separator == NumberSeparator.INDIAN
                    )
                }
            }
        }
    } catch (_: TimeoutCancellationException) {
        throw CalculationException(TIMEOUT)
    }
}
