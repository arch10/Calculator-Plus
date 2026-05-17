package com.gigaworks.tech.calculator.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gigaworks.tech.calculator.compose.theme.CalculatorPlusTheme
import com.gigaworks.tech.calculator.util.AccentTheme

// Phase 0 smoke test. Not declared in AndroidManifest — launch via adb if needed:
//   adb shell am start -n com.gigaworks.tech.calculator/.compose.PlaygroundActivity
// Delete this file before Phase 4 ships.
class PlaygroundActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PlaygroundContent() }
    }
}

@Composable
private fun PlaygroundContent() {
    var accent by remember { mutableStateOf(AccentTheme.BLUE) }
    CalculatorPlusTheme(accent = accent) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Compose ✓ accent=$accent", style = MaterialTheme.typography.headlineSmall)
                    AccentTheme.entries.forEach { option ->
                        Button(onClick = { accent = option }) { Text(option.name) }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PlaygroundPreview() = PlaygroundContent()
