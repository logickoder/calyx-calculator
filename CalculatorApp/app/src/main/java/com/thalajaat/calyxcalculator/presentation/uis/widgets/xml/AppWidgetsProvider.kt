package com.thalajaat.calyxcalculator.presentation.uis.widgets.xml

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.lifecycle.map
import com.google.gson.Gson
import com.thalajaat.calyxcalculator.MainActivity
import com.thalajaat.calyxcalculator.R
import com.thalajaat.calyxcalculator.data.datasources.local.room.ConversionDatabase
import com.thalajaat.calyxcalculator.data.datasources.local.room.ConversionDbRepo
import com.thalajaat.calyxcalculator.data.datasources.local.room.DropDownRateEntity
import com.thalajaat.calyxcalculator.data.datasources.remote.api.ApiHelper
import com.thalajaat.calyxcalculator.dormain.Arithemetics
import com.thalajaat.calyxcalculator.dormain.CalculationHandler
import com.thalajaat.calyxcalculator.presentation.viewmodels.CalculatorViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

fun DropDownRateEntity.getName(): String{
    return "${start?:""}/${end?:""}"
}

class ExampleAppWidgetProvider : AppWidgetProvider() {

    private lateinit var notesRepository: ConversionDbRepo

    companion object {
        var list = emptyList<DropDownRateEntity>()
        val calculationHandler = CalculationHandler()
        fun getVM(context: Context) = CalculatorViewModel(
            ConversionDbRepo(      ConversionDatabase.getDatabase(context).converstionDao()),
            calculationHandler)
    }

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
        // Perform this loop procedure for each widget that belongs to this
        // provider.
        val widgetManager = AppWidgetManager.getInstance(context.applicationContext)
        val remoteViews = RemoteViews(context.packageName, R.layout.calculator_widget)


        CoroutineScope(Dispatchers.IO).launch {
            getVM(context).offlineRepository.getDropDownRate2(). filter { it.isPinned } .also {
                Log.d("Value", it.toString())
                withContext(Dispatchers.Main) {
                    val first = it.firstOrNull()
                    val second = it.getOrNull(1)
                    val three = it.getOrNull(2)
                    val four = it.getOrNull(3)
                    Timber.tag("GOTTEN").v(it.map { it.start }.toString())
                    if (first != null) {
                        remoteViews.setTextViewText(R.id.first_rate, first.getName())
                        remoteViews.setOnClickPendingIntent(
                            R.id.button_first_conversion,
                            getPendingSelfIntent(context, rate+1, first.toJson())
                        )
                        remoteViews.setViewVisibility(R.id.button_first_conversion, View.VISIBLE)
                    } else {
                        remoteViews.setViewVisibility(R.id.button_first_conversion, View.GONE)
                        remoteViews.setViewVisibility(R.id.button_second_conversion, View.GONE)
                        remoteViews.setViewVisibility(R.id.button_third_conversion, View.GONE)
                        remoteViews.setViewVisibility(R.id.button_fourth_conversion, View.GONE)
                    }
                    if (second != null) {
                        remoteViews.setTextViewText(R.id.second_rate, second.getName())
                        remoteViews.setOnClickPendingIntent(
                            R.id.button_second_conversion,
                            getPendingSelfIntent(context, rate+2, second.toJson())
                        )
                        remoteViews.setViewVisibility(R.id.button_second_conversion, View.VISIBLE)
                    } else {
                        remoteViews.setViewVisibility(R.id.button_second_conversion, View.GONE)
                        remoteViews.setViewVisibility(R.id.button_third_conversion, View.GONE)
                        remoteViews.setViewVisibility(R.id.button_fourth_conversion, View.GONE)
                    }
                    if (three != null) {
                        remoteViews.setTextViewText(R.id.third_rate, three.getName())
                        remoteViews.setOnClickPendingIntent(
                            R.id.button_third_conversion,
                            getPendingSelfIntent(context, rate+3, three.toJson())
                        )
                        remoteViews.setViewVisibility(R.id.button_third_conversion, View.VISIBLE)
                    } else {
                        remoteViews.setViewVisibility(R.id.button_third_conversion, View.GONE)
                        remoteViews.setViewVisibility(R.id.button_fourth_conversion, View.GONE)
                    }
                    if (four != null) {
                        remoteViews.setTextViewText(R.id.fourth_rate, four.getName())
                        remoteViews.setOnClickPendingIntent(
                            R.id.button_fourth_conversion,
                            getPendingSelfIntent(context, rate+4, four.toJson())
                        )
                        remoteViews.setViewVisibility(R.id.button_fourth_conversion, View.VISIBLE)
                    } else {
                        remoteViews.setViewVisibility(R.id.button_fourth_conversion, View.GONE)
                    }
                    appWidgetIds.forEach {
                        updateAppWidget(context, appWidgetManager, it, remoteViews)
                    }

                }
            }
        }
        appWidgetIds.forEach { appWidgetId ->

            val toastPendingIntent: PendingIntent = Intent(
                context,
                ExampleAppWidgetProvider::class.java
            ).run {
                // Set the action for the intent.
                // When the user touches a particular view, it has the effect of
                // broadcasting TOAST_ACTION.
                action = "EDIT"
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))

                PendingIntent.getBroadcast(
                    context,
                    0,
                    this,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
            }

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

            updateAppWidget(context, appWidgetManager, appWidgetId, remoteViews)
        }

    }

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
    fun getPendingSelfIntent(context: Context?, action: String?,extra:String?=null): PendingIntent? {
        val intent = Intent(context, ExampleAppWidgetProvider::class.java)
        intent.putExtra(EXTRA,extra,)
        intent.setAction(action)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        Timber.tag("ACTION").v(action)
        Timber.tag("ID").v(intent?.getIntExtra("ID", 0).toString())
        Timber.tag("IDS")
            .v(intent?.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS).toString())
        val entity = intent?.getStringExtra(EXTRA)
        val remoteViews = RemoteViews(context!!.packageName, R.layout.calculator_widget)
        val componentName = ComponentName(context, ExampleAppWidgetProvider::class.java)
        when (action) {

            rate+1,rate+2,rate+3,rate+4 -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(
                        context!!,
                        ExampleAppWidgetProvider::class.java
                    )
                )
                val entity = Gson().fromJson(entity,DropDownRateEntity::class.java)
                if(entity!=null){
                    getVM(context).convertCurrency(entity,{
                        var text = calculationHandler.getExpression().value
                            .replace("/", "÷")
                            .replace("*", "×").replace("#", "%")
                        text.also {
                            if (it.length > 1) {
                                if (it[1].toString() != ".") {
                                    text = it.trimStart("0".toCharArray().first())
                                }
                                it
                            } else {
                                it
                            }
                        }
                        val text2 = calculationHandler.getAnswer().value
                        remoteViews.setTextViewText(R.id.input, text)
                        remoteViews.setTextViewText(R.id.output, text2)
                        for (appWidgetId in appWidgetIds) {
                            appWidgetManager.updateAppWidget(componentName, remoteViews)
                        }
                    }){

                    }
                }

            }

            clear -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(
                        context!!,
                        ExampleAppWidgetProvider::class.java
                    )
                )
                calculationHandler.removeValue()
                var text = calculationHandler.getExpression().value
                    .replace("/", "÷")
                    .replace("*", "×").replace("#", "%")
                text.also {
                    if (it.length > 1) {
                        if (it[1].toString() != ".") {
                            text = it.trimStart("0".toCharArray().first())
                        }
                        it
                    } else {
                        it
                    }
                }
                val text2 = calculationHandler.getAnswer().value
                remoteViews.setTextViewText(R.id.input, text)
                remoteViews.setTextViewText(R.id.output, text2)
                for (appWidgetId in appWidgetIds) {
                    appWidgetManager.updateAppWidget(componentName, remoteViews)
                }
            }

            open -> {
                val open = Intent(context, MainActivity::class.java)
                open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                open.putExtra("mode", "add")
                context?.startActivity(open)
            }

            submit -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(
                        context,
                        ExampleAppWidgetProvider::class.java
                    )
                )
                calculationHandler.calculate({}) {
                    var text = calculationHandler.getExpression().value
                        .replace("/", "÷")
                        .replace("*", "×").replace("#", "%")
                    text.also {
                        if (it.length > 1) {
                            if (it[1].toString() != ".") {
                                text = it.trimStart("0".toCharArray().first())
                            }
                            it
                        } else {
                            it
                        }
                    }
                    val text2 = calculationHandler.getAnswer().value
                    remoteViews.setTextViewText(R.id.input, text)
                    remoteViews.setTextViewText(R.id.output, text2)
                    for (appWidgetId in appWidgetIds) {
                        appWidgetManager.updateAppWidget(componentName, remoteViews)
                    }

                }
            }

            addbuttonclick, subtractbuttonclick, dividebuttonclick, multiplybuttonclick, modulusbuttonclick -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(
                        context!!,
                        ExampleAppWidgetProvider::class.java
                    )
                )
                calculationHandler.addArithemetic(Arithemetics.valueOf(action))
                var text = calculationHandler.getExpression().value
                    .replace("/", "÷")
                    .replace("*", "×").replace("#", "%")
                text.also {
                    if (it.length > 1) {
                        if (it[1].toString() != ".") {
                            text = it.trimStart("0".toCharArray().first())
                        }
                        it
                    } else {
                        it
                    }
                }
                val text2 = calculationHandler.getAnswer().value
                remoteViews.setTextViewText(R.id.input, text)
                remoteViews.setTextViewText(R.id.output, text2)
                for (appWidgetId in appWidgetIds) {
                    appWidgetManager.updateAppWidget(componentName, remoteViews)
                }
            }


            onebuttonclick, dot, twobuttonclick, threebuttonclick, fourbuttonclick, fivebuttonclick, sixbuttonclick, sevenbuttonclick, eightbuttonclick, ninebuttonclick, zerobuttonclick -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(
                        context!!,
                        ExampleAppWidgetProvider::class.java
                    )
                )
                calculationHandler.addValue(action)
                var text = calculationHandler.getExpression().value
                    .replace("/", "÷")
                    .replace("*", "×").replace("#", "%")
                text.also {
                    if (it.length > 1) {
                        if (it[1].toString() != ".") {
                            text = it.trimStart("0".toCharArray().first())
                        }
                        it
                    } else {
                        it
                    }
                }
                val text2 = calculationHandler.getAnswer().value
                remoteViews.setTextViewText(R.id.input, text)
                remoteViews.setTextViewText(R.id.output, text2)
                for (appWidgetId in appWidgetIds) {
                    appWidgetManager.updateAppWidget(componentName, remoteViews)
                }
            }


            ACTION_APPWIDGET_UPDATE -> {

                val ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                context?.let {
                    ids?.let { it1 ->
                        onUpdate(
                            it, AppWidgetManager.getInstance(context),
                            it1
                        )
                    }
                }
            }
        }

        super.onReceive(context, intent)
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)

    }
}