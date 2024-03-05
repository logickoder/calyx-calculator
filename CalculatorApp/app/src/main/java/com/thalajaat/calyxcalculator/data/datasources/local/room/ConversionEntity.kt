package com.thalajaat.calyxcalculator.data.datasources.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ConversionTable")
data class ConversionEntity(
    @PrimaryKey
    var id: String,
    val rate:String,
    val timestap:String
)
