package com.example.codemind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codemind.data.model.IssueSeverity
import com.example.codemind.data.model.ProjectFile
import com.example.codemind.ui.components.CodeEditorView
import com.example.codemind.ui.navigation.Screen
import com.example.codemind.viewmodel.CodeMindViewModel
import com.example.ui.theme.*

@Composable
fun ProjectScannerScreen(
    viewModel: CodeMindViewModel,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val projectName by viewModel.selectedProjectName.collectAsStateWithLifecycle()
    val projectFiles by viewModel.currentProjectFiles.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanningProject.collectAsStateWithLifecycle()
    val progress by viewModel.projectScanProgress.collectAsStateWithLifecycle()
    val scanResult by viewModel.projectScanResult.collectAsStateWithLifecycle()

    var selectedFile by remember { mutableStateOf<ProjectFile?>(null) }
    var reportCopied by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FULL PROJECT SCANNER",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Multi-file dependency graph & architecture audit",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Button(
                    onClick = { viewModel.runProjectScan() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonIndigo),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isScanning,
                    modifier = Modifier.testTag("run_project_scan_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isScanning) "Scanning..." else "Scan Project", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }

        // Project Switcher
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = projectName.contains("Flask"),
                    onClick = { viewModel.selectProject(isPipeline = false) },
                    label = { Text("Flask User API", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonIndigo,
                        selectedLabelColor = Color.White,
                        containerColor = DarkSurfaceElevated,
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = projectName.contains("Pipeline"),
                    onClick = { viewModel.selectProject(isPipeline = true) },
                    label = { Text("ML ETL Pipeline", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonIndigo,
                        selectedLabelColor = Color.White,
                        containerColor = DarkSurfaceElevated,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        // Progress Bar during scan
        if (isScanning) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "PARSING AST & MODULE DEPENDENCIES...",
                                color = NeonCyanLight,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = NeonCyan,
                            trackColor = DarkBackground
                        )
                    }
                }
            }
        }

        // Scan Summary & Export Button
        scanResult?.let { res ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("project_scan_summary_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PROJECT AUDIT REPORT",
                                color = StatusSuccess,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )

                            OutlinedButton(
                                onClick = {
                                    val report = """
# CodeMind AI Project Scan Report
Project: ${res.projectName}
Total Files: ${res.totalFiles} (${res.pythonFilesCount} Python modules)
Lines of Code: ${res.totalLinesOfCode}
Total Issues Detected: ${res.issues.size}
- Critical Errors: ${res.criticalCount}
- Security Warnings: ${res.securityCount}
- Warnings: ${res.warningCount}

## Issues Breakdown:
${res.issues.joinToString("\n") { "• [${it.severity}] ${it.file}:${it.line} - ${it.title}: ${it.message}" }}
""".trimIndent()
                                    clipboardManager.setText(AnnotatedString(report))
                                    reportCopied = true
                                },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (reportCopied) "Copied!" else "Export Report", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PillStat("Files", "${res.totalFiles}", NeonIndigoLight, Modifier.weight(1f))
                            PillStat("LOC", "${res.totalLinesOfCode}", TextPrimary, Modifier.weight(1f))
                            PillStat("Critical", "${res.criticalCount}", SeverityCritical, Modifier.weight(1f))
                            PillStat("Security", "${res.securityCount}", SeveritySecurity, Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Project File Tree
        item {
            Text(
                text = "PROJECT REPOSITORY TREE (${projectFiles.size} FILES)",
                color = TextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        items(projectFiles) { file ->
            val isPy = file.name.endsWith(".py")
            val isReq = file.name.endsWith(".txt")
            val isSelected = selectedFile?.path == file.path

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedFile = file },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) DarkSurfaceContainer else DarkSurfaceElevated
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isPy) Icons.Default.Code else if (isReq) Icons.Default.Article else Icons.Default.Description,
                            contentDescription = null,
                            tint = if (isPy) NeonCyanLight else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = file.path,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${file.content.lines().size} lines",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isPy) {
                            TextButton(
                                onClick = {
                                    viewModel.updateCode(file.content)
                                    onNavigate(Screen.Analyzer)
                                },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Open in Editor", color = NeonIndigoLight, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }

        // File Content Preview if selected
        selectedFile?.let { file ->
            item {
                Column {
                    Text(
                        text = "PREVIEW: ${file.name}",
                        color = NeonCyanLight,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    CodeEditorView(
                        code = file.content,
                        onCodeChange = {},
                        readOnly = true,
                        filename = file.name
                    )
                }
            }
        }
    }
}

@Composable
private fun PillStat(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Text(text = label, color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }
    }
}
