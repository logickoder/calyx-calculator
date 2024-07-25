package com.thalajaat.calyxcalculator.presentation.uis.widgets.xml

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import com.google.gson.Gson
import com.thalajaat.calyxcalculator.MainActivity
import com.thalajaat.calyxcalculator.R
import com.thalajaat.calyxcalculator.data.datasources.local.room.ConversionDatabase
import com.thalajaat.calyxcalculator.data.datasources.local.room.ConversionDbRepo
import com.thalajaat.calyxcalculator.data.datasources.local.room.DropDownRateEntity
import com.thalajaat.calyxcalculator.domain.Arithemetics
import com.thalajaat.calyxcalculator.domain.CalculationHandler
import com.thalajaat.calyxcalculator.presentation.viewmodels.CalculatorViewModel
import com.thalajaat.calyxcalculator.utils.Utils.flatten
import kotlinx.coroutines.*

fun DropDownRateEntity.getName(): String {
    return "${start ?: ""}/${end ?: ""}"
}

class ExampleAppWidgetProvider : AppWidgetProvider() {

    private lateinit var notesRepository: ConversionDbRepo
    val onebuttonclick = "1"
    val twobuttonclick = "2"
    val threebuttonclick = "3"
    val fourbuttonclick = "4"
    val fivebuttonclick = "5"
    val sixbuttonclick = "6"
    val sevenbuttonclick = "7"
    val eightbuttonclick = "8"
    val ninebuttonclick = "9"
    val zerobuttonclick = "0"
    val addbuttonclick = Arithemetics.ADD.name
    val subtractbuttonclick = Arithemetics.SUBTRACT.name
    val dividebuttonclick = Arithemetics.DIVIDE.name
    val multiplybuttonclick = Arithemetics.MULTIPLY.name
    val modulusbuttonclick = Arithemetics.MODULUS.name
    val clear = "clear"
    val dot = "."
    val rate = "rate"
    val open = "open"
    val submit = "submit"
    val EXTRA = "EXTRA"
    val answer = "answer"
    companion object {
        var list = emptyList<DropDownRateEntity>()
        val calculationHandler = CalculationHandler()
        fun getVM(context: Context) = CalculatorViewModel(
            ConversionDbRepo(ConversionDatabase.getDatabase(context).conversionDao()),
            calculationHandler
        )
    }

    private var coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        remoteViews: RemoteViews
    ) {
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Perform this loop procedure for each widget that belongs to this provider.
        val widgetManager = AppWidgetManager.getInstance(context.applicationContext)
        val remoteViews = RemoteViews(context.packageName, R.layout.layout_calculator_widget)

        Log.d("Value", "onUpdate: called")

        coroutineScope.launch {
            try {
                val rates = getVM(context).offlineRepository.getDropDownRate2().filter { it.isPinned }
                Log.d("Value", rates.toString())
                withContext(Dispatchers.Main) {
                    var text = calculationHandler.getExpression().value
                        .replace("/", "÷")
                        .replace("*", "×")
                        .replace("#", "%")
                    text = if (text.length > 1 && text[1].toString() != ".") {
                        text.trimStart('0')
                    } else {
                        text
                    }
                    val text2 = calculationHandler.getAnswer().value
                    remoteViews.setTextViewText(R.id.input, text)
                    remoteViews.setTextViewText(R.id.output, text2)

                    // Update conversion rates
                    updateConversionRates(remoteViews, context, rates)

                    appWidgetIds.forEach {
                        updateAppWidget(context, appWidgetManager, it, remoteViews)
                    }
                }
            } catch (e: Exception) {
                Log.e("AppWidgetProvider", "Error updating widget", e)
            }
        }

        setPendingIntents(context, remoteViews)

        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId, remoteViews)
        }
    }

    private fun updateConversionRates(
        remoteViews: RemoteViews,
        context: Context,
        rates: List<DropDownRateEntity>
    ) {
        val first = rates.getOrNull(0)
        val second = rates.getOrNull(1)
        val third = rates.getOrNull(2)
        val fourth = rates.getOrNull(3)

        if (first != null) {
            remoteViews.setTextViewText(R.id.first_rate, first.getName())
            remoteViews.setOnClickPendingIntent(
                R.id.button_first_conversion,
                getPendingSelfIntent(context, rate + 1, first.toJson())
            )
            remoteViews.setViewVisibility(R.id.button_first_conversion, View.VISIBLE)
        } else {
            remoteViews.setViewVisibility(R.id.button_first_conversion, View.GONE)
        }

        if (second != null) {
            remoteViews.setTextViewText(R.id.second_rate, second.getName())
            remoteViews.setOnClickPendingIntent(
                R.id.button_second_conversion,
                getPendingSelfIntent(context, rate + 2, second.toJson())
            )
            remoteViews.setViewVisibility(R.id.button_second_conversion, View.VISIBLE)
        } else {
            remoteViews.setViewVisibility(R.id.button_second_conversion, View.GONE)
        }

        if (third != null) {
            remoteViews.setTextViewText(R.id.third_rate, third.getName())
            remoteViews.setOnClickPendingIntent(
                R.id.button_third_conversion,
                getPendingSelfIntent(context, rate + 3, third.toJson())
            )
            remoteViews.setViewVisibility(R.id.button_third_conversion, View.VISIBLE)
        } else {
            remoteViews.setViewVisibility(R.id.button_third_conversion, View.GONE)
        }

        if (fourth != null) {
            remoteViews.setTextViewText(R.id.fourth_rate, fourth.getName())
            remoteViews.setOnClickPendingIntent(
                R.id.button_fourth_conversion,
                getPendingSelfIntent(context, rate + 4, fourth.toJson())
            )
            remoteViews.setViewVisibility(R.id.button_fourth_conversion, View.VISIBLE)
        } else {
            remoteViews.setViewVisibility(R.id.button_fourth_conversion, View.GONE)
        }
    }

    private fun setPendingIntents(context: Context, remoteViews: RemoteViews) {
        remoteViews.setOnClickPendingIntent(
            R.id.button1,
            getPendingSelfIntent(context, onebuttonclick)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button_more,
            getPendingSelfIntent(context, open)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button2,
            getPendingSelfIntent(context, twobuttonclick)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button3,
            getPendingSelfIntent(context, threebuttonclick)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button4,
            getPendingSelfIntent(context, fourbuttonclick)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button5,
            getPendingSelfIntent(context, fivebuttonclick)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button6,
            getPendingSelfIntent(context, sixbuttonclick)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button7,
            getPendingSelfIntent(context, sevenbuttonclick)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button8,
            getPendingSelfIntent(context, eightbuttonclick)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button9,
            getPendingSelfIntent(context, ninebuttonclick)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button0,
            getPendingSelfIntent(context, zerobuttonclick)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button_period,
            getPendingSelfIntent(context, dot)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button_add,
            getPendingSelfIntent(context, addbuttonclick)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button_subtraction,
            getPendingSelfIntent(context, subtractbuttonclick)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button_division,
            getPendingSelfIntent(context, dividebuttonclick)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button_multiplication,
            getPendingSelfIntent(context, multiplybuttonclick)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button_percentage,
            getPendingSelfIntent(context, modulusbuttonclick)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button_equal_to,
            getPendingSelfIntent(context, submit)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button_delete,
            getPendingSelfIntent(context, clear)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.button_ans,
            getPendingSelfIntent(context, answer)
        )
    }

    private fun getPendingSelfIntent(
        context: Context?,
        action: String?,
        extra: String? = null
    ): PendingIntent? {
        val intent = Intent(context, ExampleAppWidgetProvider::class.java).apply {
            putExtra(EXTRA, extra)
            this.action = action
        }
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val action = intent?.action
        Log.d("AppWidgetProvider", "onReceive: $action")
        val entity = intent?.getStringExtra(EXTRA)
        val remoteViews = RemoteViews(context!!.packageName, R.layout.layout_calculator_widget)
        val componentName = ComponentName(context, ExampleAppWidgetProvider::class.java)
        val ids: IntArray = AppWidgetManager.getInstance(context.applicationContext)
            .getAppWidgetIds(
                ComponentName(
                    context,
                    ExampleAppWidgetProvider::class.java
                )
            )
        onUpdate(context, AppWidgetManager.getInstance(context), ids)

        when (action) {
            AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED -> {
                Log.d("AppWidgetProvider", "onReceive: ACTION_APPWIDGET_OPTIONS_CHANGED called")
            }
            rate + 1, rate + 2, rate + 3, rate + 4 -> {
                handleRateConversion(context, entity, remoteViews, componentName)
            }
            clear -> {
                handleClearAction(context, remoteViews, componentName)
            }
            answer -> {
                handleAnswerAction(context, remoteViews, componentName)
            }
            open -> {
                val openIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("mode", "add")
                }
                context.startActivity(openIntent)
            }
            submit -> {
                handleSubmitAction(context, remoteViews, componentName)
            }
            in listOf(addbuttonclick, subtractbuttonclick, dividebuttonclick, multiplybuttonclick, modulusbuttonclick) -> {
                handleArithmeticAction(context, action, remoteViews, componentName)
            }
            in listOf(onebuttonclick, dot, twobuttonclick, threebuttonclick, fourbuttonclick, fivebuttonclick, sixbuttonclick, sevenbuttonclick, eightbuttonclick, ninebuttonclick, zerobuttonclick) -> {
                handleNumberAction(context, action, remoteViews, componentName)
            }
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                val ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                context?.let {
                    ids?.let { it1 ->
                        onUpdate(it, AppWidgetManager.getInstance(context), it1)
                    }
                }
            }
        }
    }

    private fun handleRateConversion(context: Context?, entity: String?, remoteViews: RemoteViews, componentName: ComponentName) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        val entityObj = Gson().fromJson(entity, DropDownRateEntity::class.java)
        entityObj?.let {
            val currentValue = calculationHandler.getAnswer().value.flatten()
            getVM(context!!).convertCurrency(currentValue, it, onDOne = {
                updateViewsAfterConversion(context, remoteViews, componentName, appWidgetIds)
            }, onError = {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }, conversionError = {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun handleClearAction(context: Context?, remoteViews: RemoteViews, componentName: ComponentName) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        calculationHandler.removeValue()
        updateViewsAfterClear(context, remoteViews, componentName, appWidgetIds)
    }

    private fun handleAnswerAction(context: Context?, remoteViews: RemoteViews, componentName: ComponentName) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        if (calculationHandler.getAnswer().value.isNotEmpty()) {
            calculationHandler.clearInput()
            calculationHandler.addValue(calculationHandler.getAnswer().value.split(" ").first().toString())
        }
        updateViewsAfterAnswer(context, remoteViews, componentName, appWidgetIds)
    }

    private fun handleSubmitAction(context: Context?, remoteViews: RemoteViews, componentName: ComponentName) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        calculationHandler.calculate({
            // Optional success handler
        }) {
            calculationHandler.clearInput()
            calculationHandler.addValue(it.split(" ").first().toString())
            updateViewsAfterSubmit(context, remoteViews, componentName, appWidgetIds)
        }
    }

    private fun handleArithmeticAction(context: Context?, action: String?, remoteViews: RemoteViews, componentName: ComponentName) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        calculationHandler.addArithemetic(Arithemetics.valueOf(action!!))
        updateViewsAfterArithmetic(context, remoteViews, componentName, appWidgetIds)
    }

    private fun handleNumberAction(context: Context?, action: String?, remoteViews: RemoteViews, componentName: ComponentName) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        calculationHandler.addValue(action!!)
        updateViewsAfterNumber(context, remoteViews, componentName, appWidgetIds)
    }

    private fun updateViewsAfterConversion(context: Context?, remoteViews: RemoteViews, componentName: ComponentName, appWidgetIds: IntArray) {
        var text = calculationHandler.getExpression().value
            .replace("/", "÷")
            .replace("*", "×")
            .replace("#", "%")
        text = if (text.length > 1 && text[1].toString() != ".") {
            text.trimStart('0')
        } else {
            text
        }
        val text2 = calculationHandler.getAnswer().value
        remoteViews.setTextViewText(R.id.input, text)
        remoteViews.setTextViewText(R.id.output, text2)
        val currencyRate = calculationHandler.getTotalCurrency().value
        remoteViews.setTextViewText(R.id.conversion_rate_output_widget, currencyRate)
        remoteViews.setViewVisibility(R.id.conversion_rate_output_widget, View.VISIBLE)
        updateWidgets(context, componentName, appWidgetIds, remoteViews)
    }

    private fun updateViewsAfterClear(context: Context?, remoteViews: RemoteViews, componentName: ComponentName, appWidgetIds: IntArray) {
        var text = calculationHandler.getExpression().value
            .replace("/", "÷")
            .replace("*", "×")
            .replace("#", "%")
        text = if (text.length > 1 && text[1].toString() != ".") {
            text.trimStart('0')
        } else {
            text
        }
        val text2 = calculationHandler.getAnswer().value
        remoteViews.setTextViewText(R.id.input, text)
        remoteViews.setTextViewText(R.id.output, text2)
        remoteViews.setViewVisibility(R.id.conversion_rate_output_widget, View.GONE)
        updateWidgets(context, componentName, appWidgetIds, remoteViews)
    }

    private fun updateViewsAfterAnswer(context: Context?, remoteViews: RemoteViews, componentName: ComponentName, appWidgetIds: IntArray) {
        var text = calculationHandler.getExpression().value
            .replace("/", "÷")
            .replace("*", "×")
            .replace("#", "%")
        text = if (text.length > 1 && text[1].toString() != ".") {
            text.trimStart('0')
        } else {
            text
        }
        val text2 = calculationHandler.getAnswer().value
        remoteViews.setTextViewText(R.id.input, text)
        remoteViews.setTextViewText(R.id.output, text2)
        remoteViews.setViewVisibility(R.id.conversion_rate_output_widget, View.GONE)
        updateWidgets(context, componentName, appWidgetIds, remoteViews)
    }

    private fun updateViewsAfterSubmit(context: Context?, remoteViews: RemoteViews, componentName: ComponentName, appWidgetIds: IntArray) {
        var text = calculationHandler.getExpression().value
            .replace("/", "÷")
            .replace("*", "×")
            .replace("#", "%")
        text = if (text.length > 1 && text[1].toString() != ".") {
            text.trimStart('0')
        } else {
            text
        }
        val text2 = calculationHandler.getAnswer().value
        val currencyRate = calculationHandler.getTotalCurrency().value
        remoteViews.setTextViewText(R.id.conversion_rate_output_widget, currencyRate)
        remoteViews.setViewVisibility(R.id.conversion_rate_output_widget, View.GONE)
        remoteViews.setTextViewText(R.id.input, text.flatten())
        remoteViews.setTextViewText(R.id.output, text2)
        updateWidgets(context, componentName, appWidgetIds, remoteViews)
    }

    private fun updateViewsAfterArithmetic(context: Context?, remoteViews: RemoteViews, componentName: ComponentName, appWidgetIds: IntArray) {
        var text = calculationHandler.getExpression().value
            .replace("/", "÷")
            .replace("*", "×")
            .replace("#", "%")
        text = if (text.length > 1 && text[1].toString() != ".") {
            text.trimStart('0')
        } else {
            text
        }
        val text2 = calculationHandler.getAnswer().value
        remoteViews.setTextViewText(R.id.input, text.flatten())
        remoteViews.setTextViewText(R.id.output, text2)
        updateWidgets(context, componentName, appWidgetIds, remoteViews)
    }

    private fun updateViewsAfterNumber(context: Context?, remoteViews: RemoteViews, componentName: ComponentName, appWidgetIds: IntArray) {
        var text = calculationHandler.getExpression().value
            .replace("/", "÷")
            .replace("*", "×")
            .replace("#", "%")
        text = if (text.length > 1 && text[1].toString() != ".") {
            text.trimStart('0')
        } else {
            text
        }
        val text2 = calculationHandler.getAnswer().value
        remoteViews.setTextViewText(R.id.input, text.flatten())
        remoteViews.setTextViewText(R.id.output, text2)
        updateWidgets(context, componentName, appWidgetIds, remoteViews)
    }

    private fun updateWidgets(context: Context?, componentName: ComponentName, appWidgetIds: IntArray, remoteViews: RemoteViews) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        for (appWidgetId in appWidgetIds) {
            appWidgetManager.updateAppWidget(componentName, remoteViews)
        }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        // Initialize resources if needed
        coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        // Clean up resources if needed
        coroutineScope.cancel() // Cancel all coroutines when the widget is disabled
    }
}
