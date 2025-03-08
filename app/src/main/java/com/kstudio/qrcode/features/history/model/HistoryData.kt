package com.kstudio.qrcode.features.history.model

data class HistoryData(
    val id: Int,
    val title: String,
    val isFavorite: Boolean = false,
    val createDate: String
)