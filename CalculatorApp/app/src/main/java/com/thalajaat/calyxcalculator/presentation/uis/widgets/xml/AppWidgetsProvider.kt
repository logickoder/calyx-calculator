package com.thalajaat.calyxcalculator.presentation.uis.widgets.xml

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.thalajaat.calyxcalculator.R
import com.thalajaat.calyxcalculator.data.datasources.local.room.ConversionDatabase
import com.thalajaat.calyxcalculator.data.datasources.local.room.ConversionDbRepo
import com.thalajaat.calyxcalculator.data.datasources.local.room.DropDownRateEntity
import com.thalajaat.calyxcalculator.dormain.Arithemetics
import com.thalajaat.calyxcalculator.dormain.CalculationHandler
import timber.log.Timber


class ExampleAppWidgetProvider : AppWidgetProvider() {

    private lateinit var notesRepository: ConversionDbRepo
    private var notesItemList = arrayListOf<DropDownRateEntity>(

    )

    companion object {
        var list = emptyList<DropDownRateEntity>()
        val calculationHandler = CalculationHandler()
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

        val database =ConversionDatabase.getDatabase(context)
        notesRepository = ConversionDbRepo(database.converstionDao())
        notesRepository.getDropDownRate2() .observeForever {

            Timber.tag("GOTTEN").v(it.map { it.start }.toString())


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

                    PendingIntent.getBroadcast(context, 0, this, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                }
                var text = calculationHandler.getExpression().value
                    .replace("/", "÷")
                    .replace("*", "×").replace("#", "%")
                text.also{
                    if (it.length > 1) {
                        if (it[1].toString() != ".") {
                            text=it.trimStart("0".toCharArray().first())
                        }
                        it
                    } else {
                        it
                    }
                } // Assuming this is the method to add the number to the calculation.
                remoteViews.setTextViewText(R.id.input, text)
                val text2 = calculationHandler.getAnswer().value
                remoteViews.setTextViewText(R.id.output, text2)

                remoteViews.setOnClickPendingIntent(
                    R.id.button1,
                    getPendingSelfIntent(context, onebuttonclick)
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
                    R.id.button_add,
                    getPendingSelfIntent(context, addbuttonclick)
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

    fun getPendingSelfIntent(context: Context?, action: String?): PendingIntent? {
        val intent = Intent(context, ExampleAppWidgetProvider::class.java)
        intent.setAction(action)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        Timber.tag("ACTION").v(action)
        Timber.tag("ID").v(intent?.getIntExtra("ID", 0).toString())
        Timber.tag("IDS")
            .v(intent?.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS).toString())
        val remoteViews = RemoteViews(context!!.packageName, R.layout.calculator_widget)
        val componentName = ComponentName(context, ExampleAppWidgetProvider::class.java)
        when (action) {


            addbuttonclick,subtractbuttonclick,dividebuttonclick,multiplybuttonclick,modulusbuttonclick-> {
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
                text.also{
                    if (it.length > 1) {
                        if (it[1].toString() != ".") {
                            text=it.trimStart("0".toCharArray().first())
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
                    appWidgetManager.updateAppWidget(componentName,remoteViews )
                }
            }


            onebuttonclick,twobuttonclick,threebuttonclick,fourbuttonclick,fivebuttonclick,sixbuttonclick,sevenbuttonclick,eightbuttonclick,ninebuttonclick,zerobuttonclick -> {
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
                    text.also{
                        if (it.length > 1) {
                            if (it[1].toString() != ".") {
                                text=it.trimStart("0".toCharArray().first())
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
                    appWidgetManager.updateAppWidget(componentName,remoteViews )
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