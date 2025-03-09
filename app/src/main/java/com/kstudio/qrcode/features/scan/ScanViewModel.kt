package com.kstudio.qrcode.features.scan

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kstudio.qrcode.features.history.data.ScanHistoryRepository
import com.kstudio.qrcode.features.history.data.model.ScanHistoryItem
import com.kstudio.qrcode.features.scan.model.ScanImageState
import com.kstudio.qrcode.features.scan.model.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class ScanViewModel(
    private val scanHistoryRepository: ScanHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Analysis)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private var newIdItem: Long? = null

    fun onResultScanAnalyzer(result: ScanImageState) {
        when (result) {
            is ScanImageState.Fail -> _uiState.value = UiState.Analysis
            is ScanImageState.Success -> {
                _uiState.value = UiState.DisplayBottomSheet(result.result)
            }
        }
    }

    fun restartAnalysis() {
        _uiState.value = UiState.Analysis
    }

    fun onImagePicked(uri: Uri?) {
        _uiState.value = UiState.AnalysisGalleryImage(uri)
    }

    fun saveScanHistory(uri: String) {
        if (uri.isEmpty()) return
        viewModelScope.launch {
            val id = scanHistoryRepository.insertItem(ScanHistoryItem(value = uri))
            newIdItem = id
        }
    }

    fun updateItemFavoriteStatus() {
        newIdItem?.let {
            viewModelScope.launch {
                getHistoryFromId(it.toInt()).collect {
                    if (it != null) {
                        val newItem = it.copy(isFavorite = !it.isFavorite)
                        scanHistoryRepository.updateItem(newItem)
                    }
                }
            }
        }
    }

    private fun getHistoryFromId(id: Int) =
        scanHistoryRepository.getItemStream(id).flowOn(Dispatchers.IO)
}