package com.thalajaat.calyxcalculator.data.datasources.remote.api

import android.content.Context
import com.thalajaat.calyxcalculator.utils.ResponseState
import com.thalajaat.calyxcalculator.utils.processNetworkError
import kotlinx.coroutines.flow.flow

class ApiHelper {
    suspend fun getCurrencyConversionRate(
        from: String,
        to: String,
    ) = flow {
        emit(ResponseState.Loading())
        try {
            val currencyConversionRate =
                currencyApiService.getCurrencyConversionRate(from, to)
            emit(ResponseState.Success(data = currencyConversionRate))
        } catch (e: Throwable){
            emit(ResponseState.Error(processNetworkError(e)))
        }
    }
}