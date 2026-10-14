package com.example.codemind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
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
import com.example.codemind.data.model.MlTaskType
import com.example.codemind.data.samples.BundledDatasets
import com.example.codemind.ui.components.ConfusionMatrixView
import com.example.codemind.ui.components.FeatureImportanceChartView
import com.example.codemind.ui.components.MetricCard
import com.example.codemind.ui.components.SimpleScatterPlotView
import com.example.codemind.ui.navigation.Screen
import com.example.codemind.viewmodel.CodeMindViewModel
import com.example.ui.theme.*

@Composable
fun MlLabScreen(
    viewModel: CodeMindViewModel,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val dataset by viewModel.selectedDataset.collectAsStateWithLifecycle()
    val algorithm by viewModel.selectedAlgorithm.collectAsStateWithLifecycle()
    val modelResult by viewModel.mlModelResult.collectAsStateWithLifecycle()
    val isTraining by viewModel.isTrainingMl.collectAsStateWithLifecycle()
    val predictionInputs by viewModel.predictionInputs.collectAsStateWithLifecycle()
    val predictionOutput by viewModel.predictionOutput.collectAsStateWithLifecycle()

    var scriptCopied by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MACHINE LEARNING LAB",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Real tabular training, evaluation & scikit-learn export",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Button(
                    onClick = { viewModel.trainSelectedMlModel() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isTraining,
                    modifier = Modifier.testTag("train_model_button")
                ) {
                    if (isTraining) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Train Model", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // Dataset Selector Chips
        item {
            Column {
                Text(
                    text = "SELECT DATASET (CSV):",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BundledDatasets.allDatasets.forEach { ds ->
                        val isSelected = ds.id == dataset.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectMlDataset(ds) },
                            label = { Text(ds.name, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan,
                                selectedLabelColor = Color.Black,
                                containerColor = DarkSurfaceElevated,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }
            }
        }

        // Dataset Overview & Missing Values Detection
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dataset_overview_card"),
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
                            text = "${dataset.name} (${dataset.filename})",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = NeonIndigo.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = dataset.taskType.name,
                                color = NeonIndigoLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dataset.description,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MlStatBadge("Rows", "${dataset.rowCount}", Modifier.weight(1f))
                        MlStatBadge("Features", "${dataset.columnCount - 1}", Modifier.weight(1f))
                        MlStatBadge("Missing Values", "${dataset.totalMissingValues} (0%)", Modifier.weight(1f), StatusSuccess)
                        MlStatBadge("Target", dataset.targetColumn, Modifier.weight(1.2f), NeonCyanLight)
                    }
                }
            }
        }

        // Exploratory Data Analysis (EDA) Charts
        item {
            val scatterPoints = remember(dataset) {
                if (dataset.taskType == MlTaskType.CLASSIFICATION) {
                    dataset.rows.mapNotNull { row ->
                        val x = row.getOrNull(0)?.toDoubleOrNull()
                        val y = row.getOrNull(2)?.toDoubleOrNull()
                        if (x != null && y != null) x to y else null
                    }
                } else {
                    dataset.rows.mapNotNull { row ->
                        val x = row.getOrNull(0)?.toDoubleOrNull()
                        val y = row.getOrNull(row.size - 1)?.toDoubleOrNull()
                        if (x != null && y != null) x to y else null
                    }
                }
            }

            SimpleScatterPlotView(
                dataPoints = scatterPoints,
                xLabel = dataset.headers.firstOrNull() ?: "X",
                yLabel = dataset.headers.getOrNull(2) ?: "Y"
            )
        }

        // Algorithm Selector
        item {
            Column {
                Text(
                    text = "SELECT LEARNING ALGORITHM:",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                val algorithms = when (dataset.taskType) {
                    MlTaskType.CLASSIFICATION -> listOf("Logistic Regression", "K-Nearest Neighbors (KNN)")
                    MlTaskType.REGRESSION -> listOf("Linear Regression (OLS)", "Ridge Regularization")
                    MlTaskType.CLUSTERING -> listOf("K-Means Clustering", "MiniBatch K-Means")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    algorithms.forEach { algo ->
                        val isSelected = algo == algorithm
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectMlAlgorithm(algo) },
                            label = { Text(algo, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonPurple,
                                selectedLabelColor = Color.White,
                                containerColor = DarkSurfaceElevated,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }
            }
        }

        // Training Results & Evaluation Metrics
        modelResult?.let { res ->
            item {
                Text(
                    text = "MODEL EVALUATION METRICS",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (res.evaluation.accuracy != null) {
                        MetricCard(
                            title = "Accuracy",
                            value = "${String.format("%.1f", res.evaluation.accuracy * 100)}%",
                            subtitle = "F1: ${String.format("%.3f", res.evaluation.f1Score ?: 0.0)}",
                            icon = Icons.Default.CheckCircle,
                            accentColor = StatusSuccess,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Precision / Recall",
                            value = "${String.format("%.2f", res.evaluation.precision ?: 0.0)}",
                            subtitle = "Recall: ${String.format("%.2f", res.evaluation.recall ?: 0.0)}",
                            icon = Icons.Default.TrendingUp,
                            accentColor = NeonCyanLight,
                            modifier = Modifier.weight(1f)
                        )
                    } else if (res.evaluation.r2Score != null) {
                        MetricCard(
                            title = "R² Coefficient",
                            value = String.format("%.3f", res.evaluation.r2Score),
                            subtitle = "Variance explained",
                            icon = Icons.Default.ShowChart,
                            accentColor = NeonCyanLight,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "MSE Loss",
                            value = String.format("%.2f", res.evaluation.mse ?: 0.0),
                            subtitle = "Mean Squared Error",
                            icon = Icons.Default.Timeline,
                            accentColor = NeonIndigoLight,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        MetricCard(
                            title = "K-Means Inertia",
                            value = String.format("%.1f", res.evaluation.inertia ?: 0.0),
                            subtitle = "Elbow compact score",
                            icon = Icons.Default.BubbleChart,
                            accentColor = NeonPurpleLight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Confusion Matrix if classification
            res.evaluation.confusionMatrix?.let { cm ->
                item {
                    ConfusionMatrixView(data = cm)
                }
            }

            // Feature Importance
            if (res.featureWeights.isNotEmpty()) {
                item {
                    FeatureImportanceChartView(weights = res.featureWeights)
                }
            }

            // Interactive Live Prediction Sandbox
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("interactive_prediction_card"),
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
                                text = "INTERACTIVE MODEL INFERENCE",
                                color = NeonCyanLight,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { viewModel.executePrediction() },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("run_inference_button")
                            ) {
                                Text("Predict", color = Color.Black, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Adjust feature inputs below and click Predict to run live inference against the trained model:",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Sliders for numeric features
                        predictionInputs.forEach { (feat, currentVal) ->
                            val col = dataset.columns.find { it.name == feat }
                            val minVal = (col?.min ?: 0.0).toFloat()
                            val maxVal = (col?.max ?: 10.0).toFloat()

                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = feat, color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    Text(text = String.format("%.2f", currentVal), color = NeonIndigoLight, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                }
                                Slider(
                                    value = currentVal.toFloat().coerceIn(minVal, maxVal),
                                    onValueChange = { viewModel.updatePredictionInput(feat, it.toDouble()) },
                                    valueRange = minVal..maxVal,
                                    colors = SliderDefaults.colors(
                                        thumbColor = NeonCyan,
                                        activeTrackColor = NeonIndigo,
                                        inactiveTrackColor = DarkSurfaceContainer
                                    )
                                )
                            }
                        }

                        predictionOutput?.let { out ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StatusSuccessBg,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Stars, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "PREDICTED OUTPUT:",
                                            color = StatusSuccess,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = out,
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Exportable Scikit-Learn Python Script
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "EXPORT TRAINING SCRIPT (.PY)",
                                color = NeonIndigoLight,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(res.generatedPythonScript))
                                    scriptCopied = true
                                },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (scriptCopied) "Copied!" else "Copy Python Script", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DarkBackground,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = res.generatedPythonScript,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MlStatBadge(label: String, value: String, modifier: Modifier = Modifier, color: Color = TextPrimary) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Text(text = label, color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }
    }
}
