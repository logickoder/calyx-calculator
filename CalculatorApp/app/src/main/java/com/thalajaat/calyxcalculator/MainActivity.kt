package com.thalajaat.calyxcalculator

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.thalajaat.calyxcalculator.data.datasources.local.Coins
import com.thalajaat.calyxcalculator.data.datasources.local.room.ConversionDatabase
import com.thalajaat.calyxcalculator.data.datasources.local.room.ConversionDbRepo
import com.thalajaat.calyxcalculator.data.datasources.local.room.DropDownRateEntity
import com.thalajaat.calyxcalculator.databinding.ActivityMainBinding
import com.thalajaat.calyxcalculator.databinding.MessageDialogViewBinding
import com.thalajaat.calyxcalculator.databinding.PopupLayoutBinding
import com.thalajaat.calyxcalculator.domain.Arithemetics
import com.thalajaat.calyxcalculator.domain.CalculationHandler
import com.thalajaat.calyxcalculator.presentation.uis.adapter.Convert
import com.thalajaat.calyxcalculator.presentation.uis.adapter.PinnedConiRecyclerViewAdapter
import com.thalajaat.calyxcalculator.presentation.uis.adapter.RatesAdapter
import com.thalajaat.calyxcalculator.presentation.uis.widgets.xml.ExampleAppWidgetProvider
import com.thalajaat.calyxcalculator.presentation.viewmodels.CalculatorViewModel
import com.thalajaat.calyxcalculator.presentation.viewmodels.CalculorViewModelFactory
import com.thalajaat.calyxcalculator.utils.Utils.flatten
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), Convert {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val calculatorRepository by lazy {
        val repository = ConversionDbRepo(
            ConversionDatabase.getDatabase(this).converstionDao()
        ) // Create a repository instance
        val factory = CalculorViewModelFactory(repository, CalculationHandler())
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
                if (calculatorViewModel.rateState.value.filter { it.isPinned }.size < 4) {
                    calculatorViewModel.pin(it)
                } else {
                    showPinMessage()
                }
            }
        }
    }
    private val pinnedadapter by lazy {
        PinnedConiRecyclerViewAdapter(this, this, calculationHandler) {
            val value = calculationHandler.getAnswer().value
            calculatorViewModel.convertCurrency(value.substringBefore("  "), it, onError = {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        init()
    }


    override fun onPause() {
        val intent = Intent(this, ExampleAppWidgetProvider::class.java)
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:
        val ids: IntArray = AppWidgetManager.getInstance(application)
            .getAppWidgetIds(
                android.content.ComponentName(
                    application,
                    ExampleAppWidgetProvider::class.java
                )
            )
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
        super.onPause()
    }


    var job: Job? = null
    var oldDialog: PopupWindow? = null
    private fun init() {
        val marginDp = 16
// Convert dp to pixels
        val marginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            marginDp.toFloat(),
            resources.displayMetrics
        ).toInt()

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
                if (mOutput.text.isNotEmpty()) {
                    calculationHandler.clearInput()
                    calculationHandler.addValue(mOutput.text.split(" ").first().toString())
                }
            }
            buttonAns.setOnClickListener {
                if (mOutput.text.isNotEmpty()) {
                    calculationHandler.clearInput()
                    calculationHandler.addValue(mOutput.text.split(" ").first().toString())
                }
            }
            buttonDivision.setAritheieticListener(Arithemetics.DIVIDE)
            buttonEqualTo.setOnClickListener {
                if (mOutput.text.isNotEmpty()) {
                    calculationHandler.clearInput()
                    calculationHandler.addValue(mOutput.text.split(" ").first().toString())
                }
                calculationHandler.calculate(onError = {

                }) {
                    calculationHandler.addValue(it.split(" ").first().toString())
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
                    oldDialog?.dismiss()
                    oldDialog = this
                    job?.cancel()
                    // Set an elevation value for the popup window
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        elevation = 10.0f
                    }

                    val recyclerview = popupView.ratesList
                    val filter = popupView.search.doOnTextChanged { text, start, before, count ->

                        adapter.setItems(
                            calculatorViewModel.rateState.value.sortedBy { it.start }
                                .filter {
                                    it.end.contains(
                                        text.toString().uppercase()
                                    ) || it.start.contains(text.toString().uppercase())
                                })
                    }
                    recyclerview.layoutManager =
                        LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                    recyclerview.adapter = adapter


                    job = lifecycleScope.launch {
                        calculatorViewModel.rates.collectLatest {
                            if (it.isEmpty()) {
                                calculatorViewModel.addItems()
                            }
                            val coins = it
                            adapter.setItems(coins)
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
                    showAsDropDown(view, -30, 0)

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
                    input.text = it.flatten()
                }

            }
            lifecycleScope.launch {
                calculationHandler.getAnswer().collect {
                    ensureActive()
                    mOutput.text = it
                }
            }
            lifecycleScope.launch {
                calculationHandler.getTotalCurrency().collect {
                    ensureActive()
                    when (it.isNotEmpty()) {
                        true -> conversionRateOutputLayout.visibility = View.VISIBLE
                        else -> conversionRateOutputLayout.visibility = View.GONE
                    }
                    conversionRateOutput.text = it
                    conversionRateOutput.visibility = View.VISIBLE
                }
            }
            buttonDelete.setOnLongClickListener {
                calculationHandler.clearInputForAll()
                true
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

    override fun onClickConvert(item: DropDownRateEntity, enteredValue: String) {
        val currentAnswer = calculationHandler.getAnswer().value.flatten()
        calculatorViewModel.convertCurrency(
            currentAnswer,
            item,
            {},
            onError = { },
            conversionError = {error ->
               Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
            })
    }

    private fun showPinMessage() {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
        val messageDialog = MessageDialogViewBinding.inflate(layoutInflater)
        builder.setView(messageDialog.root)
        messageDialog.dialogButtonOkay.setOnClickListener { builder.dismiss() }
        builder.apply {
            setCanceledOnTouchOutside(true)
            show()
        }
    }
}