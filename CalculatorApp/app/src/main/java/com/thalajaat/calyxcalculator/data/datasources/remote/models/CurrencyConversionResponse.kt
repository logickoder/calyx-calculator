package com.thalajaat.calyxcalculator.data.datasources.remote.models

import com.google.gson.annotations.SerializedName

// To change this when the API is live
data class CurrencyConversionResponse(
    @SerializedName("from")
    var fromConverter: String = "USD",
    @SerializedName("to")
    var toConverter : String = "EUR",
    @SerializedName("rate")
    var conversionRate : Double = 0.85126,
    @SerializedName("timestamp")
    var timeStamp: String = "2023-03-01T12:00:00Z"
)