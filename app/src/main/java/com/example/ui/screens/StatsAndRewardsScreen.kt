package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AchievementBadge
import com.example.data.model.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.components.ActivityBarChart
import com.example.ui.theme.StreakAmber
import com.example.ui.viewmodel.AzkariUiState

@Composable
fun StatsAndRewardsScreen(
    uiState: AzkariUiState,
    strings: AppStrings,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val stats = uiState.stats
    var showMonthly by remember { mutableStateOf(false) }

    val shareText = remember(stats, uiState.language) {
        val unlockedCount = stats.achievements.count { it.unlocked }
        when (uiState.language) {
            AppLanguage.AR ->
                "إنجازي في تطبيق أذكاري 🌿\n" +
                    "• سلسلة الالتزام: ${stats.currentStreak} يوم\n" +
                    "• إجمالي الأذكار: ${stats.totalAthkar}\n" +
                    "• إجمالي التسبيح: ${stats.totalTasbih}\n" +
                    "• المستوى ${stats.level} ($unlockedCount/${stats.achievements.size} شارات)\n" +
                    "اللهم أعنّا على ذكرك وشكرك وحسن عبادتك!"
            AppLanguage.EN ->
                "My Progress on Azkari 🌿\n" +
                    "• Daily Streak: ${stats.currentStreak} days\n" +
                    "• Athkar Completed: ${stats.totalAthkar}\n" +
                    "• Total Tasbih: ${stats.totalTasbih}\n" +
                    "• Level ${stats.level} ($unlockedCount/${stats.achievements.size} badges)"
            AppLanguage.FR ->
                "Mon progrès sur Azkari 🌿\n" +
                    "• Série quotidienne : ${stats.currentStreak} jours\n" +
                    "• Athkar complétés : ${stats.totalAthkar}\n" +
                    "• Total Tasbih : ${stats.totalTasbih}\n" +
                    "• Niveau ${stats.level} ($unlockedCount/${stats.achievements.size} badges)"
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = strings.statsTitle,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Level & Share Achievement Hero Card
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
                                    MaterialTheme.colorScheme.secondary
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.EmojiEvents,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "${strings.levelLabel} ${stats.level}",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Text(
                                        text = "${stats.xp} ${strings.xpUnit}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(
                                        Intent.createChooser(intent, strings.shareAchievement)
                                    )
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("share_achievement_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Share,
                                    contentDescription = strings.shareAchievement,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(strings.shareAchievement)
                            }
                        }

                        LinearProgressIndicator(
                            progress = { stats.levelProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f)
                        )
                    }
                }
            }
        }

        // KPI Summary Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatKpiCard(
                    label = strings.totalAthkarLabel,
                    value = stats.totalAthkar.toString(),
                    icon = Icons.Rounded.AutoAwesome,
                    modifier = Modifier.weight(1f)
                )
                StatKpiCard(
                    label = strings.totalTasbihLabel,
                    value = stats.totalTasbih.toString(),
                    icon = Icons.Rounded.Star,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatKpiCard(
                    label = strings.currentStreakLabel,
                    value = "${stats.currentStreak} ${strings.dayStreakUnit}",
                    icon = Icons.Rounded.LocalFireDepartment,
                    modifier = Modifier.weight(1f)
                )
                StatKpiCard(
                    label = strings.bestStreakLabel,
                    value = "${stats.bestStreak} ${strings.dayStreakUnit}",
                    icon = Icons.Rounded.MilitaryTech,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Periodic Activity Chart (Weekly / Monthly)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = !showMonthly,
                            onClick = { showMonthly = false },
                            label = { Text(strings.weeklyTab) },
                            modifier = Modifier.testTag("chart_weekly_chip")
                        )
                        FilterChip(
                            selected = showMonthly,
                            onClick = { showMonthly = true },
                            label = { Text(strings.monthlyTab) },
                            modifier = Modifier.testTag("chart_monthly_chip")
                        )
                    }

                    ActivityBarChart(
                        points = if (showMonthly) stats.monthlyPoints else stats.weeklyPoints
                    )
                }
            }
        }

        // Achievement Badges Section
        item {
            Text(
                text = strings.achievementsTitle,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(stats.achievements, key = { it.id }) { badge ->
            AchievementBadgeCard(
                badge = badge,
                language = uiState.language,
                strings = strings
            )
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun StatKpiCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AchievementBadgeCard(
    badge: AchievementBadge,
    language: AppLanguage,
    strings: AppStrings
) {
    val icon = when (badge.iconKey) {
        "streak" -> Icons.Rounded.LocalFireDepartment
        "sun_moon" -> Icons.Rounded.WbSunny
        "crown" -> Icons.Rounded.EmojiEvents
        "heart" -> Icons.Rounded.Favorite
        "tasbih" -> Icons.Rounded.Star
        else -> Icons.Rounded.AutoAwesome
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        border = if (badge.unlocked) BorderStroke(1.5.dp, StreakAmber.copy(alpha = 0.65f)) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (badge.unlocked) {
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        if (badge.unlocked) StreakAmber.copy(alpha = 0.22f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (badge.unlocked) icon else Icons.Rounded.Lock,
                    contentDescription = null,
                    tint = if (badge.unlocked) StreakAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = badge.localizedTitle(language),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (badge.unlocked) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = if (badge.unlocked) strings.unlockedLabel else strings.lockedLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (badge.unlocked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Text(
                    text = badge.localizedDesc(language),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LinearProgressIndicator(
                    progress = { badge.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (badge.unlocked) StreakAmber else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
