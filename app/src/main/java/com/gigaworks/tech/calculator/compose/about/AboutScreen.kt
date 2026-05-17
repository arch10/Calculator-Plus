package com.gigaworks.tech.calculator.compose.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gigaworks.tech.calculator.BuildConfig
import com.gigaworks.tech.calculator.R
import com.gigaworks.tech.calculator.compose.components.BackTopBar
import com.gigaworks.tech.calculator.compose.components.PreferenceRow
import com.gigaworks.tech.calculator.compose.components.PreferenceRowDivider
import com.gigaworks.tech.calculator.compose.components.PreferenceRowGroup
import com.gigaworks.tech.calculator.compose.theme.CalculatorPlusTheme

const val PRIVACY_URL =
    "https://raw.githubusercontent.com/arch10/Calculator-Plus/main/docs/en/privacy_policy.md"
const val TERMS_OF_USE_URL =
    "https://raw.githubusercontent.com/arch10/Calculator-Plus/main/docs/en/terms_and_conditions.md"
const val BETA_TESTING_LINK =
    "https://play.google.com/apps/testing/com.gigaworks.tech.calculator"

private data class AboutRow(
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit,
)

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    onNavigateToOpenSource: () -> Unit,
    onJoinBetaClick: () -> Unit,
    onLaunchUrl: (String) -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = { BackTopBar(title = stringResource(R.string.title_activity_about), onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        AboutContent(
            contentPadding = innerPadding,
            onJoinBetaClick = onJoinBetaClick,
            onLaunchUrl = onLaunchUrl,
            onOpenSourceClick = onNavigateToOpenSource,
        )
    }
}

@Composable
private fun AboutContent(
    contentPadding: PaddingValues,
    onJoinBetaClick: () -> Unit,
    onLaunchUrl: (String) -> Unit,
    onOpenSourceClick: () -> Unit,
) {
    val rows = listOf(
        AboutRow(
            title = stringResource(R.string.join_beta),
            subtitle = stringResource(R.string.join_beta_desc),
            onClick = onJoinBetaClick,
        ),
        AboutRow(
            title = stringResource(R.string.open_source),
            subtitle = stringResource(R.string.open_source_desc),
            onClick = onOpenSourceClick,
        ),
        AboutRow(
            title = stringResource(R.string.privacy_policy),
            subtitle = stringResource(R.string.privacy_policy_desc),
            onClick = { onLaunchUrl(PRIVACY_URL) },
        ),
        AboutRow(
            title = stringResource(R.string.terms_of_use),
            subtitle = stringResource(R.string.terms_of_use_desc),
            onClick = { onLaunchUrl(TERMS_OF_USE_URL) },
        ),
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + 32.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
            start = 16.dp,
            end = 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        item { Header() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            PreferenceRowGroup {
                rows.forEachIndexed { index, row ->
                    PreferenceRow(title = row.title, subtitle = row.subtitle, onClick = row.onClick)
                    if (index != rows.lastIndex) PreferenceRowDivider()
                }
            }
        }
    }
}

@Composable
private fun Header() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.about_icon),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.fillMaxWidth().height(92.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${stringResource(R.string.version)}: ${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    CalculatorPlusTheme {
        AboutScreen(
            onBack = {},
            onNavigateToOpenSource = {},
            onJoinBetaClick = {},
            onLaunchUrl = {},
        )
    }
}
