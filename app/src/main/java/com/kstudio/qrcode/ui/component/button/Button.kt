package com.kstudio.qrcode.ui.component.button

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable


@Composable
fun iconButtonColors() = IconButtonColors(
    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .7f),
    contentColor = MaterialTheme.colorScheme.primary,
    disabledContentColor = MaterialTheme.colorScheme.primary,
    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer
)

@Composable
fun buttonColors() = ButtonColors(
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.secondary,
    disabledContentColor = MaterialTheme.colorScheme.primary,
    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer
)