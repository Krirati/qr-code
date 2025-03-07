package com.kstudio.qrcode.features.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kstudio.qrcode.features.history.data.ScanHistoryRepository
import com.kstudio.qrcode.features.history.data.model.ScanHistoryItem
import com.kstudio.qrcode.features.history.model.HistoryData
import com.kstudio.qrcode.features.history.model.HistoryUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(historyRepository: ScanHistoryRepository) : ViewModel() {

    val historyState: StateFlow<HistoryUiState> =
        historyRepository.getAllItemsStream().map { items ->
            if (items.isEmpty()) {
                HistoryUiState.Empty
            } else {
                HistoryUiState.Success(items.mapToHistoryData())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = HistoryUiState.Loading
        )

    private fun List<ScanHistoryItem>.mapToHistoryData() = this.map {
        HistoryData(id = it.id, title = it.value, createDate = it.createdDateFormatted)
    }
}