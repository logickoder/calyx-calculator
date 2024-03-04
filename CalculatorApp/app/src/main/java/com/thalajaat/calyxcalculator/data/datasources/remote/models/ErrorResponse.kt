package com.thalajaat.calyxcalculator.data.datasources.remote.models

import com.google.gson.annotations.SerializedName

data class ErrorResponse (
    @SerializedName("error")
    val error : String?
)