package com.kstudio.qrcode

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kstudio.qrcode.database.coverter.Converters
import com.kstudio.qrcode.features.history.data.datasource.ScanHistoryDao
import com.kstudio.qrcode.features.history.data.model.ScanHistoryItem

@Database(
    entities = [ScanHistoryItem::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
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