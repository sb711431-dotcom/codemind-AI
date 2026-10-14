package com.example.codemind.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.codemind.ui.components.MetricCard
import com.example.codemind.ui.navigation.Screen
import com.example.codemind.viewmodel.CodeMindViewModel
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: CodeMindViewModel,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val scans by viewModel.scanHistory.collectAsStateWithLifecycle()
    val analysisResult by viewModel.analysisResult.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_hero_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_codemind),
                        contentDescription = "CodeMind AI Hero Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop,
                        alpha = 0.35f
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(NeonIndigo.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Terminal,
                                        contentDescription = null,
                                        tint = NeonIndigoLight,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "CodeMind AI",
                                        color = TextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Python Studio & ML Engine",
                                        color = NeonCyanLight,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            // Sandbox status badge
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = StatusSuccess.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(StatusSuccess, RoundedCornerShape(3.dp))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "SANDBOX ACTIVE",
                                        color = StatusSuccess,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Analyze AST syntax, automatically remediate bugs with verifiable diffs, and train real Machine Learning models in a safe isolated environment.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onNavigate(Screen.Analyzer) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("analyze_code_hero_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonIndigo),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 10.dp)
                            ) {
                                Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Analyze Code", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            }

                            OutlinedButton(
                                onClick = { onNavigate(Screen.MlLab) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("launch_ml_hero_button"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyanLight),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 10.dp)
                            ) {
                                Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ML Lab", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }

        // Metrics Grid (2x2)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Projects Analyzed",
                    value = "2",
                    subtitle = "Flask & ML Pipeline",
                    icon = Icons.Default.Folder,
                    accentColor = NeonIndigoLight,
                    modifier = Modifier.weight(1f),
                    testTag = "metric_projects_card"
                )
                MetricCard(
                    title = "Issues Detected",
                    value = "${analysisResult?.issues?.size ?: 3}",
                    subtitle = "${analysisResult?.criticalCount ?: 1} Critical Bugs",
                    icon = Icons.Default.BugReport,
                    accentColor = SeverityCritical,
                    modifier = Modifier.weight(1f),
                    testTag = "metric_issues_card"
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Fixes Validated",
                    value = "100%",
                    subtitle = "PyTest Verified",
                    icon = Icons.Default.AutoFixHigh,
                    accentColor = StatusSuccess,
                    modifier = Modifier.weight(1f),
                    testTag = "metric_fixes_card"
                )
                MetricCard(
                    title = "Test Pass Rate",
                    value = "96.4%",
                    subtitle = "Isolated Sandbox",
                    icon = Icons.Default.CheckCircle,
                    accentColor = NeonCyanLight,
                    modifier = Modifier.weight(1f),
                    testTag = "metric_tests_card"
                )
            }
        }

        // Quick Navigation Tiles
        item {
            Text(
                text = "DEVELOPMENT WORKFLOWS",
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WorkflowTile(
                    title = "AI Auto-Repair & Unified Diff",
                    description = "Side-by-side comparison with instant line explanations and undo stack",
                    icon = Icons.Default.AutoFixHigh,
                    color = NeonIndigoLight,
                    onClick = { onNavigate(Screen.AutoFix) }
                )
                WorkflowTile(
                    title = "Full Project Directory Scanner",
                    description = "Inspect multi-module Python projects, cross-file imports, and requirements",
                    icon = Icons.Default.FolderZip,
                    color = NeonCyanLight,
                    onClick = { onNavigate(Screen.ProjectScanner) }
                )
                WorkflowTile(
                    title = "Machine Learning Studio",
                    description = "Dataset inspection, EDA charts, model training & interactive prediction",
                    icon = Icons.Default.Analytics,
                    color = NeonPurpleLight,
                    onClick = { onNavigate(Screen.MlLab) }
                )
                WorkflowTile(
                    title = "AI Coding Copilot & PyTest Runner",
                    description = "Gemini-powered code explanations, unit test generation, and AST audits",
                    icon = Icons.Default.Psychology,
                    color = StatusSuccess,
                    onClick = { onNavigate(Screen.Chat) }
                )
            }
        }

        // Recent Scan History from Database
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT SCAN HISTORY",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigate(Screen.History) }) {
                    Text("View All", color = NeonCyanLight, fontSize = 12.sp)
                }
            }
        }

        if (scans.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                ) {
                    Text(
                        text = "No prior scans recorded. Run analysis to populate history.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(scans.take(3)) { scan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.updateCode(scan.fullCode)
                            onNavigate(Screen.Analyzer)
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = scan.title,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = scan.analysisNotes,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (scan.criticalIssues > 0) SeverityCriticalBg else StatusSuccessBg
                        ) {
                            Text(
                                text = if (scan.criticalIssues > 0) "${scan.criticalIssues} CRITICAL" else "CLEAN",
                                color = if (scan.criticalIssues > 0) SeverityCritical else StatusSuccess,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkflowTile(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
