package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AddAlarm
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.OfflinePin
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.data.model.AppLanguage
import com.example.data.model.DhikrCategory
import com.example.data.model.FontScalePreference
import com.example.data.model.ThemePreference
import com.example.localization.AppStrings
import com.example.ui.viewmodel.AzkariUiState
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsAndRemindersScreen(
    uiState: AzkariUiState,
    strings: AppStrings,
    onLanguageChange: (AppLanguage) -> Unit,
    onThemeChange: (ThemePreference) -> Unit,
    onFontScaleChange: (FontScalePreference) -> Unit,
    onHapticChange: (Boolean) -> Unit,
    onAddReminder: (String, String, Int, Int) -> Unit,
    onToggleReminder: (String, Boolean) -> Unit,
    onDeleteReminder: (String) -> Unit,
    onBuildBackupJson: () -> String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddReminderDialog by remember { mutableStateOf(false) }

    var hasNotifPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasNotifPermission = granted
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = strings.settingsTitle,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // 1. Custom Reminders Section
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.NotificationsActive,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = strings.remindersSection,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        FilledTonalButton(
                            onClick = { showAddReminderDialog = true },
                            modifier = Modifier.testTag("add_reminder_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddAlarm,
                                contentDescription = strings.addReminder,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(strings.addReminder)
                        }
                    }

                    if (!hasNotifPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = strings.notificationPermissionNotice,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Button(
                                    onClick = {
                                        notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    },
                                    modifier = Modifier.testTag("grant_notifications_button")
                                ) {
                                    Text(strings.grantPermission)
                                }
                            }
                        }
                    }

                    if (uiState.reminders.isEmpty()) {
                        Text(
                            text = strings.emptyRemindersTip,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        uiState.reminders.forEachIndexed { index, reminder ->
                            if (index > 0) HorizontalDivider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = reminder.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = String.format(
                                            Locale.US,
                                            "%02d:%02d",
                                            reminder.hour.toInt(),
                                            reminder.minute.toInt()
                                        ),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Switch(
                                        checked = reminder.enabled,
                                        onCheckedChange = { onToggleReminder(reminder.id, it) }
                                    )
                                    IconButton(onClick = { onDeleteReminder(reminder.id) }) {
                                        Icon(
                                            imageVector = Icons.Rounded.DeleteOutline,
                                            contentDescription = strings.delete,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Appearance, Language, Font Size & Haptics
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = strings.appearanceSection,
                        style = MaterialTheme.typography.titleMedium
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            ThemePreference.SYSTEM to strings.themeSystem,
                            ThemePreference.LIGHT to strings.themeLight,
                            ThemePreference.DARK to strings.themeDark
                        ).forEach { (pref, label) ->
                            FilterChip(
                                selected = uiState.themePreference == pref,
                                onClick = { onThemeChange(pref) },
                                label = { Text(label) },
                                modifier = Modifier.testTag("theme_chip_${pref.code}")
                            )
                        }
                    }

                    HorizontalDivider()

                    Text(
                        text = strings.languageSection,
                        style = MaterialTheme.typography.titleMedium
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppLanguage.entries.forEach { lang ->
                            FilterChip(
                                selected = uiState.language == lang,
                                onClick = { onLanguageChange(lang) },
                                label = { Text(lang.nativeLabel) },
                                modifier = Modifier.testTag("lang_chip_${lang.code}")
                            )
                        }
                    }

                    HorizontalDivider()

                    Text(
                        text = strings.fontSizeSection,
                        style = MaterialTheme.typography.titleMedium
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            FontScalePreference.SMALL to strings.fontSmall,
                            FontScalePreference.MEDIUM to strings.fontMedium,
                            FontScalePreference.LARGE to strings.fontLarge,
                            FontScalePreference.XLARGE to strings.fontXLarge
                        ).forEach { (scale, label) ->
                            FilterChip(
                                selected = uiState.fontScale == scale,
                                onClick = { onFontScaleChange(scale) },
                                label = { Text(label) }
                            )
                        }
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.hapticFeedbackLabel,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = uiState.settings.hapticEnabled,
                            onCheckedChange = onHapticChange
                        )
                    }
                }
            }
        }

        // 3. Offline Readiness & Settings Backup
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.OfflinePin,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = strings.exportBackup,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Text(
                        text = strings.offlineReadyNote,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = {
                            val json = onBuildBackupJson()
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Azkari Settings Backup")
                                putExtra(Intent.EXTRA_TEXT, json)
                            }
                            context.startActivity(
                                Intent.createChooser(intent, strings.exportBackup)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("export_backup_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Backup,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(strings.exportBackup)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }

    if (showAddReminderDialog) {
        AddReminderDialog(
            strings = strings,
            onDismiss = { showAddReminderDialog = false },
            onSave = { title, catId, hour, minute ->
                onAddReminder(title, catId, hour, minute)
                showAddReminderDialog = false
            }
        )
    }
}

@Composable
private fun AddReminderDialog(
    strings: AppStrings,
    onDismiss: () -> Unit,
    onSave: (String, String, Int, Int) -> Unit
) {
    var title by remember { mutableStateOf(strings.categoryNames[DhikrCategory.MORNING] ?: "Morning Athkar") }
    var selectedCategory by remember { mutableStateOf(DhikrCategory.MORNING) }
    var hour by remember { mutableIntStateOf(6) }
    var minute by remember { mutableIntStateOf(30) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.addReminder) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= 100) title = it },
                    label = { Text(strings.reminderTitleLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        DhikrCategory.MORNING to (6 to 30),
                        DhikrCategory.EVENING to (17 to 30),
                        DhikrCategory.SLEEP to (22 to 0)
                    ).forEach { (cat, defaultTime) ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = {
                                selectedCategory = cat
                                title = strings.categoryNames[cat] ?: cat.id
                                hour = defaultTime.first
                                minute = defaultTime.second
                            },
                            label = {
                                Text(
                                    text = (strings.categoryNames[cat] ?: cat.id).take(12),
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }

                Text(
                    text = strings.reminderTimeLabel,
                    style = MaterialTheme.typography.labelLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TextButton(onClick = { hour = (hour + 1) % 24 }) { Text("+") }
                        Text(
                            text = String.format(Locale.US, "%02d", hour),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { hour = (hour + 23) % 24 }) { Text("-") }
                    }
                    Text(
                        text = ":",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TextButton(onClick = { minute = (minute + 5) % 60 }) { Text("+") }
                        Text(
                            text = String.format(Locale.US, "%02d", minute),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { minute = (minute + 55) % 60 }) { Text("-") }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, selectedCategory.id, hour, minute) },
                enabled = title.isNotBlank()
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
