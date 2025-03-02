package com.kstudio.qrcode.features.scan

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kstudio.qrcode.features.history.data.ScanHistoryRepository
import com.kstudio.qrcode.features.history.data.model.ScanHistoryItem
import com.kstudio.qrcode.features.scan.model.ScanImageState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    data object Analysis : UiState()
    data class AnalysisGalleryImage(val url: Uri?) : UiState()
    data class DisplayBottomSheet(val data: String) : UiState()
}

class CameraPreviewViewModel(
    private val scanHistoryRepository: ScanHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Analysis)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

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
            scanHistoryRepository.insertItem(ScanHistoryItem(value = uri))
        }
    }
}