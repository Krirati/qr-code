package com.kstudio.qrcode.features.history.model

sealed class HistoryUiState {
    data object Empty: HistoryUiState()
    data object Loading: HistoryUiState()
    data class Success(val data: List<HistoryData>): HistoryUiState()
}