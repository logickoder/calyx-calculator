package com.thalajaat.calyxcalculator.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.thalajaat.calyxcalculator.MainActivity
import com.thalajaat.calyxcalculator.R

class CalculatorWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Perform this loop procedure for each widget that belongs to this provider.
        appWidgetIds.forEach { appWidgetId ->
            // Create an Intent to launch an Activity when the widget is clicked (optional).
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE)

            // Get the layout for the widget and attach an on-click listener to the layout.
            val views = RemoteViews(context.packageName, R.layout.calculator_widget).apply {
                setOnClickPendingIntent(R.id.output, pendingIntent)
            }

            // Tell the AppWidgetManager to perform an update on the current widget.
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}