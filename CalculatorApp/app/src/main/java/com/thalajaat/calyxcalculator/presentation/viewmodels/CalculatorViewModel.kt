package com.thalajaat.calyxcalculator.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.thalajaat.calyxcalculator.dormain.CalculationHandler
import com.thalajaat.calyxcalculator.dormain.CalculationHandlerInterface

class CalculatorViewModel():ViewModel() {

    private val _handler = CalculationHandler()
    val geHandler : CalculationHandlerInterface
        get() = _handler





}