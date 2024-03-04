package com.thalajaat.calyxcalculator.network.api

import com.thalajaat.calyxcalculator.network.models.CurrencyConversionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {
    @GET("/conversion")
    suspend fun getCurrencyConversionRate(
        @Query("from") from: String = "USD",
        @Query("to") to: String = "EUR"
    ): CurrencyConversionResponse
}
