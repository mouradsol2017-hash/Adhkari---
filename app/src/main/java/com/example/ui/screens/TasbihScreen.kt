package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.catalog.BuiltInAthkarCatalog
import com.example.data.model.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.components.IslamicGeometricBackground
import com.example.ui.components.TasbihProgressRing
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.viewmodel.AzkariUiState

@Composable
fun TasbihScreen(
    uiState: AzkariUiState,
    strings: AppStrings,
    onTapTasbih: () -> Boolean,
    onResetCurrent: () -> Unit,
    onSelectPhrase: (String, Int) -> Unit,
    onSelectTarget: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var showPhraseDialog by remember { mutableStateOf(false) }

    val selectedPhrase = remember(uiState.settings.tasbihPhraseId) {
        BuiltInAthkarCatalog.tasbihPhrases.firstOrNull {
            it.id == uiState.settings.tasbihPhraseId
        } ?: BuiltInAthkarCatalog.tasbihPhrases.first()
    }

    val currentCount = uiState.settings.tasbihCurrentCount
    val target = uiState.settings.tasbihTarget.coerceAtLeast(1L)
    val isOpenMode = target >= 9999L
    val rawProgress = if (isOpenMode) {
        ((currentCount % 100L) / 100f)
    } else {
        (currentCount.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    }
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
        label = "tasbih_ring_progress"
    )

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Compact Top Control Bar (Phrase Selector, Target Chips, Reset)
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { showPhraseDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("tasbih_phrase_selector")
                    ) {
                        Text(
                            text = selectedPhrase.arabic,
                            fontFamily = AmiriFontFamily,
                            maxLines = 1
                        )
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = strings.selectPhrase
                        )
                    }

                    IconButton(
                        onClick = onResetCurrent,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("tasbih_reset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = strings.reset
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val targets = listOf(33L to "33", 99L to "99", 100L to "100", 10000L to strings.openTarget)
                    targets.forEach { (value, label) ->
                        FilterChip(
                            selected = target == value,
                            onClick = { onSelectTarget(value) },
                            label = { Text(label) },
                            modifier = Modifier.testTag("tasbih_target_$value")
                        )
                    }
                }
            }
        }

        // Full-Screen Tap-Anywhere Eyes-Free Tasbih Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(
                        bounded = true,
                        color = MaterialTheme.colorScheme.secondary
                    ),
                    onClick = {
                        val completedCycle = onTapTasbih()
                        if (uiState.settings.hapticEnabled) {
                            haptic.performHapticFeedback(
                                if (completedCycle) HapticFeedbackType.LongPress
                                else HapticFeedbackType.TextHandleMove
                            )
                        }
                    }
                )
                .testTag("tasbih_tap_surface"),
            contentAlignment = Alignment.Center
        ) {
            IslamicGeometricBackground(modifier = Modifier.fillMaxSize())

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = selectedPhrase.arabic,
                    fontFamily = AmiriFontFamily,
                    fontSize = 30.sp,
                    lineHeight = 46.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                val subText = when (uiState.language) {
                    AppLanguage.AR -> ""
                    AppLanguage.EN -> "${selectedPhrase.transliteration}\n${selectedPhrase.translationEn}"
                    AppLanguage.FR -> "${selectedPhrase.transliteration}\n${selectedPhrase.translationFr}"
                }
                if (subText.isNotBlank()) {
                    Text(
                        text = subText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                // Glowing Circular Progress Ring & Count
                Box(
                    modifier = Modifier.size(236.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TasbihProgressRing(
                        progress = animatedProgress,
                        modifier = Modifier.fillMaxSize()
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentCount.toString(),
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 62.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.testTag("tasbih_count_text")
                        )
                        Text(
                            text = if (isOpenMode) strings.openTarget else "/ $target",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                // Lifetime Counter Pill
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = "${strings.lifetimeLabel}: ${uiState.settings.tasbihTotalLifetime}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Eyes-free touch hint
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.TouchApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.tapAnywhereHint,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    if (showPhraseDialog) {
        AlertDialog(
            onDismissRequest = { showPhraseDialog = false },
            title = { Text(strings.selectPhrase) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(BuiltInAthkarCatalog.tasbihPhrases, key = { it.id }) { phrase ->
                        val isSelected = phrase.id == selectedPhrase.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    onSelectPhrase(phrase.id, phrase.defaultTarget)
                                    showPhraseDialog = false
                                },
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = phrase.arabic,
                                    fontFamily = AmiriFontFamily,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                val subtitle = when (uiState.language) {
                                    AppLanguage.AR -> "${strings.targetLabel}: ${phrase.defaultTarget}"
                                    AppLanguage.EN -> phrase.translationEn
                                    AppLanguage.FR -> phrase.translationFr
                                }
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPhraseDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}
