package com.gigaworks.tech.calculator.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gigaworks.tech.calculator.compose.about.AboutScreen
import com.gigaworks.tech.calculator.compose.about.BETA_TESTING_LINK
import com.gigaworks.tech.calculator.compose.about.ChangeLogScreen
import com.gigaworks.tech.calculator.compose.about.OpenSourceScreen
import com.gigaworks.tech.calculator.compose.theme.CalculatorPlusTheme
import com.gigaworks.tech.calculator.util.AccentTheme
import com.gigaworks.tech.calculator.util.AppPreference
import com.gigaworks.tech.calculator.util.JOIN_BETA
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent

private object Routes {
    const val ABOUT = "about"
    const val CHANGELOG = "changelog"
    const val OPEN_SOURCE = "opensource"
}

class AboutActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        firebaseAnalytics = Firebase.analytics

        val accent = readAccentPreference()

        setContent {
            CalculatorPlusTheme(accent = accent, darkTheme = isSystemInDarkTheme()) {
                AboutNavHost(
                    onFinish = { finish() },
                    onJoinBeta = { logBetaClickAndOpenPlayStore() },
                )
            }
        }
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

    private fun logBetaClickAndOpenPlayStore() {
        firebaseAnalytics.logEvent(JOIN_BETA, null)
        startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(BETA_TESTING_LINK) })
    }
}

@androidx.compose.runtime.Composable
private fun AboutNavHost(onFinish: () -> Unit, onJoinBeta: () -> Unit) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()

    val launchUrl: (String) -> Unit = remember(toolbarColor) {
        { url ->
            val params = CustomTabColorSchemeParams.Builder().setToolbarColor(toolbarColor).build()
            val intent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setDefaultColorSchemeParams(params)
                .build()
            intent.launchUrl(context, Uri.parse(url))
        }
    }

    val openExternal: (String) -> Unit = remember {
        { url -> context.startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }) }
    }

    NavHost(navController = navController, startDestination = Routes.ABOUT) {
        composable(Routes.ABOUT) {
            AboutScreen(
                onBack = onFinish,
                onNavigateToOpenSource = { navController.navigate(Routes.OPEN_SOURCE) },
                onJoinBetaClick = onJoinBeta,
                onLaunchUrl = launchUrl,
            )
        }
        composable(Routes.CHANGELOG) {
            ChangeLogScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.OPEN_SOURCE) {
            OpenSourceScreen(
                onBack = { navController.popBackStack() },
                onLicenseClick = { license -> openExternal(license.url) },
            )
        }
    }
}
