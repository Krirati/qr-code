package com.kstudio.qrcode.ui.component.button

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable


@Composable
fun iconButtonColors() = IconButtonColors(
    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = .7f),
    contentColor = MaterialTheme.colorScheme.onPrimary,
    disabledContentColor = MaterialTheme.colorScheme.secondary,
    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer
)

@Composable
fun buttonColors() = ButtonColors(
    containerColor = MaterialTheme.colorScheme.primary,
    contentColor = MaterialTheme.colorScheme.onPrimary,
    disabledContentColor = MaterialTheme.colorScheme.secondary,
    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer
)