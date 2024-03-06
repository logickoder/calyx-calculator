package com.thalajaat.calyxcalculator.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thalajaat.calyxcalculator.data.datasources.remote.api.ApiHelper
import com.thalajaat.calyxcalculator.data.datasources.remote.api.RemoteModule
import com.thalajaat.calyxcalculator.dormain.CalculationHandler
import com.thalajaat.calyxcalculator.dormain.CalculationHandlerInterface
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CalculatorViewModel():ViewModel() {

    private val _handler = CalculationHandler()
    val geHandler : CalculationHandlerInterface
        get() = _handler

    private val api = ApiHelper()

    init {
        getData()
    }
    // Please Remove, Just for testing purpose, i didn't have time to set up timber
    private fun getData(){
        viewModelScope.launch {
            api.getCurrencyConversionRate("USD", "EUR").collectLatest {
                println(it.data)
                Log.d("CalculatorViewModel", "getData: ${it.data}")
            }
        }
    }



}