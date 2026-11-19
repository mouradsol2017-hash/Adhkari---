package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.DhikrCategory
import com.example.data.model.ThemePreference
import com.example.localization.stringsFor
import com.example.ui.screens.DhikrReaderScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsAndRemindersScreen
import com.example.ui.screens.StatsAndRewardsScreen
import com.example.ui.screens.TasbihScreen
import com.example.ui.theme.AzkariTheme
import com.example.ui.viewmodel.AzkariViewModel

private enum class MainTab(
    val routeTag: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("nav_home", Icons.Rounded.AutoAwesome, Icons.Outlined.AutoAwesome),
    TASBIH("nav_tasbih", Icons.Rounded.TouchApp, Icons.Outlined.TouchApp),
    STATS("nav_stats", Icons.Rounded.EmojiEvents, Icons.Outlined.EmojiEvents),
    SETTINGS("nav_settings", Icons.Rounded.Settings, Icons.Outlined.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AzkariApp()
        }
    }
}

@Composable
private fun AzkariApp() {
    val context = LocalContext.current
    val viewModel: AzkariViewModel = viewModel(factory = AzkariViewModel.Factory(context))
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val strings = stringsFor(uiState.language)

    val systemDark = isSystemInDarkTheme()
    val isDarkTheme = when (uiState.themePreference) {
        ThemePreference.SYSTEM -> systemDark
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
    }

    var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }
    var activeCategory by rememberSaveable { mutableStateOf<DhikrCategory?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides if (uiState.language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        AzkariTheme(darkTheme = isDarkTheme) {
            if (activeCategory != null) {
                DhikrReaderScreen(
                    category = activeCategory!!,
                    uiState = uiState,
                    strings = strings,
                    onBack = { activeCategory = null },
                    onTapDhikr = { viewModel.incrementDhikrCount(it) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onDeleteCustomDhikr = { viewModel.deleteCustomDhikr(it) },
                    onCycleFontScale = { viewModel.setFontScale(it) },
                    onResetCategory = { viewModel.resetCategorySession(it) }
                )
            } else {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val useNavRail = maxWidth >= 600.dp

                    val tabLabel: (MainTab) -> String = { tab ->
                        when (tab) {
                            MainTab.HOME -> strings.navHome
                            MainTab.TASBIH -> strings.navTasbih
                            MainTab.STATS -> strings.navStats
                            MainTab.SETTINGS -> strings.navSettings
                        }
                    }

                    Scaffold(
                        contentWindowInsets = WindowInsets.safeDrawing,
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        bottomBar = {
                            if (!useNavRail) {
                                NavigationBar {
                                    MainTab.entries.forEach { tab ->
                                        val isSelected = selectedTab == tab
                                        val label = tabLabel(tab)
                                        NavigationBarItem(
                                            selected = isSelected,
                                            onClick = { selectedTab = tab },
                                            icon = {
                                                Icon(
                                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                                    contentDescription = label
                                                )
                                            },
                                            label = { Text(label) },
                                            modifier = Modifier.testTag(tab.routeTag)
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (useNavRail) {
                                NavigationRail {
                                    MainTab.entries.forEach { tab ->
                                        val isSelected = selectedTab == tab
                                        val label = tabLabel(tab)
                                        NavigationRailItem(
                                            selected = isSelected,
                                            onClick = { selectedTab = tab },
                                            icon = {
                                                Icon(
                                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                                    contentDescription = label
                                                )
                                            },
                                            label = { Text(label) },
                                            modifier = Modifier.testTag(tab.routeTag)
                                        )
                                    }
                                }
                            }

                            when (selectedTab) {
                                MainTab.HOME -> HomeScreen(
                                    uiState = uiState,
                                    strings = strings,
                                    isDarkTheme = isDarkTheme,
                                    onToggleDarkMode = { viewModel.setThemePreference(it) },
                                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                                    onCategoryClick = { activeCategory = it },
                                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                                    onAddCustomDhikr = { title, text, translation, count ->
                                        viewModel.addCustomDhikr(title, text, translation, count)
                                    }
                                )
                                MainTab.TASBIH -> TasbihScreen(
                                    uiState = uiState,
                                    strings = strings,
                                    onTapTasbih = { viewModel.incrementTasbih() },
                                    onResetCurrent = { viewModel.resetTasbihCurrent() },
                                    onSelectPhrase = { id, target -> viewModel.selectTasbihPhrase(id, target) },
                                    onSelectTarget = { viewModel.setTasbihTarget(it) }
                                )
                                MainTab.STATS -> StatsAndRewardsScreen(
                                    uiState = uiState,
                                    strings = strings
                                )
                                MainTab.SETTINGS -> SettingsAndRemindersScreen(
                                    uiState = uiState,
                                    strings = strings,
                                    onLanguageChange = { viewModel.setLanguage(it) },
                                    onThemeChange = { viewModel.setThemePreference(it) },
                                    onFontScaleChange = { viewModel.setFontScale(it) },
                                    onHapticChange = { viewModel.setHapticEnabled(it) },
                                    onAddReminder = { title, catId, hour, minute ->
                                        viewModel.addReminder(title, catId, hour, minute)
                                    },
                                    onToggleReminder = { id, enabled ->
                                        viewModel.toggleReminder(id, enabled)
                                    },
                                    onDeleteReminder = { viewModel.deleteReminder(it) },
                                    onBuildBackupJson = { viewModel.buildSettingsBackupJson() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
