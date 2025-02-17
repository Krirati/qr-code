package com.kstudio.qrcode.features.scan

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage
import com.kstudio.qrcode.features.scan.model.ScanImageState

class ScannerAnalyzer(
    private val scanner: BarcodeScanner,
    private val onResult: (state: ScanImageState) -> Unit
) : ImageAnalysis.Analyzer {
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val displayValue = barcode.displayValue

                    displayValue?.let {
                        onResult.invoke(ScanImageState.Success(it))
                    } ?: onResult.invoke(ScanImageState.Fail("fail"))
                }
            }
            .addOnCompleteListener {
                imageProxy.close() // Ensure images are closed
            }
    }
}