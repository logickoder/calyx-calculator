package com.thalajaat.calyxcalculator.utils

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.thalajaat.calyxcalculator.presentation.uis.widgets.xml.ExampleAppWidgetProvider

class ConfigurationListener: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        when(action){
            Intent.ACTION_CONFIGURATION_CHANGED -> updateWidget(context)
        }
    }

    private fun updateWidget(context: Context?){
        val intent = Intent(context, ExampleAppWidgetProvider::class.java)
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        val ids: IntArray = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(
                ComponentName(
                    context!!,
                    ExampleAppWidgetProvider::class.java
                )
            )
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }
}