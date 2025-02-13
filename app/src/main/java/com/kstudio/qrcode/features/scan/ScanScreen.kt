package com.kstudio.qrcode.features.scan

import android.Manifest
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.kstudio.qrcode.R
import com.kstudio.qrcode.features.scan.model.ScanImageState
import com.kstudio.qrcode.ui.theme.QrCodeTheme
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen() {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    QrCodeTheme {
        Scaffold(bottomBar = { }) { paddingValues ->
            CameraScreen(
                paddingValues = paddingValues,
                hasPermission = cameraPermissionState.status.isGranted
            ) {
                cameraPermissionState.launchPermissionRequest()
            }
        }
    }
}

@Composable
private fun CameraScreen(
    paddingValues: PaddingValues,
    hasPermission: Boolean,
    onRequirePermission: () -> Unit
) {
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_FRONT) }
    var zoomLevel by remember { mutableFloatStateOf(0.0f) }
    val imageCaptureUseCase = remember { ImageCapture.Builder().build() }

    if (hasPermission) {
        CameraPreview(
            paddingValues = paddingValues,
            lensFacing = lensFacing,
            zoomLevel = zoomLevel,
            imageCaptureUseCase = imageCaptureUseCase
        )
    } else {
        NoPermissionScreen(onRequirePermission)
    }
}

@Composable
fun NoPermissionScreen(onRequirePermission: () -> Unit) {
    onRequirePermission.invoke()
}

@Composable
fun CameraPreview(
    paddingValues: PaddingValues,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    lensFacing: Int,
    zoomLevel: Float,
    imageCaptureUseCase: ImageCapture
) {
    val context = LocalContext.current
    val previewView: PreviewView = remember { PreviewView(context) }
    val executor = rememberMainExecutor(context)
    val scanner = rememberScanner()

    val latestOnResult = rememberLatestOnResult { state ->
        Log.d("test", ">>> $state")
        Toast.makeText(context, "Toast", Toast.LENGTH_SHORT).show()
    }

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            setImageAnalysisAnalyzer(
                executor,
                ScannerAnalyzer(scanner, latestOnResult) // Ensures image is closed
            )
        }
    }


    bindCameraController(cameraController, lifecycleOwner)

    Box(Modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                previewView.apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    this.controller = cameraController
                }
            },
        )
        TopOptionSection(paddingValues)
        BottomNavigation(paddingValues)
    }
}

@Composable
fun rememberMainExecutor(context: Context): Executor {
    return remember { ContextCompat.getMainExecutor(context) }
}

@Composable
fun rememberLatestOnResult(onResult: (ScanImageState) -> Unit): (ScanImageState) -> Unit {
    return rememberUpdatedState(newValue = onResult).value
}

@Composable
fun rememberScanner(): BarcodeScanner {
    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_AZTEC)
        .build()

    return remember { BarcodeScanning.getClient(options) }
}

fun bindCameraController(
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner
) {
    cameraController.bindToLifecycle(lifecycleOwner)
}

@Composable
private fun TopOptionSection(paddingValues: PaddingValues) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Black.copy(alpha = 0.7f))
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp + paddingValues.calculateTopPadding(), bottom = 24.dp),
        horizontalArrangement = Arrangement.End
    ) {
        FilledIconButton(
            onClick = {
                Toast.makeText(context, "Toast", Toast.LENGTH_SHORT).show()
            },
            colors = IconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .7f),
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Image(
                painter = painterResource(R.drawable.ic_zap),
                "flash"
            )
        }
    }
}

@Composable
private fun BoxScope.BottomNavigation(paddingValues: PaddingValues) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .background(color = Color.Black.copy(alpha = .7f))
            .padding(vertical = 32.dp, horizontal = 16.dp)
            .padding(bottom = paddingValues.calculateTopPadding()),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        FilledIconButton(
            onClick = {},
            modifier = Modifier.size(60.dp),
            colors = IconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Image(
                painter = painterResource(R.drawable.ic_image),
                contentDescription = "Select image from galley",
            )
        }
        FilledIconButton(
            onClick = {},
            modifier = Modifier.size(60.dp),
            colors = IconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Image(
                painter = painterResource(R.drawable.ic_qr_code),
                contentDescription = "Generate Qr code"
            )
        }
    }
}

@Preview
@Composable
private fun ScanScreenPreview() {
    QrCodeTheme {
        ScanScreen()
    }
}