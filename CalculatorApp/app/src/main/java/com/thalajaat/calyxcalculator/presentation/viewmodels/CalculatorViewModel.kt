package com.thalajaat.calyxcalculator.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.thalajaat.calyxcalculator.data.datasources.local.Coins
import com.thalajaat.calyxcalculator.data.datasources.local.room.ConversionDbRepoInterface
import com.thalajaat.calyxcalculator.data.datasources.local.room.DropDownRateEntity
import com.thalajaat.calyxcalculator.data.datasources.remote.api.ApiHelper
import com.thalajaat.calyxcalculator.dormain.CalculationHandler
import com.thalajaat.calyxcalculator.dormain.CalculationHandlerInterface
import com.thalajaat.calyxcalculator.utils.ResponseState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class CalculatorViewModel(
    private val offlineRepository: ConversionDbRepoInterface
) : ViewModel() {


    val rates = offlineRepository.getDropDownRate()

    var rateState: StateFlow<List<DropDownRateEntity>> =
        rates.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _handler = CalculationHandler()
    val geHandler: CalculationHandlerInterface
        get() = _handler

    private val api = ApiHelper()

    init {
        getData()
    }

    // Please Remove, Just for testing purpose, i didn't have time to set up timber
    private fun getData() {
        viewModelScope.launch {
            api.getCurrencyConversionRate("USD", "EUR").collectLatest {
                println(it.data)
                Log.d("CalculatorViewModel", "getData: ${it.data}")
            }
        }
    }

    fun addItems() = viewModelScope.launch {
        offlineRepository.insetDropDOwnRates2(Coins.entities)
    }

    fun pin(it: DropDownRateEntity) = viewModelScope.launch {

        offlineRepository.insetDropDOwnRates(listOf(it.copy(isPinned = true)))

    }

    fun unpin(it: DropDownRateEntity) = viewModelScope.launch {

        offlineRepository.insetDropDOwnRates(listOf(it.copy(isPinned = false)))

    }

    var loading = false
    fun convertCurrency(entity: DropDownRateEntity, onError:(String)->Unit) = viewModelScope.launch {
        Timber.tag("CURRENCY").v(entity.toString())
        if (loading.not()) {
            Timber.tag("NotLoading").d("Here")
            val value = _handler.getExpression().value
            if (value.containsArithmeticSign() || value.equals("0")) {
                Timber.tag("ContainsArithmeticSign").d(value.toString())
                onError("Conversion failed, symbol not allowed")
            } else {
                val isLongerThanAnHour = entity.timestamp.isOlderThanOneHourLegacy()
                if (isLongerThanAnHour) {
                    Timber.tag("IsLongerThanHour").d("Here")
                    loading = true
                    api.getCurrencyConversionRate(entity.start, entity.end).collectLatest {
                        loading = false
                        if (it is ResponseState.Success) {
                            val ratte = it.data?.conversionRate ?: 0.0
                            val timestamp = it.data?.timeStamp ?: ""

                            _handler.setAnswer(
                                (value.toDouble()) * (ratte) ,
                                " ${entity.start}/${entity.end} (${ratte})"
                            )
                            offlineRepository.insetDropDOwnRates(
                                listOf(
                                    entity.copy(
                                        rate = ratte,
                                        timestamp = timestamp
                                    )
                                )
                            )
                        } else {
                            onError("Conversion failed, Network Error")

                        }
                    }
                } else {
                    _handler.setAnswer(
                        value.toDouble() * (entity.rate),
                        " ${entity.start}/${entity.end} (${entity.rate})"
                    )
                }
            }
        } else {
            Timber.tag("Here23").d("Here")
            onError("Conversion failed, previous Conversion still ongoing")
        }

    }
}

class CalculorViewModelFactory(private val repository: ConversionDbRepoInterface) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalculatorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

fun String.isOlderThanOneHourLegacy(): Boolean {
    try {
        val truncatedTimestamp = substring(0, lastIndexOf('.') + 4) + "Z"

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val parsedTimestamp = sdf.parse(truncatedTimestamp)
        val oneHourAgo = Date(System.currentTimeMillis() - 3600 * 1000)

        // Returns true if the parsed timestamp is before the timestamp one hour ago
        return parsedTimestamp.before(oneHourAgo)

    } catch (e: Exception) {
        e.printStackTrace()
        return true
    }
    return true
}

val arithmeticSignPattern = "[+\\-*/=%]".toRegex()

fun String.containsArithmeticSign(): Boolean {
    return arithmeticSignPattern.containsMatchIn(this)
}