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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDropDownRates(entity: List<DropDownRateEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDropDownRates2(entity: List<DropDownRateEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDropDownRate(entity: DropDownRateEntity)

    @Delete
    suspend fun deleteConversionRate(entity: ConversionEntity)

    @Delete
    suspend fun deleteDropDownRate(entity: DropDownRateEntity)

    @Query("SELECT * FROM conversiontable")
    fun getConversionRates(): Flow<List<ConversionEntity>>

    @Query("SELECT * FROM dropdownratestable ORDER BY id ASC")
    fun getDropDownRates(): Flow<List<DropDownRateEntity>>

    @Query("SELECT * FROM dropdownratestable ORDER BY id ASC")
    fun getDropDownRates2(): List<DropDownRateEntity>

    @Query("SELECT * FROM conversiontable WHERE id = :rateId")
    fun getConversionRate(rateId: String): ConversionEntity?

    @Query("DELETE FROM conversiontable")
    suspend fun deleteAll()


    @Query("DELETE FROM dropdownratestable")
    suspend fun deleteAllDropDownTable()
}
