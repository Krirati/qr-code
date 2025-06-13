package com.kstudio.qrcode.ui.component.ad

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.AdChoicesView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdOptions.ADCHOICES_TOP_RIGHT
import com.google.android.gms.ads.nativead.NativeAdView
import com.kstudio.qrcode.R
import kotlin.math.abs

@Composable
fun NativeAdView(context: Context, modifier: Modifier = Modifier) {
    val contentViewId by remember { mutableIntStateOf(View.generateViewId()) }
    val adViewId by remember { mutableIntStateOf(View.generateViewId()) }
    val nativeAd = remember { mutableStateOf<NativeAd?>(null) }
    val adLoader = remember {
        AdLoader.Builder(context, context.getString(R.string.ad_id_native_advance))
            .forNativeAd { ad: NativeAd ->
                nativeAd.value = ad
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdMob", "Ad failed to load: ${adError.message}")
                }
            })
            .withNativeAdOptions(
                NativeAdOptions
                    .Builder()
                    .setRequestCustomMuteThisAd(false)
                    .setAdChoicesPlacement(ADCHOICES_TOP_RIGHT)
                    .build()
            )
            .build()
    }

    LaunchedEffect(Unit) {
        adLoader.loadAd(AdRequest.Builder().build())
    }

    nativeAd.value?.let { ad ->
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray),
            factory = { context ->
                val contentView = ComposeView(context).apply {
                    id = contentViewId
                }

                NativeAdView(context).apply {
                    id = adViewId
                    addView(contentView)
                }
            },
            update = { view ->
                val adView = view.findViewById<NativeAdView>(adViewId)
                val contentView = view.findViewById<ComposeView>(contentViewId)
                val adChoicesView = AdChoicesView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                adView.addView(adChoicesView)
                adView.adChoicesView = adChoicesView
                adView.callToActionView = contentView
                adView.setNativeAd(ad)
                contentView.setContent { LoadAdContent(ad, contentView) }
            }
        )
    }
}

@Composable
private fun LoadAdContent(nativeAd: NativeAd?, composeView: View) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
    ) {
        AdMainContent(nativeAd)
        Spacer(Modifier.height(12.dp))
        AdDescriptionContent(nativeAd)
        Button(
            onClick = { composeView.performClick() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(text = nativeAd?.callToAction ?: "")
        }
    }
}

@Composable
private fun AdMainContent(nativeAd: NativeAd?) {
    Box {
        val mediaContent: Drawable? = nativeAd?.mediaContent?.mainImage
        mediaContent?.let { drawable ->
            Image(
                painter = rememberAsyncImagePainter(model = drawable),
                contentDescription = "Ad",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            "Ad",
            style = MaterialTheme.typography.titleMedium.copy(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .defaultMinSize(20.dp)
                .padding(4.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(4.dp)
        )
    }
}

@Composable
private fun AdDescriptionContent(nativeAd: NativeAd?) {
    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
        val iconApp: Drawable? = nativeAd?.icon?.drawable
        iconApp?.let { drawable ->
            Image(
                painter = rememberAsyncImagePainter(model = drawable),
                contentDescription = "Ad"/*it.icon?.contentDescription*/,
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = nativeAd?.headline ?: "",
                style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.primary)
            )
            Text(
                nativeAd?.body.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.primary),
            )
        }
    }
}

inline fun <T> LazyListScope.itemsWithAd(
    items: List<T>,
    addEvery: Int = 3,
    paddingItem: Dp,
    noinline key: ((item: T) -> Any)? = null,
    crossinline adItem: @Composable LazyItemScope.() -> Unit,
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit
) = items(
    count = items.size,
    key = if (key != null) { index: Int -> key(items[index]) } else null
) {
    itemContent(items[it])
    if (abs(it -1) % addEvery == 0 && it != 0) {
        Spacer(Modifier.height(paddingItem))
        adItem()
    }
}