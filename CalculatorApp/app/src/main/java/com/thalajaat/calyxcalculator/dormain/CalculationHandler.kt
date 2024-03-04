package com.thalajaat.calyxcalculator.dormain

import org.mariuszgromada.math.mxparser.Expression
import java.text.DecimalFormat


class CalculationHandler():CalculationHandlerInterface {

    private var expression = ""

    override fun addValue(value: String){
        expression += value
    }

    override fun addArithemetic(value: Arithemetics){

        when(value){
            Arithemetics.ADD -> {
                expression+="+"
            }
            Arithemetics.SUBTRACT -> {
                expression+="-"
            }
            Arithemetics.MODULUS -> {
                expression+="%"
            }
            Arithemetics.MULTIPLY -> {
                expression+="*"
            }
            Arithemetics.DIVIDE -> {
                expression+="/"
            }
        }

    }

    override fun calculate(onError: () -> Unit, onCalculationResponse: (String) -> Unit) {
        try {
            val e = Expression(expression)
            val result = e.calculate()
            if (result.isNaN()) {
                onError()
            } else {
                // Show Result
                onCalculationResponse(DecimalFormat("0.######").format(result).toString())
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

    fun addArithemetic(value: Arithemetics)

    fun calculate(onError:()->Unit,onCalculationResponse:(String)->Unit)
}



enum class Arithemetics{
    ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULUS
}