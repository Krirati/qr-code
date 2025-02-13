package com.kstudio.qrcode.features.scan.model

sealed class ScanImageState {
    data class Success(val result: String) : ScanImageState()
    data class Fail(val reason: String) : ScanImageState()
}
