package com.thalajaat.calyxcalculator.dormain

import com.thalajaat.calyxcalculator.utils.Utils.flatten
import kotlinx.coroutines.flow.MutableStateFlow
import org.mariuszgromada.math.mxparser.Expression
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
        if (value.endsWith("%")) {
            val percentageValue = value.removeSuffix("%").toDoubleOrNull() ?: return
            val regex = Regex("([+\\-*/])?(\\d+)$")
            val matchResult = regex.find(expression.value)

            matchResult?.let {
                val operator = it.groups[1]?.value ?: "+"
                val lastNumber = it.groups[2]?.value?.toDoubleOrNull() ?: return

                // Calculate the percentage of the last number
                val percentageResult = lastNumber * (percentageValue / 100)

                // Append the calculated percentage based on the operator
                when (operator) {
                    "+" -> expression.value = "${expression.value}+${percentageResult}"
                    "-" -> expression.value = "${expression.value}-${percentageResult}"
                    else -> expression.value += "*($percentageValue/100)"
                }
            }
        } else {
            expression.value += value
        }

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
        if (expression.value.isNotEmpty()) {
            expression.value = expression.value.dropLast(1)
            if (expression.value.isEmpty()) {
                expression.value = "0"
            }
        }

        try {
            val e = Expression(expression.value.flatten())
            val result = e.calculate()
            if (!result.isNaN()) {
                answer.value = result.answerFormatter()
            } else {
                answer.value = "0"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            expression.value = "0"
            answer.value = "0"
        }
    }

    override fun addArithemetic(value: Arithemetics) {
        val operator = when (value) {
            Arithemetics.ADD -> "+"
            Arithemetics.SUBTRACT -> "-"
            Arithemetics.MULTIPLY -> "*"
            Arithemetics.DIVIDE -> "/"
            Arithemetics.MODULUS -> "%" // Handle within addValue for actual calculation
        }
        addValue(operator)
    }

    override fun calculate(onError: () -> Unit, onCalculationResponse: (String) -> Unit) {
        try {
            val e = Expression(expression.value.flatten())
            val result = e.calculate()
            if (result.isNaN()) {
                onError()
            } else {
                expression.value = result.toString()
                answer.value = result.answerFormatter()
                onCalculationResponse(answer.value)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onError()
        }
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

enum class Arithemetics {
    ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULUS
}
