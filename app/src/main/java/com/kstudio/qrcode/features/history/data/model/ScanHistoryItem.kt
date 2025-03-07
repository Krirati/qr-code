package com.kstudio.qrcode.features.history.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "scan_history")
data class ScanHistoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val value: String,
    val createDateTime: LocalDateTime = LocalDateTime.now()
) {
    val createdDateFormatted: String
        get() = createDateTime.format(DateTimeFormatter.ofPattern("dd MMM YYYY - HH:mm:ss a"))
}
