package com.example.codemind.data.model

enum class IssueSeverity {
    CRITICAL,
    WARNING,
    OPTIMIZATION,
    SECURITY
}

data class CodeIssue(
    val id: String,
    val file: String = "main.py",
    val line: Int,
    val column: Int = 1,
    val severity: IssueSeverity,
    val ruleId: String,
    val title: String,
    val message: String,
    val explanation: String,
    val fixSuggestion: String,
    val codeSnippet: String = ""
)

enum class DiffLineType {
    CONTEXT,
    ADDED,
    REMOVED
}

data class DiffLine(
    val type: DiffLineType,
    val oldLineNumber: Int?,
    val newLineNumber: Int?,
    val content: String
)

data class AnalysisResult(
    val id: String = System.currentTimeMillis().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val code: String,
    val issues: List<CodeIssue>,
    val linesOfCode: Int,
    val functionsCount: Int,
    val complexityScore: Int,
    val hasSyntaxErrors: Boolean
) {
    val isClean: Boolean get() = issues.isEmpty()
    val criticalCount: Int get() = issues.count { it.severity == IssueSeverity.CRITICAL }
    val warningCount: Int get() = issues.count { it.severity == IssueSeverity.WARNING }
    val securityCount: Int get() = issues.count { it.severity == IssueSeverity.SECURITY }
    val optimizationCount: Int get() = issues.count { it.severity == IssueSeverity.OPTIMIZATION }
}

data class ValidationResult(
    val passed: Boolean,
    val totalTests: Int,
    val passedTests: Int,
    val failedTests: Int,
    val executionTimeMs: Long,
    val outputLog: String,
    val errorDetails: String? = null
)

enum class MlTaskType {
    CLASSIFICATION,
    REGRESSION,
    CLUSTERING
}

data class MlColumn(
    val name: String,
    val type: String, // "numeric", "categorical"
    val missingCount: Int,
    val mean: Double? = null,
    val min: Double? = null,
    val max: Double? = null,
    val uniqueValues: List<String> = emptyList()
)

data class MlDataset(
    val id: String,
    val name: String,
    val description: String,
    val filename: String,
    val taskType: MlTaskType,
    val headers: List<String>,
    val rows: List<List<String>>,
    val targetColumn: String,
    val columns: List<MlColumn>
) {
    val rowCount: Int get() = rows.size
    val columnCount: Int get() = headers.size
    val totalMissingValues: Int get() = columns.sumOf { it.missingCount }
}

data class ConfusionMatrixData(
    val labels: List<String>,
    val matrix: List<List<Int>>
)

data class MlEvaluation(
    val accuracy: Double? = null,
    val precision: Double? = null,
    val recall: Double? = null,
    val f1Score: Double? = null,
    val mse: Double? = null,
    val r2Score: Double? = null,
    val inertia: Double? = null,
    val confusionMatrix: ConfusionMatrixData? = null
)

data class MlModelResult(
    val algorithmName: String,
    val taskType: MlTaskType,
    val evaluation: MlEvaluation,
    val trainingTimeMs: Long,
    val trainSplitRatio: Float = 0.8f,
    val featureWeights: Map<String, Double> = emptyMap(),
    val samplePredictions: List<Pair<List<String>, String>> = emptyList(),
    val generatedPythonScript: String
)

enum class ChatSender {
    USER,
    ASSISTANT
}

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val sender: ChatSender,
    val text: String,
    val codeSnippet: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false
)

data class TestCase(
    val id: String,
    val name: String,
    val inputDescription: String,
    val expectedOutput: String,
    val actualOutput: String,
    val passed: Boolean,
    val executionDurationMs: Long,
    val stackTrace: String? = null
)

data class ProjectFile(
    val name: String,
    val path: String,
    val content: String,
    val isDirectory: Boolean = false,
    val issuesCount: Int = 0,
    val children: List<ProjectFile> = emptyList()
)

data class ProjectScanResult(
    val projectName: String,
    val scanTimestamp: Long = System.currentTimeMillis(),
    val totalFiles: Int,
    val pythonFilesCount: Int,
    val totalLinesOfCode: Int,
    val issues: List<CodeIssue>,
    val files: List<ProjectFile>
) {
    val criticalCount: Int get() = issues.count { it.severity == IssueSeverity.CRITICAL }
    val securityCount: Int get() = issues.count { it.severity == IssueSeverity.SECURITY }
    val warningCount: Int get() = issues.count { it.severity == IssueSeverity.WARNING }
}
