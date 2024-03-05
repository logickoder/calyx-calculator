package com.thalajaat.calyxcalculator.data.datasources.remote.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.thalajaat.calyxcalculator.utils.Utils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RemoteModule {
    fun provideGson(): Gson =
        GsonBuilder().setLenient().create()

    private fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }

    fun provideOkhttpClient(): OkHttpClient =
        OkHttpClient().newBuilder()
            .addInterceptor(provideLoggingInterceptor())
            .callTimeout(Utils.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(Utils.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .build()
}

val currencyApiService: CurrencyApiService by lazy {
    Retrofit.Builder()
        .baseUrl(Utils.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(RemoteModule.provideGson()))
        .client(RemoteModule.provideOkhttpClient())
        .build()
        .create(CurrencyApiService::class.java)
}