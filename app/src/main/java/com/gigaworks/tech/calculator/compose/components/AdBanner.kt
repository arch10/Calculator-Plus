package com.gigaworks.tech.calculator.compose.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * Banner AdView wrapped in AndroidView with lifecycle-aware pause/resume/destroy.
 * Caller is responsible for upstream gating (Remote Config + UMP consent + ad unit availability).
 */
@Composable
fun AdBanner(
    adUnitId: String,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val adViewHolder = remember { AdViewHolder() }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
                adViewHolder.adView = this
            }
        },
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            val view = adViewHolder.adView ?: return@LifecycleEventObserver
            when (event) {
                Lifecycle.Event.ON_RESUME -> view.resume()
                Lifecycle.Event.ON_PAUSE -> view.pause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            adViewHolder.adView?.destroy()
            adViewHolder.adView = null
        }
    }
}

// Plain holder so the AdView reference survives recomposition without going through Compose state
// (avoids spurious recompositions when the value is set inside the AndroidView factory).
private class AdViewHolder(var adView: AdView? = null)
