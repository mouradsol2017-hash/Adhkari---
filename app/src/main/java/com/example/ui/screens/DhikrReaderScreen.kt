package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DhikrCategory
import com.example.data.model.DhikrItem
import com.example.data.model.FontScalePreference
import com.example.localization.AppStrings
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.SuccessJade
import com.example.ui.viewmodel.AzkariUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DhikrReaderScreen(
    category: DhikrCategory,
    uiState: AzkariUiState,
    strings: AppStrings,
    onBack: () -> Unit,
    onTapDhikr: (DhikrItem) -> Boolean,
    onToggleFavorite: (String) -> Unit,
    onDeleteCustomDhikr: (String) -> Unit,
    onCycleFontScale: (FontScalePreference) -> Unit,
    onResetCategory: (List<DhikrItem>) -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val items = uiState.athkarForCategory(category)
    val title = strings.categoryNames[category] ?: category.id
    val allDone = items.isNotEmpty() && items.all {
        (uiState.sessionCounts[it.id] ?: 0) >= it.targetCount
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("reader_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = strings.cancel
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val entries = FontScalePreference.entries
                            val nextIdx = (entries.indexOf(uiState.fontScale) + 1) % entries.size
                            onCycleFontScale(entries[nextIdx])
                        },
                        modifier = Modifier.testTag("reader_font_size_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FormatSize,
                            contentDescription = strings.fontSizeSection
                        )
                    }
                    IconButton(
                        onClick = { onResetCategory(items) },
                        modifier = Modifier.testTag("reader_reset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = strings.resetProgress
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (items.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.FavoriteBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = if (category == DhikrCategory.FAVORITES) {
                        strings.emptyFavoritesTip
                    } else {
                        strings.emptyCustomTip
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (allDone) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = strings.allCompletedTitle,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = strings.allCompletedBody,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(12.dp))
                                Button(onClick = { onResetCategory(items) }) {
                                    Text(strings.resetProgress)
                                }
                            }
                        }
                    }
                }

                items(items, key = { it.id }) { item ->
                    val currentCount = uiState.sessionCounts[item.id] ?: 0
                    val isDone = currentCount >= item.targetCount
                    val isFav = uiState.favoriteIds.contains(item.id)

                    DhikrReaderCard(
                        item = item,
                        currentCount = currentCount,
                        isDone = isDone,
                        isFavorite = isFav,
                        uiState = uiState,
                        strings = strings,
                        onTap = {
                            val completedNow = onTapDhikr(item)
                            if (uiState.settings.hapticEnabled) {
                                haptic.performHapticFeedback(
                                    if (completedNow) HapticFeedbackType.LongPress
                                    else HapticFeedbackType.TextHandleMove
                                )
                            }
                        },
                        onToggleFavorite = { onToggleFavorite(item.id) },
                        onShare = {
                            val shareText = buildString {
                                append(item.localizedTitle(uiState.language))
                                append("\n\n")
                                append(item.arabicText)
                                val tr = item.localizedTranslation(uiState.language)
                                if (tr.isNotBlank()) {
                                    append("\n\n")
                                    append(tr)
                                }
                                append("\n\n— ${strings.appName}")
                            }
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, strings.shareDhikr))
                        },
                        onDelete = if (item.isCustom) {
                            { onDeleteCustomDhikr(item.id.removePrefix("custom_")) }
                        } else null
                    )
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun DhikrReaderCard(
    item: DhikrItem,
    currentCount: Int,
    isDone: Boolean,
    isFavorite: Boolean,
    uiState: AzkariUiState,
    strings: AppStrings,
    onTap: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit,
    onDelete: (() -> Unit)?
) {
    val progress by animateFloatAsState(
        targetValue = (currentCount.toFloat() / item.targetCount.toFloat()).coerceIn(0f, 1f),
        animationSpec = spring(),
        label = "dhikr_progress"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isDone) SuccessJade else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
        label = "dhikr_border"
    )
    val fontSize = (23f * uiState.fontScale.multiplier).sp
    val lineHeight = (40f * uiState.fontScale.multiplier).sp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(enabled = !isDone, onClick = onTap)
            .testTag("dhikr_card_${item.id}"),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(if (isDone) 2.dp else 1.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.localizedTitle(uiState.language),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onShare) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = strings.shareDhikr,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (onDelete != null) {
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteOutline,
                                contentDescription = strings.delete,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Sacred Arabic Dhikr Text in Amiri font
            Text(
                text = item.arabicText,
                fontFamily = AmiriFontFamily,
                fontSize = fontSize,
                lineHeight = lineHeight,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            // Localized translation for EN/FR
            val translation = item.localizedTranslation(uiState.language)
            if (translation.isNotBlank()) {
                if (item.transliteration.isNotBlank()) {
                    Text(
                        text = item.transliteration,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Text(
                    text = translation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            val virtue = item.localizedVirtue(uiState.language)
            if (virtue.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                ) {
                    Text(
                        text = "${strings.virtueLabel}: $virtue",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isDone) SuccessJade else MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isDone) strings.completedBadge else strings.tapToCount,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isDone) SuccessJade else MaterialTheme.colorScheme.primary
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isDone) SuccessJade else MaterialTheme.colorScheme.primary
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isDone) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                        }
                        Text(
                            text = "$currentCount / ${item.targetCount}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}
