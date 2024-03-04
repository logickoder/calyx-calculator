package com.thalajaat.calyxcalculator.data.datasources.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ConversionEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class ConversionDatabase : RoomDatabase() {
    abstract fun converstionDao(): ConversionDao

    companion object {
        @Volatile
        private var INSTANCE: ConversionDatabase? = null

        fun getDatabase(context: Context): ConversionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ConversionDatabase::class.java,
                    "conversiondatabase",
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
