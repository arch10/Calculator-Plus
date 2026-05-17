package com.gigaworks.tech.calculator.compose.about

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gigaworks.tech.calculator.R
import com.gigaworks.tech.calculator.compose.components.BackTopBar

// Stub screen — the original ChangeLogFragment was reachable in nav but the entry point in
// AboutScreen is currently hidden. Kept as a destination so the route stays valid.
@Composable
fun ChangeLogScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = { BackTopBar(title = stringResource(R.string.whats_new), onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.whats_new),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
