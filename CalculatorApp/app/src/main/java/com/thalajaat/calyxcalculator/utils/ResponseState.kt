package com.thalajaat.calyxcalculator.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart

sealed class ResponseState<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?): ResponseState<T>(data)
    class Error<T>(message : String?, data: T? = null): ResponseState<T>(data ,message)
    class Loading<T>(data : T? = null) : ResponseState<T>(data)
}

fun<T> Flow<ResponseState<T>>.handleError(): Flow<ResponseState<T>>{
    return onStart {
        emit(ResponseState.Loading(null))
    }.catch {

    }

}