package com.thalajaat.calyxcalculator.utils

import android.content.Context
import com.google.gson.Gson
import com.thalajaat.calyxcalculator.R
import com.thalajaat.calyxcalculator.data.datasources.remote.models.CurrencyConversionResponse
import com.thalajaat.calyxcalculator.data.datasources.remote.models.ErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import java.io.IOException
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class ResponseState<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : ResponseState<T>(data)
    class Error<T>(message: String?, data: T? = null) : ResponseState<T>(data, message)
    class Loading<T>: ResponseState<T>(null, null)
}

fun processNetworkError(throwable: Throwable?): String? {
    throwable?.let {
        throwable.printStackTrace()
        val message: String?
        when (throwable) {
            is HttpException -> {
                when (throwable.code()) {
                    404 -> return "Resource not found"
                    else -> message = try {
                        val errorBody = throwable.response()?.errorBody()?.string()
                        val err = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        err.error
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Services unavailable"
                    }
                }
            }
            is SocketTimeoutException -> message = "Network Error"
            is MalformedURLException -> message = "Malformed URL"
            is IOException -> message = "Check your network and try again"
            else -> message = "Internal Error"
        }
        return message ?: "Internal Error"
    }
    return null
}
