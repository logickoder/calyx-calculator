package com.thalajaat.calyxcalculator.domain

import com.thalajaat.calyxcalculator.utils.Utils.flatten
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.mariuszgromada.math.mxparser.Expression
import timber.log.Timber
import java.text.DecimalFormat


class CalculationHandler : CalculationHandlerInterface {

    private var expression = MutableStateFlow("0")
    private var answer = MutableStateFlow("0")
    private var totalCurrencyConversion = MutableStateFlow("")

    override fun getExpression(): MutableStateFlow<String> = expression
    override fun getAnswer(): MutableStateFlow<String> = answer
    override fun getTotalCurrency(): MutableStateFlow<String> = totalCurrencyConversion
    override fun setAnswer(d: Double, suffix: String) {
        val result = d.answerFormatter()
        totalCurrencyConversion.value = suffix
        answer.value = result
        // Show Result
    }

    override fun clearOutput() {
        answer.value = ""
    }

    override fun clearInputForAll() {
        answer.value = "0"
        totalCurrencyConversion.value = ""
        expression.value = "0"
    }

    override fun clearInput() {
        expression.value = ""
        totalCurrencyConversion.value = ""
    }

    override fun addValue(value: String) {
        if (value.equals(".")) {
            if (expression.value.endsWithSpecialCharacter()) {
                expression.value = expression.value.dropLast(1)
            }
        }

        expression.value += value

        try {
            val e = Expression(convertPercentageExpressions(insertMultiplicationAfterPercentage(expression.value.flatten())))
            val result = e.calculate()
            if (!result.isNaN()) {
                answer.value = result.answerFormatter()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateExpression() {
        try {
            val e = Expression(expression.value.flatten())
            val result = e.calculate()
            if (!result.isNaN()) {
                answer.value = result.answerFormatter()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun removeValue() {
        totalCurrencyConversion.value = ""
        answer.value = "0"
        try {
            if (expression.value.length == 1) {
                expression.value = "0"
                return
            }
            expression.update {
                it.dropLast(1)
            }
            addValue("")
        } catch (e: Exception) {
            expression.value = "0"
        }
    }

    override fun addArithemetic(value: Arithemetics) {

        if (expression.value.endsWithOperator()) {
            expression.value = expression.value.dropLast(1)
        }

        if (value == Arithemetics.MODULUS) {
            if (expression.value.endsWith("%")) {
                return
            }
        }

        when (value) {
            Arithemetics.ADD -> {
                expression.value += "+"
            }

            Arithemetics.SUBTRACT -> {
                expression.value += "-"
            }

            Arithemetics.MODULUS -> {
                expression.value += "%"
                try {
                    val e = Expression(convertPercentageExpressions(insertMultiplicationAfterPercentage(expression.value)))
                    val result = e.calculate()
                    if (result.isNaN()) {

                    } else {
                        answer.value = result.answerFormatter()
                        // Show Result

                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                }

            }

            Arithemetics.MULTIPLY -> {
                expression.value += "*"
            }

            Arithemetics.DIVIDE -> {
                expression.value += "/"
            }
        }

    }
    private fun insertMultiplicationAfterPercentage(expression: String): String {
        // Regular expression to find a number followed by '%' and another number
        val regex = Regex("(\\d+)%\\s*(\\d+)")

        // Replace matched patterns with the first number followed by '%*', then the second number
        return expression.replace(regex) { matchResult ->
            "${matchResult.groupValues[1]}%*${matchResult.groupValues[2]}"
        }
    }
    private fun String.endsWithOperator(): Boolean {
        // Regex pattern to check if the string ends with +, -, *, %, or /
        val pattern = Regex("[+\\-*/]$")
        return pattern.containsMatchIn(this)
    }

    override fun calculate(onError: () -> Unit, onCalculationResponse: (String) -> Unit) {
        try {
            val e = Expression(convertPercentageExpressions(insertMultiplicationAfterPercentage(expression.value)))
            val result = e.calculate()
            if (result.isNaN()) {
                onError()
            } else {
                expression.value = "0"
                answer.value = result.answerFormatter()
                // Show Result
                onCalculationResponse(answer.value)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onError()
        }
    }

    private fun convertPercentageExpressions(expression: String): String {
        // Pattern to match expressions like 3+2%, 10-2%, etc.
        val regex = Regex("(\\d+)([+\\-*/])(\\d+)%")

        // Replace each match in the original string
        val value = regex.replace(expression) { matchResult ->
            // Extract the components of the match
            val (number1, operator, number2) = matchResult.destructured

            // Convert the matched expression into the desired format
            when (operator) {
                "+" -> "(${number2}%*${number1})+${number1}"
                "-" -> "${number1}-(${number2}%*${number1})"
                "*" -> "${number2}%*${number1}" // This case is already correct, but we include it for completeness
                "/" -> "${number2}%/${number1}"
                else -> matchResult.value // Should never happen, but included for completeness
            }
        }
        Timber.tag("VALUE IS").d(value)
        Timber.tag("Expression IS").d(expression)
        return value;
    }
}


interface CalculationHandlerInterface {

    fun addValue(value: String)
    fun removeValue()

    fun addArithemetic(value: Arithemetics)

    fun calculate(onError: () -> Unit, onCalculationResponse: (String) -> Unit)

    fun getExpression(): MutableStateFlow<String>
    fun getAnswer(): MutableStateFlow<String>
    fun setAnswer(d: Double, suffix: String)
    fun getTotalCurrency(): MutableStateFlow<String>
    fun clearOutput()
    fun clearInput()
    fun clearInputForAll()
}

fun Double.answerFormatter(): String {
    val formatter = DecimalFormat("###,###,##0.##")
    return formatter.format(this)
}

fun String.endsWithSpecialCharacter(): Boolean {
    // Regex pattern to check if the last character is +, -, *, /, ., or %
    val pattern = Regex("[+\\-*/.%]$")
    return this.matches(pattern)
}

enum class Arithemetics {
    ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULUS
}