package com.thalajaat.calyxcalculator.utils

object Utils {
    const val BASE_URL = "https://currency-converter-api-f5dc322b9c00.herokuapp.com/api/"
    const val NETWORK_TIMEOUT: Long = 30L
    const val CLEAR_ALL_CLICK_ACTION = "CLEAR_ALL_CLICK_ACTION"

    fun String.flatten() =
        this.replace(",", "")
}