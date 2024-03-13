package com.thalajaat.calyxcalculator.dormain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.mariuszgromada.math.mxparser.Expression
import java.text.DecimalFormat


class CalculationHandler :CalculationHandlerInterface {

    private var expression = MutableStateFlow( "0")
    private var answer = MutableStateFlow( "0")
    private var totalCurrencyConversion = MutableStateFlow("")

    override fun getExpression():MutableStateFlow<String> = expression
    override fun getAnswer(): MutableStateFlow<String> = answer
    override fun getTotalCurrency(): MutableStateFlow<String> = totalCurrencyConversion
    override fun setAnswer(d: Double,suffix:String) {

            val result = DecimalFormat("0.##").format(d).toString()
            totalCurrencyConversion.value = suffix
            answer.value = result
            // Show Result
    }

    override fun clearOutput() {
        answer.value = ""
    }

    override fun clearInput() {
        expression.value = ""
        totalCurrencyConversion.value = ""
    }

    override fun addValue(value: String){
        expression.value += value

            try {
                val e = Expression(expression.value)
                val result = e.calculate()
                if (result.isNaN()) {
                    return
                } else {
                    answer.value = DecimalFormat("0.##").format(result).toString()
                }
            }
            catch (e:Exception){
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
        }
        catch (e:Exception) {
            expression.value ="0"
        }
    }

    override fun addArithemetic(value: Arithemetics){

        when(value){
            Arithemetics.ADD -> {
                expression.value+="+"
            }
            Arithemetics.SUBTRACT -> {
                expression.value+="-"
            }
            Arithemetics.MODULUS -> {
                expression.value+="%"
                addValue("*")
            }
            Arithemetics.MULTIPLY -> {
                expression.value+="*"
            }
            Arithemetics.DIVIDE -> {
                expression.value+="/"
            }
        }

    }

    override fun calculate(onError: () -> Unit, onCalculationResponse: (String) -> Unit) {
        try {
            val e = Expression(expression.value)
            val result = e.calculate()
            if (result.isNaN()) {
                onError()
            } else {
                expression.value = "0"
                answer.value = DecimalFormat("0.######").format(result).toString()
                // Show Result
                onCalculationResponse(answer.value)
            }
        }
        catch (e:Exception){
            e.printStackTrace()
            onError()
        }
    }
}




interface CalculationHandlerInterface {

    fun addValue(value: String)
    fun removeValue()

    fun addArithemetic(value: Arithemetics)

    fun calculate(onError:()->Unit,onCalculationResponse:(String)->Unit)

    fun getExpression():MutableStateFlow<String>
    fun getAnswer():MutableStateFlow<String>
    fun setAnswer(d: Double,suffix:String)
    fun getTotalCurrency() : MutableStateFlow<String>
    fun clearOutput()
    fun clearInput()
}



enum class Arithemetics{
    ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULUS
}