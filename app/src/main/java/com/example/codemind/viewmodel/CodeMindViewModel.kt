package com.example.codemind.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.codemind.data.db.CodeMindDatabase
import com.example.codemind.data.db.ScanEntity
import com.example.codemind.data.db.ScanRepository
import com.example.codemind.data.model.*
import com.example.codemind.data.samples.BundledDatasets
import com.example.codemind.data.samples.BundledProjects
import com.example.codemind.data.samples.PresetCodeSamples
import com.example.codemind.engine.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CodeMindViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ScanRepository

    init {
        val db = CodeMindDatabase.getDatabase(application)
        repository = ScanRepository(db.scanDao())
    }

    val scanHistory: StateFlow<List<ScanEntity>> = repository.allScans
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Editor & Analyzer State
    private val _currentCode = MutableStateFlow(PresetCodeSamples.presets.first().code)
    val currentCode: StateFlow<String> = _currentCode.asStateFlow()

    private val _selectedPresetTitle = MutableStateFlow(PresetCodeSamples.presets.first().title)
    val selectedPresetTitle: StateFlow<String> = _selectedPresetTitle.asStateFlow()

    private val _analysisResult = MutableStateFlow<AnalysisResult?>(null)
    val analysisResult: StateFlow<AnalysisResult?> = _analysisResult.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    // Auto-Fix State
    private val _fixResult = MutableStateFlow<FixResult?>(null)
    val fixResult: StateFlow<FixResult?> = _fixResult.asStateFlow()

    private val _undoStack = mutableListOf<String>()

    // Sandbox & Test Runner State
    private val _validationResult = MutableStateFlow<ValidationResult?>(null)
    val validationResult: StateFlow<ValidationResult?> = _validationResult.asStateFlow()

    private val _testCases = MutableStateFlow<List<TestCase>>(emptyList())
    val testCases: StateFlow<List<TestCase>> = _testCases.asStateFlow()

    private val _isValidating = MutableStateFlow(false)
    val isValidating: StateFlow<Boolean> = _isValidating.asStateFlow()

    // ML Lab State
    private val _selectedDataset = MutableStateFlow(BundledDatasets.irisDataset)
    val selectedDataset: StateFlow<MlDataset> = _selectedDataset.asStateFlow()

    private val _selectedAlgorithm = MutableStateFlow("Logistic Regression")
    val selectedAlgorithm: StateFlow<String> = _selectedAlgorithm.asStateFlow()

    private val _mlModelResult = MutableStateFlow<MlModelResult?>(null)
    val mlModelResult: StateFlow<MlModelResult?> = _mlModelResult.asStateFlow()

    private val _isTrainingMl = MutableStateFlow(false)
    val isTrainingMl: StateFlow<Boolean> = _isTrainingMl.asStateFlow()

    private val _predictionInputs = MutableStateFlow<Map<String, Double>>(
        mapOf("sepal_length" to 5.4, "sepal_width" to 3.4, "petal_length" to 1.5, "petal_width" to 0.4)
    )
    val predictionInputs: StateFlow<Map<String, Double>> = _predictionInputs.asStateFlow()

    private val _predictionOutput = MutableStateFlow<String?>(null)
    val predictionOutput: StateFlow<String?> = _predictionOutput.asStateFlow()

    // Project Scanner State
    private val _selectedProjectName = MutableStateFlow("Flask User Authentication Microservice")
    val selectedProjectName: StateFlow<String> = _selectedProjectName.asStateFlow()

    private val _currentProjectFiles = MutableStateFlow(BundledProjects.projectFlaskApi)
    val currentProjectFiles: StateFlow<List<ProjectFile>> = _currentProjectFiles.asStateFlow()

    private val _isScanningProject = MutableStateFlow(false)
    val isScanningProject: StateFlow<Boolean> = _isScanningProject.asStateFlow()

    private val _projectScanProgress = MutableStateFlow(0f)
    val projectScanProgress: StateFlow<Float> = _projectScanProgress.asStateFlow()

    private val _projectScanResult = MutableStateFlow<ProjectScanResult?>(null)
    val projectScanResult: StateFlow<ProjectScanResult?> = _projectScanResult.asStateFlow()

    // AI Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = ChatSender.ASSISTANT,
                text = "Welcome to **CodeMind AI**! I'm your intelligent Python debugging and Machine Learning copilot.\n\nI can analyze syntax, trace runtime bugs, optimize time complexity, and guide you through ML models. What are we building or debugging today?"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatTyping = MutableStateFlow(false)
    val isChatTyping: StateFlow<Boolean> = _isChatTyping.asStateFlow()

    // Settings State
    private val _sandboxTimeoutSeconds = MutableStateFlow(5)
    val sandboxTimeoutSeconds: StateFlow<Int> = _sandboxTimeoutSeconds.asStateFlow()

    private val _strictNetworkIsolation = MutableStateFlow(true)
    val strictNetworkIsolation: StateFlow<Boolean> = _strictNetworkIsolation.asStateFlow()

    private val _selectedAiModel = MutableStateFlow("gemini-2.5-flash")
    val selectedAiModel: StateFlow<String> = _selectedAiModel.asStateFlow()

    init {
        // Automatically analyze initial preset on startup
        runCodeAnalysis(_currentCode.value)
        // Train initial baseline ML model
        trainSelectedMlModel()
    }

    fun updateCode(newCode: String) {
        _currentCode.value = newCode
    }

    fun selectPreset(preset: com.example.codemind.data.samples.CodeSnippetPreset) {
        _selectedPresetTitle.value = preset.title
        _currentCode.value = preset.code
        runCodeAnalysis(preset.code)
    }

    fun runCodeAnalysis(code: String = _currentCode.value) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            delay(200) // Brief UI response feel
            val result = PythonSyntaxEngine.analyzeCode(code)
            _analysisResult.value = result
            _isAnalyzing.value = false

            // Persist scan to history
            val status = when {
                result.hasSyntaxErrors -> "CRITICAL_ISSUES"
                result.issues.isNotEmpty() -> "ISSUES_FOUND"
                else -> "CLEAN"
            }
            repository.saveScan(
                ScanEntity(
                    title = "Scan: ${_selectedPresetTitle.value}",
                    codePreview = code.lines().take(3).joinToString("\n"),
                    totalIssues = result.issues.size,
                    criticalIssues = result.criticalCount,
                    status = status,
                    fullCode = code,
                    analysisNotes = "Found ${result.issues.size} issues across ${result.linesOfCode} LOC."
                )
            )
        }
    }

    fun generateAutoFix() {
        val current = _currentCode.value
        val issues = _analysisResult.value?.issues ?: emptyList()
        _undoStack.add(current)
        val fix = PythonFixEngine.generateFix(current, issues)
        _fixResult.value = fix
    }

    fun acceptFix() {
        val fix = _fixResult.value ?: return
        _currentCode.value = fix.fixedCode
        runCodeAnalysis(fix.fixedCode)
        runSandboxValidation(fix.fixedCode)
        _fixResult.value = null
    }

    fun rejectFix() {
        _fixResult.value = null
    }

    fun undoLastFix() {
        if (_undoStack.isNotEmpty()) {
            val prev = _undoStack.removeAt(_undoStack.size - 1)
            _currentCode.value = prev
            runCodeAnalysis(prev)
            _fixResult.value = null
        }
    }

    fun runSandboxValidation(code: String = _currentCode.value) {
        viewModelScope.launch {
            _isValidating.value = true
            delay(350)
            val result = PythonSandboxEngine.runSandboxValidation(code)
            val tests = PythonSandboxEngine.generateTestsForCode(code)
            _validationResult.value = result
            _testCases.value = tests
            _isValidating.value = false
        }
    }

    // Machine Learning Controls
    fun selectMlDataset(dataset: MlDataset) {
        _selectedDataset.value = dataset
        // Update default prediction inputs
        val defaultInputs = mutableMapOf<String, Double>()
        dataset.columns.filter { it.type == "numeric" && it.name != dataset.targetColumn }.forEach { col ->
            defaultInputs[col.name] = col.mean ?: 1.0
        }
        _predictionInputs.value = defaultInputs
        _predictionOutput.value = null
        trainSelectedMlModel()
    }

    fun selectMlAlgorithm(algo: String) {
        _selectedAlgorithm.value = algo
        trainSelectedMlModel()
    }

    fun updatePredictionInput(featureName: String, value: Double) {
        val updated = _predictionInputs.value.toMutableMap()
        updated[featureName] = value
        _predictionInputs.value = updated
    }

    fun trainSelectedMlModel() {
        viewModelScope.launch {
            _isTrainingMl.value = true
            delay(300)
            val result = MachineLearningEngine.trainModel(
                dataset = _selectedDataset.value,
                algorithm = _selectedAlgorithm.value
            )
            _mlModelResult.value = result
            _isTrainingMl.value = false
        }
    }

    fun executePrediction() {
        val model = _mlModelResult.value ?: return
        val output = MachineLearningEngine.runInteractivePrediction(
            dataset = _selectedDataset.value,
            modelResult = model,
            inputValues = _predictionInputs.value
        )
        _predictionOutput.value = output
    }

    // Project Scanner Controls
    fun selectProject(isPipeline: Boolean) {
        if (isPipeline) {
            _selectedProjectName.value = "ML Data Processing Pipeline"
            _currentProjectFiles.value = BundledProjects.projectMlPipeline
        } else {
            _selectedProjectName.value = "Flask User Authentication Microservice"
            _currentProjectFiles.value = BundledProjects.projectFlaskApi
        }
        _projectScanResult.value = null
    }

    fun runProjectScan() {
        viewModelScope.launch {
            _isScanningProject.value = true
            _projectScanProgress.value = 0f

            val files = _currentProjectFiles.value
            val allIssues = mutableListOf<CodeIssue>()
            var totalLoc = 0

            for ((index, file) in files.withIndex()) {
                delay(200)
                _projectScanProgress.value = (index + 1).toFloat() / files.size
                if (file.name.endsWith(".py")) {
                    val analysis = PythonSyntaxEngine.analyzeCode(file.content, file.name)
                    allIssues.addAll(analysis.issues)
                    totalLoc += analysis.linesOfCode
                }
            }

            _projectScanResult.value = ProjectScanResult(
                projectName = _selectedProjectName.value,
                totalFiles = files.size,
                pythonFilesCount = files.count { it.name.endsWith(".py") },
                totalLinesOfCode = totalLoc,
                issues = allIssues,
                files = files
            )
            _isScanningProject.value = false
        }
    }

    // AI Chat Controls
    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(sender = ChatSender.USER, text = userText)
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            _isChatTyping.value = true
            val aiResponse = GeminiAiService.askAssistant(
                prompt = userText,
                activeCode = _currentCode.value,
                contextType = "Active Preset: ${_selectedPresetTitle.value}"
            )
            _isChatTyping.value = false
            val assistantMsg = ChatMessage(sender = ChatSender.ASSISTANT, text = aiResponse)
            _chatMessages.value = _chatMessages.value + assistantMsg
        }
    }

    // Settings Controls
    fun updateSandboxTimeout(seconds: Int) {
        _sandboxTimeoutSeconds.value = seconds
    }

    fun toggleNetworkIsolation() {
        _strictNetworkIsolation.value = !_strictNetworkIsolation.value
    }

    fun selectAiModel(model: String) {
        _selectedAiModel.value = model
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
