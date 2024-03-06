package com.thalajaat.calyxcalculator.data.datasources.local

import com.thalajaat.calyxcalculator.data.datasources.local.room.DropDownRateEntity

object Coins {

    val entities = listOf(
        DropDownRateEntity(id = "USD/GBP", start = "USD",0, end = "GBP", isPinned = false,rate=0.0, timestamp = ""),
        DropDownRateEntity(id = "USD/EUR", start = "USD", 1,end = "EUR", isPinned = false,rate=0.0, timestamp = ""),
        DropDownRateEntity(id = "USD/CNY", start = "USD",2, end = "CNY", isPinned = false,rate=0.0, timestamp = ""),
        DropDownRateEntity(id = "GBP/USD", start = "GBP", 3,end = "USD", isPinned = false,rate=0.0, timestamp = ""),
        DropDownRateEntity(id = "GBP/EUR", start = "GBP", 4,end = "EUR", isPinned = false,rate=0.0, timestamp = ""),
        DropDownRateEntity(id = "GBP/CNY", start = "GBP", 5,end = "CNY", isPinned = false,rate=0.0, timestamp = ""),
        DropDownRateEntity(id = "EUR/USD", start = "EUR", 6,end = "USD", isPinned = false,rate=0.0, timestamp = ""),
        DropDownRateEntity(id = "EUR/GBP", start = "EUR", 7,end = "GBP", isPinned = false,rate=0.0, timestamp = ""),
        DropDownRateEntity(id = "EUR/CNY", start = "EUR", 8,end = "CNY", isPinned = false,rate=0.0, timestamp = ""),
        DropDownRateEntity(id = "CNY/USD", start = "CNY", 9,end = "USD", isPinned = false,rate=0.0, timestamp = ""),
        DropDownRateEntity(id = "CNY/GBP", start = "CNY", 10,end = "GBP", isPinned = false,rate=0.0, timestamp = ""),
        DropDownRateEntity(id = "CNY/EUR", start = "CNY", 11,end = "EUR", isPinned = false,rate=0.0, timestamp = ""),
    )

}