package com.gigaworks.tech.calculator.ui.history

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gigaworks.tech.calculator.R
import com.gigaworks.tech.calculator.compose.history.HistoryScreen
import com.gigaworks.tech.calculator.compose.theme.CalculatorPlusTheme
import com.gigaworks.tech.calculator.domain.HistoryAdapterItem
import com.gigaworks.tech.calculator.ui.history.viewmodel.HistoryViewModel
import com.gigaworks.tech.calculator.ui.main.helper.removeNumberSeparator
import com.gigaworks.tech.calculator.util.ADS_DISABLED
import com.gigaworks.tech.calculator.util.ADS_ENABLED
import com.gigaworks.tech.calculator.util.AccentTheme
import com.gigaworks.tech.calculator.util.AppPreference
import com.gigaworks.tech.calculator.util.GoogleMobileAdsConsentManager
import com.gigaworks.tech.calculator.util.SHARE_EXPRESSION
import com.gigaworks.tech.calculator.util.logD
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.remoteConfig
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        firebaseAnalytics = Firebase.analytics

        val accent = readAccentPreference()
        val adUnitId = resolveAdUnitId()

        setContent {
            CalculatorPlusTheme(accent = accent, darkTheme = isSystemInDarkTheme()) {
                HistoryScreenHost(adUnitId = adUnitId)
            }
        }
    }

    @Composable
    private fun HistoryScreenHost(adUnitId: String?) {
        val viewModel: HistoryViewModel = hiltViewModel()
        val items by viewModel.historyItems.collectAsStateWithLifecycle()
        HistoryScreen(
            items = items,
            adUnitId = adUnitId,
            onBack = { finish() },
            onItemClick = { item: HistoryAdapterItem ->
                viewModel.saveExpression(removeNumberSeparator(item.expression))
                finish()
            },
            onItemDelete = { item -> viewModel.deleteHistory(item.expression) },
            onItemShare = { item -> shareItem(item) },
            onClearAll = { viewModel.clearHistory() },
        )
    }

    private fun shareItem(item: HistoryAdapterItem) {
        val sharedEquation = "${item.expression} = ${item.result}"
        firebaseAnalytics.logEvent(SHARE_EXPRESSION, null)
        startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Calculator Plus Expression")
                    putExtra(Intent.EXTRA_TEXT, sharedEquation)
                },
                getString(R.string.choose),
            )
        )
    }

    private fun readAccentPreference(): AccentTheme {
        val prefs = AppPreference(this)
        val default = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            AccentTheme.DYNAMIC.name
        } else {
            AccentTheme.BLUE.name
        }
        val raw = prefs.getStringPreference(AppPreference.ACCENT_THEME, default)
        return runCatching { AccentTheme.valueOf(raw) }.getOrDefault(AccentTheme.BLUE)
    }

    // Returns the ad unit id if all of: Remote Config enable_ads, non-empty history_ad_id, and
    // UMP consent are satisfied. Null means do not render the banner.
    private fun resolveAdUnitId(): String? {
        val remoteConfig = Firebase.remoteConfig
        if (!remoteConfig["enable_ads"].asBoolean()) {
            logD("disabling ads due to remote config")
            firebaseAnalytics.logEvent(ADS_DISABLED) { param("reason", "ads_disabled") }
            return null
        }
        val adUnitId = remoteConfig["history_ad_id"].asString()
        if (adUnitId.isEmpty()) {
            logD("disabling ads due to empty ad unit id")
            firebaseAnalytics.logEvent(ADS_DISABLED) { param("reason", "empty_ad_unit") }
            return null
        }
        val consent = GoogleMobileAdsConsentManager.getInstance(applicationContext)
        if (!consent.canRequestAds) return null
        firebaseAnalytics.logEvent(ADS_ENABLED, null)
        return adUnitId
    }
}
