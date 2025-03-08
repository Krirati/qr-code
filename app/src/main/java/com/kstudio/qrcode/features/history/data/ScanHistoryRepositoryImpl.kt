package com.kstudio.qrcode.features.history.data

import com.kstudio.qrcode.features.history.data.datasource.ScanHistoryDao
import com.kstudio.qrcode.features.history.data.model.ScanHistoryItem
import kotlinx.coroutines.flow.Flow

class ScanHistoryRepositoryImpl(private val itemDao: ScanHistoryDao) : ScanHistoryRepository {
    override fun getAllItemsStream(): Flow<List<ScanHistoryItem>> = itemDao.getAllHistory()

    override fun getItemStream(id: Int): Flow<ScanHistoryItem?> = itemDao.getHistory(id)

    override suspend fun insertItem(item: ScanHistoryItem) = itemDao.insert(item)

    override suspend fun deleteItem(id: Int) = itemDao.delete(id)

    override suspend fun updateItem(item: ScanHistoryItem) = itemDao.update(item)
}