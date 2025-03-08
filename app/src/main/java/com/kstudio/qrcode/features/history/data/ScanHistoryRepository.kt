package com.kstudio.qrcode.features.history.data

import com.kstudio.qrcode.features.history.data.model.ScanHistoryItem
import kotlinx.coroutines.flow.Flow

interface ScanHistoryRepository {
    fun getAllItemsStream(): Flow<List<ScanHistoryItem>>
    fun getItemStream(id: Int): Flow<ScanHistoryItem?>
    suspend fun insertItem(item: ScanHistoryItem)
    suspend fun deleteItem(id: Int)
    suspend fun updateItem(item: ScanHistoryItem)
}