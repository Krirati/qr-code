package com.kstudio.qrcode.features.history

import android.app.Activity
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kstudio.qrcode.LocalNavController
import com.kstudio.qrcode.R
import com.kstudio.qrcode.features.history.model.HistoryData
import com.kstudio.qrcode.features.history.model.HistoryUiState
import com.kstudio.qrcode.ui.component.ad.NativeAdView
import com.kstudio.qrcode.ui.component.ad.itemsWithAd
import com.kstudio.qrcode.ui.component.appbar.AppBar
import com.kstudio.qrcode.ui.component.bottomsheet.LinkDetailBottomSheet
import com.kstudio.qrcode.ui.component.bottomsheet.model.BottomSheetData
import com.kstudio.qrcode.ui.component.loading.Loading
import com.kstudio.qrcode.ui.theme.QrCodeTheme
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = koinViewModel()
) {

    val context = LocalContext.current
    val navController = LocalNavController.current
    val historyUiState = viewModel.historyState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val showBottomSheet = viewModel.bottomSheetData.collectAsStateWithLifecycle()
    val window = (context as Activity).window

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
    Scaffold(topBar = {
        AppBar(navController, R.string.history_of_scan)
    }) { paddingValues ->
        when (val state = historyUiState.value) {
            HistoryUiState.Empty -> {
                HistoryEmpty()
            }

            HistoryUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Loading()
                }
            }

            is HistoryUiState.Success -> {
                HistoryListItem(
                    paddingValues = paddingValues,
                    state = state,
                    onClickItem = viewModel::updateDataBottomSheet,
                    onClickFavorite = viewModel::updateItemFavoriteStatus,
                    onDelete = viewModel::deleteHistory
                )
            }
        }
        if (showBottomSheet.value != null) {
            val data = showBottomSheet.value ?: return@Scaffold
            LinkDetailBottomSheet(
                scope = scope,
                onClose = { viewModel.updateDataBottomSheet(null) },
                sheetState = sheetState,
                data = BottomSheetData(
                    id = data.id,
                    isFavorite = data.isFavorite,
                    link = data.title
                ),
                onClickFavorite = { viewModel.updateItemFavoriteStatus(showBottomSheet.value!!) }
            )
        }
    }
}

@Composable
private fun HistoryEmpty() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(120.dp),
            painter = painterResource(R.drawable.box),
            contentDescription = "Box icon",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            stringResource(R.string.don_t_has_history_of_scan),
            color = MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HistoryListItem(
    paddingValues: PaddingValues,
    state: HistoryUiState.Success,
    onClickItem: (HistoryData) -> Unit,
    onClickFavorite: (HistoryData) -> Unit,
    onDelete: (HistoryData) -> Unit
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.padding(paddingValues),
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsWithAd(
            items = state.data,
            paddingItem = 12.dp,
            key = { item -> item.id },
            adItem = { NativeAdView(context) },
            itemContent = { history ->
                SwipeToDeleteContainer(
                    item = history,
                    onDelete = { item -> onDelete.invoke(item) }
                ) { item ->
                    HistoryItem(
                        data = item,
                        onClickItem = { historyData -> onClickItem(historyData) },
                        onClickFavorite = onClickFavorite
                    )
                }
            }
        )
    }
}

@Composable
fun HistoryItem(
    data: HistoryData,
    onClickItem: (HistoryData) -> Unit,
    onClickFavorite: (HistoryData) -> Unit
) {
    var isFavoriteState by remember { mutableStateOf(data.isFavorite) }
    val startIcon by remember(isFavoriteState) {
        mutableIntStateOf(
            if (isFavoriteState) R.drawable.ic_star_selected else R.drawable.ic_star
        )
    }
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onClickItem.invoke(data)
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(16.dp)

        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = data.title,
                    style = MaterialTheme.typography.titleMedium.copy(Color.Black)
                )
                IconButton(onClick = {
                    isFavoriteState = !isFavoriteState
                    onClickFavorite.invoke(data)
                }) {
                    Icon(
                        painter = painterResource(startIcon),
                        contentDescription = "favorite",
                        tint = Color.Unspecified
                    )
                }
            }
            HorizontalDivider(
                Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                data.createDate,
                style = MaterialTheme.typography.bodyMedium.copy(Color.LightGray)
            )
        }
    }
}

@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDelete: (T) -> Unit,
    animationDuration: Int = 500,
    content: @Composable (T) -> Unit
) {
    var isRemoved by remember { mutableStateOf(false) }
    val state = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled,
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                isRemoved = true
                true
            } else {
                false
            }
        }
    )

    LaunchedEffect(key1 = isRemoved) {
        if (isRemoved) {
            delay(animationDuration.toLong())
            onDelete(item)
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismissBox(
            state = state,
            backgroundContent = { DeleteItemBackground(state) },
            content = { content(item) },
            enableDismissFromStartToEnd = false
        )
    }
}

@Composable
fun DeleteItemBackground(swipeDismissState: SwipeToDismissBoxState) {
    val color = if (swipeDismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
        Color.Red
    } else {
        Color.Transparent
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .padding(12.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            null,
            tint = Color.White
        )
    }
}

@Preview
@Composable
private fun DeleteItemBackgroundPreview() {
    val history = HistoryData(0, "test1", false, "14 fev 2025")
    QrCodeTheme {
        SwipeToDeleteContainer(
            item = history,
            onDelete = { item ->
                Log.d("test", ">>> $item")
            }
        ) { item ->
            HistoryItem(data = item, {}, {})
        }
    }
}

@Preview
@Composable
private fun HistoryEmptyPreview() {
    QrCodeTheme {
        HistoryEmpty()
    }
}


@Preview
@Composable
private fun HistoryItemPreview() {
    QrCodeTheme {
        HistoryItem(data = HistoryData(id = 0, title = "test", createDate = "01/01/2025"), {}, {})
    }
}
