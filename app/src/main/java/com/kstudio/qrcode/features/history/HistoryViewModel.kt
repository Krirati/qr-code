package com.kstudio.qrcode.features.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kstudio.qrcode.features.history.data.ScanHistoryRepository
import com.kstudio.qrcode.features.history.data.model.ScanHistoryItem
import com.kstudio.qrcode.features.history.model.HistoryData
import com.kstudio.qrcode.features.history.model.HistoryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class HistoryViewModel(private val historyRepository: ScanHistoryRepository) : ViewModel() {

    private val _bottomSheetData = MutableStateFlow<HistoryData?>(null)
    val bottomSheetData: StateFlow<HistoryData?> = _bottomSheetData

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
        HistoryData(
            id = it.id,
            title = it.value,
            isFavorite = it.isFavorite,
            createDate = it.createdDateFormatted
        )
    }

    fun deleteHistory(historyData: HistoryData) {
        viewModelScope.launch {
            historyRepository.deleteItem(historyData.id)
        }
    }

    fun updateItemFavoriteStatus(item: HistoryData) {
        viewModelScope.launch {
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy - HH:mm:ss a")
            val dateTime = LocalDateTime.parse(item.createDate, formatter)
            val newItem = ScanHistoryItem(
                id = item.id,
                value = item.title,
                isFavorite = !item.isFavorite,
                createDateTime = dateTime
            )
            historyRepository.updateItem(newItem)
        }
    }

    fun updateDataBottomSheet(item: HistoryData?) {
        _bottomSheetData.value = item
    }
}