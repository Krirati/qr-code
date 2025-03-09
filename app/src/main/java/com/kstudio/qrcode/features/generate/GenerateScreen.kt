package com.kstudio.qrcode.features.generate

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.kstudio.qrcode.LocalNavController
import com.kstudio.qrcode.R
import com.kstudio.qrcode.ui.component.button.buttonColors
import com.kstudio.qrcode.ui.theme.QrCodeTheme
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun GenerateScreen(
    modifier: Modifier = Modifier,
    viewModel: GenerateQrViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val window = (context as Activity).window
    val navController = LocalNavController.current
    val bitmapImage by viewModel.qrBitmap.collectAsStateWithLifecycle(null)
    val storagePermissionState = rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    SideEffect {
        if (!storagePermissionState.status.isGranted) {
            storagePermissionState.launchPermissionRequest()
        }
    }

    DisposableEffect(Unit) {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightNavigationBars = true
            isAppearanceLightStatusBars = true
        }

        onDispose {
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightNavigationBars = false
                isAppearanceLightStatusBars = false
            }
        }
    }

    QrCodeTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.tertiary,
                    ),
                    title = { Text(stringResource(R.string.generate_qr_code)) },
                    navigationIcon = {
                        IconButton(onClick = { navController?.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.navigate_back)
                            )
                        }
                    }
                )
            },
        ) { paddingValues ->
            Column(
                modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                QrCode(bitmapImage)
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = viewModel.textFieldLinkData,
                    onValueChange = { text -> viewModel.onTextFieldChange(text) },
                    label = { Text(stringResource(R.string.input_link_or_data)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { viewModel.onConfirmField() },
                    modifier = Modifier.padding(top = 32.dp)
                ) {
                    Text(stringResource(R.string.confirm))
                }
                Row(modifier = Modifier.padding(top = 16.dp)) {
                    IconButton(
                        enabled = bitmapImage != null,
                        onClick = {
                            viewModel.saveQrResult()
                            viewModel.saveBitmapToGallery(context, bitmapImage)
                            Toast.makeText(context,
                                context.getString(R.string.toast_save_qr_success), Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(60.dp)
                            .padding(),
                        colors = buttonColors().copy(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_save),
                            contentDescription = "Save",
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    FilledIconButton(
                        onClick = shareQrData(
                            bitmapImage,
                            context
                        ),
                        enabled = bitmapImage != null,
                        modifier = Modifier.size(60.dp),
                        colors = buttonColors().copy(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_share),
                            contentDescription = stringResource(R.string.share),
                            colorFilter = ColorFilter.tint(color = colorResource(R.color.white))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QrCode(bitmapImage: Bitmap?) {
    if (bitmapImage != null) {
        Image(
            bitmap = bitmapImage.asImageBitmap(),
            "Qr result",
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        )
    } else {
        Spacer(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.outline)
        )
    }
}

@Composable
private fun shareQrData(
    data: Bitmap?,
    context: Context
): () -> Unit = {
    data?.let {
        val uri = saveBitmapToCache(context, data)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
    }
}

private fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri? {
    return try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "qrcode.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()

        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

@Preview
@Composable
private fun GenerateScreenPreview() {
    QrCodeTheme {
        GenerateScreen()
    }
}
