package com.kstudio.qrcode.features.generate

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.kstudio.qrcode.features.history.data.ScanHistoryRepository
import com.kstudio.qrcode.features.history.data.model.ScanHistoryItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class GenerateQrViewModel(
    private val historyRepository: ScanHistoryRepository
) : ViewModel() {

    private val _qrBitmap = MutableSharedFlow<Bitmap?>()
    val qrBitmap: SharedFlow<Bitmap?> = _qrBitmap.asSharedFlow()

    var textFieldLinkData by mutableStateOf("")
        private set

    fun onTextFieldChange(input: String) {
        textFieldLinkData = input
        generateQRCode(input)
    }

    fun onConfirmField() {
        generateQRCode(textFieldLinkData)
    }

    private fun generateQRCode(text: String, size: Int = 512) {
        viewModelScope.launch {
            if (text.isEmpty()) {
                _qrBitmap.emit(null)
                return@launch
            }
            val qrCodeWriter = QRCodeWriter()
            val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size)

            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            _qrBitmap.emit(bitmap)
        }
    }

    fun saveQrResult() {
        if (textFieldLinkData.isEmpty()) return
        viewModelScope.launch {
            historyRepository.insertItem(ScanHistoryItem(value = textFieldLinkData))
        }
    }

    fun saveBitmapToGallery(
        context: Context,
        bitmap: Bitmap?,
        filename: String = "Image_${System.currentTimeMillis()}.jpg"
    ) {
        if (bitmap == null) return
        val contentResolver = context.contentResolver
        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Qr_Code")
        }

        val imageUri = contentResolver.insert(imageCollection, contentValues)
        imageUri?.let { uri ->
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }
    }
}