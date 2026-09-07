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

const val HISTORY_AD_ID = "history_ad_id"
const val SETTINGS_AD_ID = "settings_ad_id"
const val ABOUT_AD_ID = "about_ad_id"
const val HISTORY_INLINE_AD_ID = "history_inline_ad_id"
const val SETTINGS_INLINE_AD_ID = "settings_inline_ad_id"

/**
 * Position of the inline ad inside the history list. Clamped to the list size
 * so shorter lists still render the ad as the last row.
 */
const val HISTORY_INLINE_AD_POSITION = 3

/**
 * The single ad a screen shows, in priority order: the inline 300x250 first, then the
 * anchored bottom banner, then nothing.
 */
sealed class AdPlacement {
    data class Inline(val adUnitId: String) : AdPlacement()
    data class BottomBanner(val adUnitId: String) : AdPlacement()
    data class None(val reason: String) : AdPlacement()
}

/**
 * Picks the one ad a screen shows, so a screen never stacks an inline ad and a banner.
 *
 * The choice is made from Remote Config before any request goes out, which means a screen
 * that picks the inline ad does not fall back to the banner when that request fails to
 * fill: the session stays purely inline so the experiment reads cleanly. An inline ad
 * whose unit id is unset is not a usable placement, so it falls through to the banner.
 *
 * The inline 300x250 is an ad revenue experiment, so it needs `enable_inline_ads` on top
 * of the master `enable_ads` switch; the banner needs only `enable_ads`.
 */
fun resolveAdPlacement(inlineAdUnitIdKey: String, bannerAdUnitIdKey: String): AdPlacement {
    val remoteConfig = Firebase.remoteConfig
    if (!remoteConfig["enable_ads"].asBoolean()) {
        return AdPlacement.None("ads_disabled")
    }
    if (remoteConfig["enable_inline_ads"].asBoolean()) {
        val inlineAdUnitId = remoteConfig[inlineAdUnitIdKey].asString()
        if (inlineAdUnitId.isNotEmpty()) {
            return AdPlacement.Inline(inlineAdUnitId)
        }
    }
    val bannerAdUnitId = remoteConfig[bannerAdUnitIdKey].asString()
    if (bannerAdUnitId.isNotEmpty()) {
        return AdPlacement.BottomBanner(bannerAdUnitId)
    }
    return AdPlacement.None("empty_ad_unit")
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
