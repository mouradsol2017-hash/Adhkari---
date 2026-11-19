package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Mosque
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.DhikrCategory
import com.example.data.model.DhikrItem
import com.example.data.model.ThemePreference
import com.example.localization.AppStrings
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.StreakAmber
import com.example.ui.viewmodel.AzkariUiState
import java.util.Calendar

@Composable
fun HomeScreen(
    uiState: AzkariUiState,
    strings: AppStrings,
    isDarkTheme: Boolean,
    onToggleDarkMode: (ThemePreference) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCategoryClick: (DhikrCategory) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onAddCustomDhikr: (String, String, String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val isMorning = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 15 }
    val greeting = if (isMorning) strings.goodMorning else strings.goodEvening

    val totalBuiltIn = uiState.allAthkar.size.coerceAtLeast(1)
    val todayDone = uiState.todayCompletedIds.size
    val todayFraction = (todayDone.toFloat() / totalBuiltIn.toFloat()).coerceIn(0f, 1f)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header + Quick Dark Mode Toggle + Add Custom Dhikr
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.appName,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalIconButton(
                        onClick = {
                            val next = if (isDarkTheme) ThemePreference.LIGHT else ThemePreference.DARK
                            onToggleDarkMode(next)
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("quick_dark_mode_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                            contentDescription = strings.appearanceSection
                        )
                    }

                    FilledTonalIconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("add_custom_dhikr_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = strings.addCustomDhikr
                        )
                    }
                }
            }
        }

        // Hero Progress & Streak Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = strings.todayProgress,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    text = "$todayDone / $totalBuiltIn",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.18f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.LocalFireDepartment,
                                        contentDescription = strings.streakDays,
                                        tint = StreakAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "${uiState.stats.currentStreak} ${strings.dayStreakUnit}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }

                        LinearProgressIndicator(
                            progress = { todayFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.22f)
                        )
                    }
                }
            }
        }

        // Instant Quick Search Bar
        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_search_input"),
                placeholder = { Text(strings.searchHint) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = strings.searchHint
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = strings.cancel
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }

        // Search Results OR Category Grid
        if (uiState.searchQuery.isNotBlank()) {
            val results = uiState.searchResults
            if (results.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = strings.noSearchResults,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(results, key = { it.id }) { item ->
                    SearchResultCard(
                        item = item,
                        uiState = uiState,
                        onOpenCategory = { onCategoryClick(item.category) },
                        onToggleFavorite = { onToggleFavorite(item.id) }
                    )
                }
            }
        } else {
            item {
                Text(
                    text = strings.categoriesTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            val categories = listOf(
                DhikrCategory.MORNING,
                DhikrCategory.EVENING,
                DhikrCategory.AFTER_PRAYER,
                DhikrCategory.SLEEP,
                DhikrCategory.WAKING_UP,
                DhikrCategory.GENERAL,
                DhikrCategory.FAVORITES,
                DhikrCategory.CUSTOM
            )

            items(categories.chunked(2)) { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { cat ->
                        val itemsInCat = uiState.athkarForCategory(cat)
                        val doneInCat = itemsInCat.count { uiState.todayCompletedIds.contains(it.id) }
                        CategoryCard(
                            category = cat,
                            title = strings.categoryNames[cat] ?: cat.id,
                            countLabel = "$doneInCat / ${itemsInCat.size}",
                            icon = categoryIcon(cat),
                            onClick = { onCategoryClick(cat) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("category_card_${cat.id}")
                        )
                    }
                    if (pair.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        item { Spacer(Modifier.height(12.dp)) }
    }

    if (showAddDialog) {
        AddCustomDhikrDialog(
            strings = strings,
            onDismiss = { showAddDialog = false },
            onSave = { title, text, translation, count ->
                onAddCustomDhikr(title, text, translation, count)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun CategoryCard(
    category: DhikrCategory,
    title: String,
    countLabel: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAccent = category == DhikrCategory.FAVORITES || category == DhikrCategory.CUSTOM
    Card(
        modifier = modifier
            .height(132.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAccent) {
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (isAccent) MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f)
                            else MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isAccent) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = countLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SearchResultCard(
    item: DhikrItem,
    uiState: AzkariUiState,
    onOpenCategory: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val isFav = uiState.favoriteIds.contains(item.id)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onOpenCategory),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFav) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFav) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = item.arabicText,
                fontFamily = AmiriFontFamily,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AddCustomDhikrDialog(
    strings: AppStrings,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var translation by remember { mutableStateOf("") }
    var targetStr by remember { mutableStateOf("3") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.addCustomDhikr) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= 120) title = it },
                    label = { Text(strings.customDhikrTitleLabel) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_dhikr_title_input")
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { if (it.length <= 1500) text = it },
                    label = { Text(strings.customDhikrTextLabel) },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_dhikr_text_input")
                )
                OutlinedTextField(
                    value = translation,
                    onValueChange = { if (it.length <= 1500) translation = it },
                    label = { Text(strings.customDhikrTranslationLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { input -> targetStr = input.filter { it.isDigit() }.take(4) },
                    label = { Text(strings.targetCountLabel) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val count = targetStr.toIntOrNull()?.coerceIn(1, 1000) ?: 1
                    onSave(title, text, translation, count)
                },
                enabled = title.isNotBlank() && text.isNotBlank(),
                modifier = Modifier.testTag("save_custom_dhikr_button")
            ) {
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}

private fun categoryIcon(category: DhikrCategory): ImageVector = when (category) {
    DhikrCategory.MORNING -> Icons.Rounded.WbSunny
    DhikrCategory.EVENING -> Icons.Rounded.NightsStay
    DhikrCategory.AFTER_PRAYER -> Icons.Rounded.Mosque
    DhikrCategory.SLEEP -> Icons.Rounded.Bedtime
    DhikrCategory.WAKING_UP -> Icons.Rounded.WbTwilight
    DhikrCategory.GENERAL -> Icons.Rounded.AutoAwesome
    DhikrCategory.FAVORITES -> Icons.Rounded.Favorite
    DhikrCategory.CUSTOM -> Icons.Rounded.EditNote
}
