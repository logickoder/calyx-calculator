package com.thalajaat.calyxcalculator.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.thalajaat.calyxcalculator.domain.CalculationHandler
import com.thalajaat.calyxcalculator.utils.Utils.CLEAR_ALL_CLICK_ACTION


class DeleteAllLongClickListener : BroadcastReceiver() {
    private val calculationHandler by lazy {
        CalculationHandler()
    }
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == CLEAR_ALL_CLICK_ACTION) {
            // Handle the long click event here
            calculationHandler.clearInputForAll()
            Toast.makeText(context, "Widget long clicked!", Toast.LENGTH_SHORT).show()
        }
    }
}