package com.kstudio.qrcode.features.scan

import android.Manifest
import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.kstudio.qrcode.LocalNavController
import com.kstudio.qrcode.R
import com.kstudio.qrcode.Screen
import com.kstudio.qrcode.features.scan.model.ScanImageState
import com.kstudio.qrcode.ui.component.bottomsheet.LinkDetailBottomSheet
import com.kstudio.qrcode.ui.component.bottomsheet.model.BottomSheetData
import com.kstudio.qrcode.ui.component.button.buttonColors
import com.kstudio.qrcode.ui.theme.QrCodeTheme
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    viewModel: CameraPreviewViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val debouncedUiState by remember { derivedStateOf { uiState } }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            viewModel.onImagePicked(uri)
        }

    SideEffect {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    QrCodeTheme {
        Scaffold { paddingValues ->
            Box {
                CameraScreen(
                    paddingValues = paddingValues,
                    hasPermission = cameraPermissionState.status.isGranted,
                    uiState = debouncedUiState,
                    launcherGallery = launcher
                ) {
                    cameraPermissionState.launchPermissionRequest()
                }
                val state = uiState
                if (state is UiState.DisplayBottomSheet) {
                    viewModel.saveScanHistory(state.data)
                    LinkDetailBottomSheet(
                        scope = scope,
                        onClose = {
                            showBottomSheet = false
                            viewModel.restartAnalysis()
                        },
                        sheetState = sheetState,
                        data = BottomSheetData(link = state.data),
                        onClickFavorite = { viewModel.updateItemFavoriteStatus() }
                    )
                }
                if (state is UiState.AnalysisGalleryImage) {
                    val inputImage = state.url?.let { it1 -> InputImage.fromFilePath(context, it1) }
                    val scanner = BarcodeScanning.getClient()
                    if (inputImage != null) {
                        scanner.process(inputImage)
                            .addOnSuccessListener { barcodes ->
                                val result = barcodes.firstOrNull()?.rawValue ?: "No barcode found"
                                viewModel.onResultScanAnalyzer(ScanImageState.Success(result))
                            }
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraScreen(
    paddingValues: PaddingValues,
    hasPermission: Boolean,
    uiState: UiState,
    launcherGallery: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    onRequirePermission: () -> Unit,
) {
    if (hasPermission) {
        CameraPreview(
            paddingValues = paddingValues,
            uiState = uiState,
            launcherGallery = launcherGallery
        )
    } else {
        NoPermissionScreen(paddingValues, launcherGallery, onRequirePermission)
    }
}

@Composable
fun NoPermissionScreen(
    paddingValues: PaddingValues,
    launcherGallery: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    onRequirePermission: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer
            )
    ) {
        Box {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Scanner need permission camera")
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onRequirePermission) {
                    Text("Grant Permission")
                }
            }
            Surface(modifier = Modifier.align(Alignment.BottomCenter)) {
                BottomNavigation(paddingValues, launcherGallery)
            }
        }
    }
}

@Composable
fun CameraPreview(
    paddingValues: PaddingValues,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: CameraPreviewViewModel = koinViewModel(),
    uiState: UiState,
    launcherGallery: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
) {
    val context = LocalContext.current
    val previewView: PreviewView = remember { PreviewView(context) }
    val executor = rememberMainExecutor(context)
    val scanner = rememberScanner()
    val latestOnResult = rememberLatestOnResult(viewModel::onResultScanAnalyzer)

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            setImageAnalysisAnalyzer(
                executor,
                ScannerAnalyzer(scanner, latestOnResult)
            )
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            UiState.Analysis,
            is UiState.AnalysisGalleryImage -> {
                bindCameraController(cameraController, lifecycleOwner)
            }

            is UiState.DisplayBottomSheet -> {
                scanner.close()
                cameraController.unbind()
            }
        }
    }

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
        Box(
            modifier = Modifier
                .background(Color.Transparent)
                .align(Alignment.BottomCenter)
        ) {
            BottomNavigation(paddingValues, launcherGallery)
        }
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

    return BarcodeScanning.getClient(options)
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
            colors = buttonColors()
        ) {
            Image(
                painter = painterResource(R.drawable.ic_zap),
                "flash"
            )
        }
    }
}

@Composable
private fun BottomNavigation(
    paddingValues: PaddingValues,
    launcherGallery: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
) {
    val navController = LocalNavController.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Black.copy(alpha = .7f))
            .padding(top = 24.dp, bottom = 24.dp + paddingValues.calculateBottomPadding()),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        FilledIconButton(
            onClick = {
                launcherGallery.launch(
                    PickVisualMediaRequest(
                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
            modifier = Modifier.size(60.dp),
            colors = buttonColors()
        ) {
            Image(
                painter = painterResource(R.drawable.ic_image),
                contentDescription = "Select image from galley",
            )
        }
        FilledIconButton(
            onClick = { navController?.navigate(Screen.Generate.name) },
            modifier = Modifier.size(60.dp),
            colors = buttonColors()
        ) {
            Image(
                painter = painterResource(R.drawable.ic_qr_code),
                contentDescription = "Generate Qr code"
            )
        }
        FilledIconButton(
            onClick = { navController?.navigate(Screen.History.name) },
            modifier = Modifier.size(60.dp),
            colors = buttonColors()
        ) {
            Image(
                painter = painterResource(R.drawable.ic_list),
                contentDescription = "Generate Qr code"
            )
        }
    }
}

@Preview
@Composable
private fun CameraPreviewPreview() {
    QrCodeTheme {
//        CameraPreview(paddingValues = PaddingValues(16.dp), uiState = UiState.Analysis)
    }
}

@Preview
@Composable
private fun NoPermissionScreenPreview() {
    QrCodeTheme {
//        NoPermissionScreen(
//            paddingValues = PaddingValues(4.dp),
//            onRequirePermission = {}
//        )
    }
}

@Preview
@Composable
private fun BottomAppBarPreview() {
    QrCodeTheme {
        Box {
//            BottomNavigation(PaddingValues(4.dp), launcherGallery)
        }
    }
}