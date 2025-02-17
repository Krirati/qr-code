package com.kstudio.qrcode.ui.component.button

import android.widget.Button
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun buttonColors() = IconButtonColors(
    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .7f),
    contentColor = MaterialTheme.colorScheme.primary,
    disabledContentColor = MaterialTheme.colorScheme.primary,
    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer
)