package com.thalajaat.calyxcalculator.data.datasources.local.room

import com.thalajaat.calyxcalculator.data.datasources.local.Coins
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ConversionDbRepo(private val conversionDao: ConversionDao):ConversionDbRepoInterface {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            insetDropDOwnRates2(Coins.entities)
        }
    }
    override suspend fun insertConversionRate(conversionEntity: ConversionEntity) {
        conversionDao.insertConversionRate(conversionEntity)
    }

    override suspend fun delete(conversionEntity: ConversionEntity) {
      conversionDao.deleteConversionRate(conversionEntity)
    }

    override fun getConversionRate(id: String) {
        conversionDao.getConversionRate(id)
    }

    override suspend fun deleteAll() {
      conversionDao.deleteAll()
    }

    override suspend fun insetDropDOwnRates( conversionEntity: List<DropDownRateEntity>) {
        conversionDao.insertDropDownRates(conversionEntity)
    }

    override suspend fun insetDropDOwnRates2( conversionEntity: List<DropDownRateEntity>) {
        conversionDao.insertDropDownRates2(conversionEntity)
    }

    override suspend fun deleteDropDownRate(conversionEntity: DropDownRateEntity) {
        conversionDao.deleteDropDownRate(conversionEntity)
    }

    override fun getDropDownRate(): Flow<List<DropDownRateEntity>> {
     return conversionDao.getDropDownRates()
    }

    override fun getDropDownRate2(): List<DropDownRateEntity> {
        return conversionDao.getDropDownRates2()
    }


    override suspend fun deleteAllDropDown() {
       conversionDao.deleteAllDropDownTable()
    }


}

interface ConversionDbRepoInterface{
    suspend fun insertConversionRate(conversionEntity: ConversionEntity)
    suspend fun delete(conversionEntity: ConversionEntity)
    fun getConversionRate(id: String)
    suspend fun deleteAll()
    suspend fun insetDropDOwnRates(conversionEntity: List<DropDownRateEntity>)
    suspend fun insetDropDOwnRates2(conversionEntity: List<DropDownRateEntity>)
    suspend fun deleteDropDownRate(conversionEntity: DropDownRateEntity)
    fun getDropDownRate() : Flow<List<DropDownRateEntity>>
    fun getDropDownRate2() : List<DropDownRateEntity>
    suspend fun deleteAllDropDown()
}