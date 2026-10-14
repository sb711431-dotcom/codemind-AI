package com.example.codemind.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codemind.ui.screens.*
import com.example.codemind.viewmodel.CodeMindViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    viewModel: CodeMindViewModel,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }

    // Handle back button: return to Dashboard from any other screen
    BackHandler(enabled = currentScreen != Screen.Dashboard) {
        currentScreen = Screen.Dashboard
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(NeonCyan, shape = androidx.compose.foundation.shape.CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentScreen.title.uppercase(),
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { currentScreen = Screen.TestRunner },
                        modifier = Modifier.testTag("nav_tests_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Test Runner",
                            tint = if (currentScreen == Screen.TestRunner) NeonCyan else TextSecondary
                        )
                    }
                    IconButton(
                        onClick = { currentScreen = Screen.History },
                        modifier = Modifier.testTag("nav_history_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Scan History",
                            tint = if (currentScreen == Screen.History) NeonCyan else TextSecondary
                        )
                    }
                    IconButton(
                        onClick = { currentScreen = Screen.Settings },
                        modifier = Modifier.testTag("nav_settings_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (currentScreen == Screen.Settings) NeonCyan else TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 4.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_navigation_bar")
            ) {
                Screen.primaryNavItems.forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            indicatorColor = DarkSurfaceContainer,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.Dashboard -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigate = { currentScreen = it }
                )
                Screen.Analyzer -> AnalyzerScreen(
                    viewModel = viewModel,
                    onNavigate = { currentScreen = it }
                )
                Screen.AutoFix -> AutoFixScreen(
                    viewModel = viewModel,
                    onNavigate = { currentScreen = it }
                )
                Screen.ProjectScanner -> ProjectScannerScreen(
                    viewModel = viewModel,
                    onNavigate = { currentScreen = it }
                )
                Screen.MlLab -> MlLabScreen(
                    viewModel = viewModel,
                    onNavigate = { currentScreen = it }
                )
                Screen.Chat -> ChatScreen(
                    viewModel = viewModel,
                    onNavigate = { currentScreen = it }
                )
                Screen.TestRunner -> TestRunnerScreen(
                    viewModel = viewModel,
                    onNavigate = { currentScreen = it }
                )
                Screen.History -> HistoryScreen(
                    viewModel = viewModel,
                    onNavigate = { currentScreen = it }
                )
                Screen.Settings -> SettingsScreen(
                    viewModel = viewModel,
                    onNavigate = { currentScreen = it }
                )
            }
        }
    }
}
