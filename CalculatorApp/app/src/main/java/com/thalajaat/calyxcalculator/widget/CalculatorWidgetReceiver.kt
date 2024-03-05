package com.thalajaat.calyxcalculator.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import com.thalajaat.calyxcalculator.R
import timber.log.Timber

class CalculatorWidgetReceiver: AppWidgetProvider() {

    val TAG = "CalculatorWidgetReceiver"

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        val remoteViews = RemoteViews(context?.packageName, R.layout.calculator_widget)

        // Iterate through all the app widget IDs
        appWidgetIds?.forEach { appWidgetId ->
            // Update each of the app widgets with the remote views
            appWidgetManager?.updateAppWidget(appWidgetId, remoteViews)
        }

        Timber.tag(TAG).i("onUpdate: Widget has been added to screen")
    }

}