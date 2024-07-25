package com.thalajaat.calyxcalculator.data.datasources.local

import com.thalajaat.calyxcalculator.data.datasources.local.room.DropDownRateEntity

object Coins {

    private val currencies = arrayOf(
        "ALL",
        "DZD",
        "USD",
        "EUR",
        "GBP",
        "AOA",
        "AED",
        "AFN",
        "XCD",
        "ARS",
        "AMD",
        "AWG",
        "AUD",
        "AZN",
        "BSD",
        "BHD",
        "BDT",
        "BBD",
        "BYN",
        "BZD",
        "XOF",
        "COL",
        "BMD",
        "BTN",
        "INR",
        "BWP",
        "BRL",
        "CNY",
        "EGP",
        "XAF",
        "HKD",
        "MGA",
        "ZAR",
        "NGN"
    ).toSortedSet()

    val entities = buildList {
        currencies.filterNot { it == "ALL" }.forEach { start ->
            currencies.filterNot { it == start }.forEach { end ->
                add(
                    DropDownRateEntity(
                        id = "$start/$end",
                        start = start,
                        index = size,
                        end = end,
                        isPinned = false,
                        rate = 0.0,
                        timestamp = ""
                    )
                )
            }
        }
    }
}