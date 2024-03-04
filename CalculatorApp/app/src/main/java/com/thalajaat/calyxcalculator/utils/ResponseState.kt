package com.thalajaat.calyxcalculator.utils

import com.thalajaat.calyxcalculator.network.models.CurrencyConversionResponse
import com.thalajaat.calyxcalculator.network.models.ErrorResponse

sealed class ResponseState {
    data class Success(val data: CurrencyConversionResponse): ResponseState()
    data class Error(val error : ErrorResponse): ResponseState()
    object Loading: ResponseState()
}