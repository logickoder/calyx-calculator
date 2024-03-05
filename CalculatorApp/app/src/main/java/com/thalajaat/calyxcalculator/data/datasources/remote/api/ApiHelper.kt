package com.thalajaat.calyxcalculator.data.datasources.remote.api

import com.thalajaat.calyxcalculator.data.datasources.remote.models.CurrencyConversionResponse
import com.thalajaat.calyxcalculator.data.datasources.remote.models.ErrorResponse
import com.thalajaat.calyxcalculator.utils.ResponseState
import com.thalajaat.calyxcalculator.utils.handleError
import kotlinx.coroutines.flow.flow

class ApiHelper {
    fun getCurrencyConversionRate(from: String, to: String) = flow {
        val currencyConversionRate =
                RemoteModule.currencyApiService.getCurrencyConversionRate(from, to)
        emit(ResponseState.Success(currencyConversionRate))
    }.handleError()

}