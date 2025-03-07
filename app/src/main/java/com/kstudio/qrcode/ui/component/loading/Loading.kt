package com.kstudio.qrcode.ui.component.loading

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kstudio.qrcode.ui.theme.QrCodeTheme

@Composable
fun Loading(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = Modifier.size(60.dp))
}

@Preview
@Composable
private fun LoadingPreview() {
    QrCodeTheme {
        Loading()
    }
}