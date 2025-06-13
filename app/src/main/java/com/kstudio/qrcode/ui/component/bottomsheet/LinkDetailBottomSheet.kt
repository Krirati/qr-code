package com.kstudio.qrcode.ui.component.bottomsheet

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kstudio.qrcode.R
import com.kstudio.qrcode.ui.component.bottomsheet.model.BottomSheetData
import com.kstudio.qrcode.ui.theme.QrCodeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkDetailBottomSheet(
    modifier: Modifier,
    onClose: () -> Unit,
    onClickFavorite: () -> Unit,
    sheetState: SheetState,
    scope: CoroutineScope,
    data: BottomSheetData
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var isFavoriteState by remember { mutableStateOf(data.isFavorite) }
    val startIcon by remember(isFavoriteState) {
        mutableIntStateOf(
            if (isFavoriteState) R.drawable.ic_star_selected else R.drawable.ic_star
        )
    }

    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.background,
        sheetState = sheetState,
        onDismissRequest = onClose,
        contentWindowInsets = { WindowInsets(bottom = 30.dp) },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Scan result",
                    style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.primary),
                )
                IconButton(onClick = handleOnClose(scope, sheetState, onClose)) {
                    Icon(Icons.Default.Clear, contentDescription = "Close modal detail")
                }
            }
            Text(
                "Link ${data.link}",
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(16.dp))
            MenuButton(
                text = stringResource(R.string.copy),
                icon = R.drawable.ic_copy,
                onClick = copyQrData(clipboardManager, data, context)
            )

            MenuButton(
                text = stringResource(R.string.share),
                icon = R.drawable.ic_share,
                onClick = shareQrData(data, context)
            )
            MenuButton(text = stringResource(R.string.favorite), icon = startIcon) {
                isFavoriteState = !isFavoriteState
                onClickFavorite.invoke()
            }
            Spacer(Modifier.height(16.dp))
            Button({
                openExternalLink(data, context)
            }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Text(
                    stringResource(R.string.open_link),
                    Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onPrimary)
                )
            }
        }
    }
}

private fun openExternalLink(data: BottomSheetData, context: Context) {
    val result = runCatching {
        val intent = Intent().apply {
            setAction(Intent.ACTION_VIEW)
            setData(Uri.parse(data.link))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
    result.onFailure {
        Toast.makeText(context, "This qr code can't open", Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun copyQrData(
    clipboardManager: ClipboardManager,
    data: BottomSheetData,
    context: Context
): () -> Unit = {
    clipboardManager.setText(AnnotatedString(data.link))
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
        Toast.makeText(context, context.getString(R.string.toast_copy_success), Toast.LENGTH_SHORT)
            .show()
    }
}

@Composable
private fun shareQrData(
    data: BottomSheetData,
    context: Context
): () -> Unit = {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, data.link)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun handleOnClose(
    scope: CoroutineScope,
    sheetState: SheetState,
    onClose: () -> Unit
): () -> Unit = {
    scope.launch { sheetState.hide() }.invokeOnCompletion {
        if (!sheetState.isVisible) onClose()
    }
}

@Composable
private fun MenuButton(text: String, icon: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick.invoke() }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = text,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(24.dp))
        Text(
            text, style = MaterialTheme.typography.titleMedium,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun LinkDetailBottomSheetPreview() {
    val scope = rememberCoroutineScope()
    val sheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Expanded)
    QrCodeTheme {
        LinkDetailBottomSheet(
            onClose = {},
            onClickFavorite = {},
            sheetState = sheetState,
            scope = scope,
            data = BottomSheetData(0, false, ""),
            modifier = Modifier
        )
    }
}