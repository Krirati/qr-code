package com.kstudio.qrcode

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kstudio.qrcode.features.history.data.datasource.ScanHistoryDao
import com.kstudio.qrcode.features.history.data.model.ScanHistoryItem

@Database(entities = [ScanHistoryItem::class], version = 1, exportSchema = false)
abstract class ScanDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var Instance: ScanDatabase? = null

        fun getDatabase(context: Context): ScanDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ScanDatabase::class.java, "database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }

    abstract fun historyDao(): ScanHistoryDao
}