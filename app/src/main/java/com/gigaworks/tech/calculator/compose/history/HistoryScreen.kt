package com.gigaworks.tech.calculator.compose.history

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.gigaworks.tech.calculator.R
import com.gigaworks.tech.calculator.compose.components.AdBanner
import com.gigaworks.tech.calculator.domain.HistoryAdapterItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    items: List<HistoryAdapterItem>,
    adUnitId: String?,
    onBack: () -> Unit,
    onItemClick: (HistoryAdapterItem) -> Unit,
    onItemDelete: (HistoryAdapterItem) -> Unit,
    onItemShare: (HistoryAdapterItem) -> Unit,
    onClearAll: () -> Unit,
) {
    var showClearConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            HistoryTopBar(
                onBack = onBack,
                onClearAll = { showClearConfirm = true },
                showClearAction = items.isNotEmpty(),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { if (adUnitId != null) AdBanner(adUnitId = adUnitId) },
    ) { innerPadding ->
        if (items.isEmpty()) {
            EmptyState(modifier = Modifier.padding(innerPadding).fillMaxSize())
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding() + 16.dp,
                ),
            ) {
                items(items = items, key = { "${it.date}|${it.expression}" }) { item ->
                    var pendingDelete by remember { mutableStateOf(false) }
                    HistoryRow(
                        item = item,
                        onClick = { onItemClick(item) },
                        onShare = { onItemShare(item) },
                        onRequestDelete = { pendingDelete = true },
                        modifier = Modifier.animateItem(
                            fadeInSpec = tween(durationMillis = 220),
                            placementSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        ),
                    )
                    if (pendingDelete) {
                        DeleteConfirmDialog(
                            item = item,
                            onConfirm = {
                                pendingDelete = false
                                onItemDelete(item)
                            },
                            onDismiss = { pendingDelete = false },
                        )
                    }
                }
            }
        }
    }

    if (showClearConfirm) {
        ClearAllConfirmDialog(
            onConfirm = {
                showClearConfirm = false
                onClearAll()
            },
            onDismiss = { showClearConfirm = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryTopBar(
    onBack: () -> Unit,
    onClearAll: () -> Unit,
    showClearAction: Boolean,
) {
    TopAppBar(
        title = { Text(stringResource(R.string.title_activity_history), style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.arrow_left),
                    contentDescription = "Back",
                )
            }
        },
        actions = {
            if (showClearAction) {
                IconButton(onClick = onClearAll) {
                    Icon(
                        painter = painterResource(R.drawable.trash),
                        contentDescription = stringResource(R.string.clear),
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

@Composable
private fun HistoryRow(
    item: HistoryAdapterItem,
    onClick: () -> Unit,
    onShare: () -> Unit,
    onRequestDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuOpen by remember { mutableStateOf(false) }
    var pressOffsetPx by remember { mutableStateOf(IntOffset.Zero) }
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .indication(interactionSource, LocalIndication.current)
            // Custom tap detector so onLongPress receives the touch position; we anchor the
            // context menu at that point. onPress drives the interactionSource so the ripple
            // still fires.
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        val press = PressInteraction.Press(offset)
                        interactionSource.tryEmit(press)
                        val released = tryAwaitRelease()
                        interactionSource.tryEmit(
                            if (released) PressInteraction.Release(press)
                            else PressInteraction.Cancel(press)
                        )
                    },
                    onTap = { onClick() },
                    onLongPress = { offset ->
                        pressOffsetPx = IntOffset(offset.x.toInt(), offset.y.toInt())
                        menuOpen = true
                    },
                )
            }
            .padding(top = 16.dp),
    ) {
        if (!item.isPrevSame) {
            Text(
                text = item.date,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text(
            text = item.expression,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.result,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (!item.isNextSame) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }

        if (menuOpen) {
            Popup(
                alignment = Alignment.TopStart,
                offset = pressOffsetPx,
                onDismissRequest = { menuOpen = false },
                properties = PopupProperties(focusable = true),
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    tonalElevation = 3.dp,
                    shadowElevation = 3.dp,
                ) {
                    // IntrinsicSize.Max sizes the menu to its widest item rather than letting
                    // DropdownMenuItem's internal fillMaxWidth stretch it to the screen edge.
                    Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                        DropdownMenuItem(
                            text = { Text("Share") },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                            onClick = {
                                menuOpen = false
                                onShare()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.trash),
                                    contentDescription = null,
                                )
                            },
                            onClick = {
                                menuOpen = false
                                onRequestDelete()
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    item: HistoryAdapterItem,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete entry?") },
        text = { Text("${item.expression} = ${item.result}") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.history),
                contentDescription = stringResource(R.string.no_history),
                modifier = Modifier
                    .height(48.dp)
                    .alpha(0.7f),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(R.string.no_history),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun ClearAllConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.clear)) },
        text = { Text("Delete all history entries?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.clear), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
