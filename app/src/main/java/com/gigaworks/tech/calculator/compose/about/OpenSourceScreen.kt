package com.gigaworks.tech.calculator.compose.about

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gigaworks.tech.calculator.R
import com.gigaworks.tech.calculator.compose.components.BackTopBar
import com.gigaworks.tech.calculator.domain.License

private val LicenseData = listOf(
    License("big-math", "eobermuhlner", "MIT License", "https://github.com/eobermuhlner/big-math"),
    License("TapTargetView", "KeepSafe", "Apache License, Version 2.0", "https://github.com/KeepSafe/TapTargetView"),
    License("Firebase", "firebase", "Apache License, Version 2.0", "https://github.com/firebase/firebase-android-sdk"),
)

@Composable
fun OpenSourceScreen(
    onBack: () -> Unit,
    onLicenseClick: (License) -> Unit,
) {
    Scaffold(
        topBar = { BackTopBar(title = stringResource(R.string.open_source), onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
            ),
        ) {
            items(items = LicenseData, key = { it.libraryName }) { license ->
                LicenseRow(
                    license = license,
                    onClick = { onLicenseClick(license) },
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(durationMillis = 200),
                        placementSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    ),
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
        }
    }
}

@Composable
private fun LicenseRow(
    license: License,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = license.libraryName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = license.owner,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = license.name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
