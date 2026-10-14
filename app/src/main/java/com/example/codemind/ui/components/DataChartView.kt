package com.example.codemind.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun FeatureImportanceChartView(
    weights: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("feature_importance_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = "FEATURE IMPORTANCE COEFFICIENTS",
                color = NeonIndigoLight,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            val maxWeight = weights.values.maxOrNull()?.coerceAtLeast(0.01) ?: 1.0

            weights.forEach { (feature, weight) ->
                val ratio = (weight / maxWeight).toFloat().coerceIn(0.05f, 1f)

                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = feature,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = String.format("%.3f", weight),
                            color = NeonCyanLight,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(DarkSurfaceContainer, RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(ratio)
                                .fillMaxHeight()
                                .background(NeonIndigo, RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleScatterPlotView(
    dataPoints: List<Pair<Double, Double>>,
    xLabel: String,
    yLabel: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("scatter_plot_card"),
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
                    text = "FEATURE CORRELATION SCATTER ($xLabel vs $yLabel)",
                    color = NeonCyanLight,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(DarkBackground, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                if (dataPoints.isEmpty()) return@Canvas

                val minX = dataPoints.minOf { it.first }
                val maxX = dataPoints.maxOf { it.first }.coerceAtLeast(minX + 0.001)
                val minY = dataPoints.minOf { it.second }
                val maxY = dataPoints.maxOf { it.second }.coerceAtLeast(minY + 0.001)

                val w = size.width
                val h = size.height

                // Draw grid lines
                drawLine(
                    color = DarkBorder,
                    start = Offset(0f, h * 0.5f),
                    end = Offset(w, h * 0.5f),
                    strokeWidth = 1f
                )

                dataPoints.forEach { pt ->
                    val normX = ((pt.first - minX) / (maxX - minX)).toFloat().coerceIn(0f, 1f)
                    val normY = 1f - ((pt.second - minY) / (maxY - minY)).toFloat().coerceIn(0f, 1f)

                    drawCircle(
                        color = NeonCyan,
                        radius = 4.dp.toPx(),
                        center = Offset(normX * (w - 16.dp.toPx()) + 8.dp.toPx(), normY * (h - 16.dp.toPx()) + 8.dp.toPx())
                    )
                }
            }
        }
    }
}
