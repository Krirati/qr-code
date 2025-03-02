package com.kstudio.qrcode.features.scan

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.kstudio.qrcode.features.scan.model.ScanImageState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class UiState {
    data object Analysis : UiState()
    data class AnalysisGalleryImage(val url: Uri?) : UiState()
    data class DisplayBottomSheet(val data: String) : UiState()
}

class CameraPreviewViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Analysis)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onResultScanAnalyzer(result: ScanImageState) {
        when (result) {
            is ScanImageState.Fail -> _uiState.value = UiState.Analysis
            is ScanImageState.Success -> _uiState.value = UiState.DisplayBottomSheet(result.result)
        }
    }

    fun restartAnalysis() {
        _uiState.value = UiState.Analysis
    }

    fun onImagePicked(uri: Uri?) {
        _uiState.value = UiState.AnalysisGalleryImage(uri)
    }
}