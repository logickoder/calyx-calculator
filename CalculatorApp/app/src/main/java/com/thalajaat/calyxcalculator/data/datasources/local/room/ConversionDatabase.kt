package com.thalajaat.calyxcalculator.data.datasources.local.room

import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ConversionEntity::class,
        DropDownRateEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class ConversionDatabase : RoomDatabase() {
    abstract fun conversionDao(): ConversionDao

    companion object {
        @Volatile
        private var INSTANCE: ConversionDatabase? = null

        fun getDatabase(context: Context): ConversionDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ConversionDatabase::class.java,
                    ConversionDatabase::class.simpleName,
                ).fallbackToDestructiveMigration().build().also {
                    INSTANCE = it
                }
            }
        }
    }
}
