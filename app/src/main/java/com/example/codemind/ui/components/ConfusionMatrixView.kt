package com.example.codemind.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codemind.data.model.ConfusionMatrixData
import com.example.ui.theme.*

@Composable
fun ConfusionMatrixView(
    data: ConfusionMatrixData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("confusion_matrix_card"),
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
                    text = "CONFUSION MATRIX (EVALUATION)",
                    color = NeonCyanLight,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Rows: True | Cols: Pred",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Column Header Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Empty corner spacer
                Box(modifier = Modifier.width(72.dp))

                data.labels.forEach { label ->
                    Text(
                        text = label.take(6),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Rows
            val maxVal = data.matrix.flatten().maxOrNull()?.coerceAtLeast(1) ?: 1

            data.labels.forEachIndexed { rIdx, rowLabel ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // True Label on left
                    Text(
                        text = rowLabel.take(6),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .width(68.dp)
                            .padding(end = 6.dp)
                    )

                    // Cells
                    data.matrix.getOrNull(rIdx)?.forEachIndexed { cIdx, count ->
                        val isDiagonal = rIdx == cIdx
                        val intensity = (count.toFloat() / maxVal).coerceIn(0.1f, 1f)
                        val cellBg = if (isDiagonal) {
                            StatusSuccess.copy(alpha = 0.2f + 0.6f * intensity)
                        } else if (count > 0) {
                            SeverityCritical.copy(alpha = 0.15f + 0.4f * intensity)
                        } else {
                            DarkSurfaceContainer
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .padding(horizontal = 2.dp)
                                .background(cellBg, RoundedCornerShape(6.dp))
                                .border(1.dp, if (isDiagonal) StatusSuccess.copy(alpha = 0.4f) else DarkBorder, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = count.toString(),
                                color = if (isDiagonal) Color.White else TextPrimary,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isDiagonal) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
