package com.kstudio.qrcode.features.history.data.datasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kstudio.qrcode.features.history.data.model.ScanHistoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: ScanHistoryItem)

    @Update
    suspend fun update(item: ScanHistoryItem)

    @Delete
    suspend fun delete(item: ScanHistoryItem)

    @Query("SELECT * from scan_history WHERE id = :id")
    fun getHistory(id: Int): Flow<ScanHistoryItem>

    @Query("SELECT * from scan_history ORDER BY id ASC")
    fun getAllHistory(): Flow<List<ScanHistoryItem>>
}