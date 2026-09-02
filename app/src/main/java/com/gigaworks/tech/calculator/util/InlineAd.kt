package com.gigaworks.tech.calculator.util

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.remoteConfig

const val HISTORY_INLINE_AD_ID = "history_inline_ad_id"
const val SETTINGS_INLINE_AD_ID = "settings_inline_ad_id"
const val ABOUT_INLINE_AD_ID = "about_inline_ad_id"

/**
 * Position of the inline ad inside the history list. Clamped to the list size
 * so shorter lists still render the ad as the last row.
 */
const val HISTORY_INLINE_AD_POSITION = 3

sealed class InlineAdDecision {
    data class Allowed(val adUnitId: String) : InlineAdDecision()
    data class Blocked(val reason: String) : InlineAdDecision()
}

/**
 * Inline ads are an ad revenue experiment on top of the existing banners, so they
 * need both the master `enable_ads` switch and the `enable_inline_ads` flag to be on.
 */
fun resolveInlineAd(adUnitIdKey: String): InlineAdDecision {
    val remoteConfig = Firebase.remoteConfig
    if (!remoteConfig["enable_ads"].asBoolean()) {
        return InlineAdDecision.Blocked("ads_disabled")
    }
    if (!remoteConfig["enable_inline_ads"].asBoolean()) {
        return InlineAdDecision.Blocked("inline_ads_disabled")
    }
    val adUnitId = remoteConfig[adUnitIdKey].asString()
    if (adUnitId.isEmpty()) {
        return InlineAdDecision.Blocked("empty_ad_unit")
    }
    return InlineAdDecision.Allowed(adUnitId)
}

/**
 * A 300x250 [AdView] reserves its full height even when the request does not fill, so the
 * placement is only surfaced through [onLoaded] to avoid a blank block in the scroll space.
 */
fun createInlineAdView(
    context: Context,
    adUnitId: String,
    screenName: String,
    onLoaded: (AdView) -> Unit
): AdView {
    val adView = AdView(context)
    adView.setAdSize(AdSize.MEDIUM_RECTANGLE)
    adView.adUnitId = adUnitId
    adView.adListener = object : AdListener() {
        override fun onAdLoaded() {
            adView.responseInfo.logAdSource(screenName)
            onLoaded(adView)
        }

        override fun onAdFailedToLoad(error: LoadAdError) {
            printLogD(screenName, "inline ad failed to load: ${error.message}")
            error.responseInfo.logAdSource(screenName)
        }
    }
    adView.loadAd(AdRequest.Builder().build())
    return adView
}
