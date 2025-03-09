package com.kstudio.qrcode.features.scan.model

import android.net.Uri

sealed class UiState {
    data object Analysis : UiState()
    data class AnalysisGalleryImage(val url: Uri?) : UiState()
    data class DisplayBottomSheet(val data: String) : UiState()
}