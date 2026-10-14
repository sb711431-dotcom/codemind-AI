package com.example.codemind.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codemind.data.model.DiffLine
import com.example.codemind.data.model.DiffLineType
import com.example.ui.theme.*

@Composable
fun DiffViewer(
    diffLines: List<DiffLine>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("diff_viewer_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Diff Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceContainer)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "DIFF VIEW (BEFORE / AFTER)",
                    color = NeonIndigoLight,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val additions = diffLines.count { it.type == DiffLineType.ADDED }
                    val deletions = diffLines.count { it.type == DiffLineType.REMOVED }
                    Text(
                        text = "+$additions",
                        color = CodeDiffAddText,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "-$deletions",
                        color = CodeDiffRemoveText,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Diff Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp)
                    .background(DarkBackground)
                    .horizontalScroll(rememberScrollState())
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp)
            ) {
                diffLines.forEach { line ->
                    val (bgColor, textColor, prefix) = when (line.type) {
                        DiffLineType.ADDED -> Triple(CodeDiffAdd, CodeDiffAddText, "+")
                        DiffLineType.REMOVED -> Triple(CodeDiffRemove, CodeDiffRemoveText, "-")
                        DiffLineType.CONTEXT -> Triple(DarkBackground, TextSecondary, " ")
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bgColor)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Old Line No
                        Text(
                            text = line.oldLineNumber?.toString() ?: "",
                            color = CodeLineNumber,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.width(28.dp)
                        )
                        // New Line No
                        Text(
                            text = line.newLineNumber?.toString() ?: "",
                            color = CodeLineNumber,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.width(28.dp)
                        )
                        // Prefix
                        Text(
                            text = prefix,
                            color = textColor,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(16.dp)
                        )
                        // Code Text
                        Text(
                            text = line.content,
                            color = textColor,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
