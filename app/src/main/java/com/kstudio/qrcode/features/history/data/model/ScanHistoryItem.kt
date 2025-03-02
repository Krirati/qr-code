package com.kstudio.qrcode.features.history.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val value: String
)
