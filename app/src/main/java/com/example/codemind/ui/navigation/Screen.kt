package com.example.codemind.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.material.icons.automirrored.filled.*

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    data object Analyzer : Screen("analyzer", "Analyzer", Icons.Default.Code)
    data object AutoFix : Screen("auto_fix", "AI Auto Fix", Icons.Default.AutoFixHigh)
    data object ProjectScanner : Screen("project_scanner", "Projects", Icons.Default.FolderZip)
    data object MlLab : Screen("ml_lab", "ML Lab", Icons.Default.Science)
    data object Chat : Screen("chat", "AI Chat", Icons.AutoMirrored.Filled.Chat)
    data object TestRunner : Screen("test_runner", "Tests", Icons.Default.CheckCircle)
    data object History : Screen("history", "History", Icons.Default.History)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)

    companion object {
        val primaryNavItems = listOf(
            Dashboard,
            Analyzer,
            AutoFix,
            MlLab,
            ProjectScanner,
            Chat
        )
        val allScreens = listOf(
            Dashboard,
            Analyzer,
            AutoFix,
            ProjectScanner,
            MlLab,
            Chat,
            TestRunner,
            History,
            Settings
        )
    }
}
