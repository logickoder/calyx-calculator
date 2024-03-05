package com.thalajaat.calyxcalculator.data.datasources.local.room

class ConversionDbRepo(private val conversionDao: ConversionDao):ConversionDbRepoInterface {
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


}

interface ConversionDbRepoInterface{
    suspend fun insertConversionRate(conversionEntity: ConversionEntity)
    suspend fun delete(conversionEntity: ConversionEntity)
    fun getConversionRate(id: String)
    suspend fun deleteAll()
}