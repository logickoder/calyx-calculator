package com.thalajaat.calyxcalculator.data.datasources.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversionRate(entity: ConversionEntity)

    @Delete
    suspend fun deleteConversionRate(entity: ConversionEntity)

    @Query("SELECT * FROM conversiontable")
    fun getConversionRates(): ConversionEntity

    @Query("SELECT * FROM conversiontable WHERE id = :rateId")
    fun getConversionRate(rateId:String): Flow<List<ConversionEntity>>

    @Query("DELETE FROM conversiontable")
    suspend fun deleteAll()

}
