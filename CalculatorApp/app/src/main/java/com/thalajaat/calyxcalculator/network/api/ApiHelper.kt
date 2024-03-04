package com.thalajaat.calyxcalculator.network.api

import com.thalajaat.calyxcalculator.network.models.ErrorResponse
import com.thalajaat.calyxcalculator.utils.ResponseState
import kotlinx.coroutines.flow.flow

class ApiHelper {
    fun getCurrencyConversionRate(from: String, to: String) = flow {
        emit(ResponseState.Loading)
        try {
            val currencyConversionRate =
                RemoteModule.currencyApiService.getCurrencyConversionRate(from, to)
            emit(ResponseState.Success(currencyConversionRate))
        } catch (e: Exception){
            emit(ResponseState.Error(error = ErrorResponse(error = e.message)))
        }
    }

}