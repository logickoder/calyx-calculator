package com.thalajaat.calyxcalculator.data.datasources.remote.api

import com.thalajaat.calyxcalculator.data.datasources.remote.models.CurrencyConversionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {
    @GET("/conversion")
    suspend fun getCurrencyConversionRate(
        @Query("from") from: String = "USD",
        @Query("to") to: String = "EUR"
    ): CurrencyConversionResponse
}
