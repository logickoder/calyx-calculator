package com.thalajaat.calyxcalculator

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.thalajaat.calyxcalculator.data.datasources.local.room.ConversionDatabase
import com.thalajaat.calyxcalculator.data.datasources.local.room.ConversionDbRepo
import com.thalajaat.calyxcalculator.databinding.ActivityMainBinding
import com.thalajaat.calyxcalculator.databinding.PopupLayoutBinding
import com.thalajaat.calyxcalculator.dormain.Arithemetics
import com.thalajaat.calyxcalculator.presentation.uis.adapter.PinnedConiRecyclerViewAdapter
import com.thalajaat.calyxcalculator.presentation.uis.adapter.RatesAdapter
import com.thalajaat.calyxcalculator.presentation.viewmodels.CalculatorViewModel
import com.thalajaat.calyxcalculator.presentation.viewmodels.CalculorViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val calculatorRepository by lazy {
        val repository = ConversionDbRepo(
            ConversionDatabase.getDatabase(this).converstionDao()
        ) // Create a repository instance
        val factory = CalculorViewModelFactory(repository)
        factory
    }

    private val calculatorViewModel by lazy {
        ViewModelProvider(this, calculatorRepository)[CalculatorViewModel::class.java]
    }

    private val calculationHandler by lazy {
        calculatorViewModel.geHandler
    }

    val adapter by lazy {
        RatesAdapter(this) {
            if (it.isPinned) {
                calculatorViewModel.unpin(it)
            } else {
                calculatorViewModel.pin(it)
            }
        }
    }
    val pinnedadapter by lazy {
        PinnedConiRecyclerViewAdapter(this) {

            calculatorViewModel.convertCurrency(it){
                Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        init()
    }

    var job: Job? = null

    private fun init() {
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

            pinnedcoinslist.layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            pinnedcoinslist.adapter = pinnedadapter
            lifecycleScope.launch {
                calculatorViewModel.rates.map { it.filter { it.isPinned } }.collectLatest {
                    pinnedadapter.setItems(it)
                }
            }
            buttonAdd.setAritheieticListener(Arithemetics.ADD)
            buttonSubtraction.setAritheieticListener(Arithemetics.SUBTRACT)
            buttonMultiplication.setAritheieticListener(Arithemetics.MULTIPLY)
            buttonPeriod.setClickListeners(".")
            mOutput.setOnClickListener {
                if(mOutput.text.isNotEmpty()){
                    calculationHandler.addValue(mOutput.text.toString())
                }
            }
            buttonDivision.setAritheieticListener(Arithemetics.DIVIDE)
            buttonEqualTo.setOnClickListener {
                calculationHandler.calculate(onError = {

                }) {

                }
            }
            buttonPercentage.setAritheieticListener(Arithemetics.MODULUS)

            buttonMore.setOnClickListener { view ->
                // Inflate the popup layout
                val popupView = PopupLayoutBinding.inflate(layoutInflater)
                val popupWindow = PopupWindow(
                    popupView.root,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    job?.cancel()
                    // Set an elevation value for the popup window
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        elevation = 10.0f
                    }

                    val recyclerview = popupView.ratesList
                    val filter = popupView.search.doOnTextChanged { text, start, before, count ->

                        adapter.setItems(
                            calculatorViewModel.rateState.value
                                .filter { it.end.contains(text.toString()) || it.start.contains(text.toString()) })
                    }
                    recyclerview.layoutManager =
                        LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                    recyclerview.adapter = adapter


                    job = lifecycleScope.launch {
                        calculatorViewModel.rates.collectLatest {
                            if (it.isEmpty()) {
                                calculatorViewModel.addItems()
                            }
                            adapter.setItems(it)
                        }
                    }

                    // If you need to dismiss the popup when touched outside
                    isOutsideTouchable = true

                    // If you need the popup to be focusable (e.g., if it contains input elements)
                    isFocusable = true

                    // Optional: animate the popup
                    animationStyle = android.R.style.Animation_Dialog

                    // Apply the background drawable
                    setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this@MainActivity,
                            R.drawable.popup_background
                        )
                    )

                    // Show the popup window below the icon
                    showAsDropDown(view)
                }
            }

            lifecycleScope.launch {
                ensureActive()
                calculationHandler.getExpression().map {
                    it.replace("/", "÷")
                        .replace("*", "×").replace("#", "%")
                }.map {
                    if (it.length > 1) {
                        if (it[1].toString() != ".") {
                            return@map it.trimStart("0".toCharArray().first())
                        }
                        it
                    } else {
                        return@map it
                    }
                }.collect {
                    ensureActive()
                    input.text = it
                }

            }
            lifecycleScope.launch {
                calculationHandler.getAnswer().collect {
                    ensureActive()
                    mOutput.text = it
                }
            }

        }
    }

    private fun View.setClickListeners(value: String) {
        setOnClickListener {
            calculationHandler.addValue(value)
        }
    }

    private fun View.setAritheieticListener(value: Arithemetics) {
        setOnClickListener {
            calculationHandler.addArithemetic(value)
        }
    }

    private fun View.setFunctionListener() {
        setOnClickListener {
            calculationHandler.removeValue()
        }
    }
}