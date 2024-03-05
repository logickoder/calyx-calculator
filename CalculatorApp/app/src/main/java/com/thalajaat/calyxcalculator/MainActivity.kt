package com.thalajaat.calyxcalculator

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thalajaat.calyxcalculator.databinding.ActivityMainBinding
import com.thalajaat.calyxcalculator.dormain.Arithemetics
import com.thalajaat.calyxcalculator.presentation.viewmodels.CalculatorViewModel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val binding by lazy{ ActivityMainBinding.inflate(layoutInflater)}

    private val calculatorViewModel = viewModels<CalculatorViewModel>()

    private val calculationHandler by lazy{
        calculatorViewModel.value.geHandler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        init()
    }


    private fun init(){
        binding.apply {
            button0.setClickListeners("0")
            button1.setClickListeners("1")
            button2.setClickListeners("2")
            button3.setClickListeners("3")
            button4.setClickListeners("4")
            button5.setClickListeners("5")
            button6.setClickListeners("6")
            button7.setClickListeners("7")
            button8.setClickListeners("8")
            button9.setClickListeners("9")

            buttonDelete.setFunctionListener()

            buttonAdd.setAritheieticListener(Arithemetics.ADD)
            buttonSubtraction.setAritheieticListener(Arithemetics.SUBTRACT)
            buttonMultiplication.setAritheieticListener(Arithemetics.MULTIPLY)
            buttonPeriod.setClickListeners(".")
            buttonDivision.setAritheieticListener(Arithemetics.DIVIDE)
            buttonEqualTo.setOnClickListener {
                calculationHandler.calculate(onError = {

                }){

                }
            }
            buttonPercentage.setAritheieticListener(Arithemetics.MODULUS)

            lifecycleScope.launch {
                ensureActive()
                calculationHandler.getExpression().map { it.replace("/","÷")
                    .replace("*","×").replace("#","%")
                }.map {
                    if(it.length>1){
                        if(it[1].toString()!="."){
                            return@map it.trimStart("0".toCharArray().first())
                        }
                        it
                    }
                    else{
                       return@map it
                    }
                } .collect {
                    ensureActive()
                    input.text = it
                }
            }

        }
    }

    private fun View.setClickListeners(value:String){
        setOnClickListener {
            calculationHandler.addValue(value)
        }
    }

    private fun View.setAritheieticListener(value:Arithemetics){
        setOnClickListener {
            calculationHandler.addArithemetic(value)
        }
    }

    private fun View.setFunctionListener(){
        setOnClickListener {
            calculationHandler.removeValue()
        }
    }
}