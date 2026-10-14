# CodeMind AI: Professional AI-Powered Python Debugging & Machine Learning Studio

**CodeMind AI** is a complete, full-stack, developer-focused platform designed for Android, foldable devices, and tablets. It combines deep Python AST static analysis, automatic code repair with verified diffs, multi-module project scanning, and a dedicated Machine Learning lab with real dataset training and interactive model inference.

---

## 🚀 Key Features

### 1. 🧠 AI Code Analyzer
* **Interactive Code Editor**: Syntax-highlighted code editor with line number gutter, error indicators, and customizable presets.
* **Bug & Security Detection**:
  * **Critical Syntax & Runtime Errors**: Catches unclosed brackets, missing colons, indentation mismatches, `ZeroDivisionError`, `TypeError`, and `IndexError`.
  * **OWASP & Bandit Security Audit**: Detects dangerous arbitrary code execution via `eval()`/`exec()`, raw SQL injection queries, and hardcoded secrets.
  * **Performance & Complexity Analysis**: Identifies quadratic $O(N^2)$ nested loops and immutable string concatenation in loops.
  * **Missing Imports**: Automatically resolves missing `numpy`, `pandas`, and `math` namespaces.
* **Beginner-Friendly Explanations**: Details the exact file, line number, rule identifier, and why each error occurs.

### 2. ⚡ Automatic Code Repair ("Fix with AI")
* **Unified & Side-by-Side Diff View**: Highlights line additions (`+` in green) and deletions (`-` in red).
* **Rationale Breakdown**: Explains why each remediation was applied before touching your code.
* **Non-Destructive Workflow**: Review, Accept, Reject, or Undo proposed fixes with full state-stack backups.
* **Post-Fix Sandbox Verification**: Automatically runs test suites against repaired code to verify zero regression.

### 3. 📂 Full Project Scanner
* **Multi-Module Exploration**: Inspects multi-file Python repositories (e.g., Flask REST APIs and ML ETL pipelines).
* **Dependency & Import Analysis**: Parses module relationships, `requirements.txt` specifications, and unit test files.
* **Project-Wide Audit Reports**: Provides categorized severity summaries, progress indicators, and one-tap exportable reports.

### 4. 🔬 Machine Learning Workspace (ML Lab)
* **Tabular Datasets (CSV)**: Bundled with classic benchmarks (Iris Flower Classification, California Housing Prices, Customer Mall Segmentation).
* **Exploratory Data Analysis (EDA)**: Interactive Compose Canvas visualizations including scatter plots and feature importance rankings.
* **Real Model Training**:
  * **Classification**: Logistic Regression with gradient descent, K-Nearest Neighbors (KNN).
  * **Regression**: Linear Regression (OLS), Ridge Regularization.
  * **Clustering**: K-Means Clustering with iterative centroid recalculation and inertia scoring.
* **Evaluation Telemetry**: Real computation of Accuracy, Precision, Recall, F1-Score, MSE, $R^2$ Variance, and interactive Confusion Matrices.
* **Interactive Inference Sandbox**: Adjust feature sliders and generate live predictions.
* **Exportable Python Scripts**: Generates production-ready Scikit-Learn, Pandas, and Matplotlib code.

### 5. 💬 AI Coding Assistant
* **Conversational Copilot**: Powered by Gemini API (Gemini 2.5 Flash / Pro).
* **Context-Aware Assistance**: Answers questions with direct awareness of active editor code and project files.
* **Quick Prompt Chips**: Instant actions for line-by-line explanation, PyTest generation, time complexity optimization, and security audits.

### 6. 🧪 PyTest Test Runner & Sandbox Security
* **Automated PyTest Generation**: Synthesizes comprehensive assertion suites covering normal cases, empty inputs, and boundary conditions.
* **Isolated Sandbox Execution**: Enforces configurable execution timeouts (3s, 5s, 10s), memory bounds, and strict network isolation.

---

## 🛠️ Architecture & Tech Stack

* **Platform**: Android SDK 36 (Android 15+ compatible), Material Design 3 (M3).
* **UI Framework**: 100% Jetpack Compose with Edge-to-Edge full-bleed display.
* **State Management**: Android MVVM architecture with `ViewModel` and `MutableStateFlow`.
* **Local Persistence**: Android Room SQLite Database for scan histories and audit telemetry.
* **Networking**: Retrofit & OkHttp with Gemini REST integration.
* **Image Loading**: Coil Compose.

---

## ⚙️ Setup & Installation Instructions

### Windows (PowerShell)
```powershell
# 1. Clone the repository
git clone <repo-url>
cd codemind-ai

# 2. Configure Gemini API Key (Optional for Live Cloud AI)
# AI Studio automatically injects this from the Secrets Panel.
# For local Gradle runs, set environment variable:
${env:GEMINI_API_KEY} = "your_gemini_api_key_here"

# 3. Build debug APK
gradle assembleDebug

# 4. Run unit and Robolectric tests
gradle :app:testDebugUnitTest
```

### Linux / macOS (Bash)
```bash
# 1. Build debug APK
gradle assembleDebug

# 2. Run unit and Robolectric tests
gradle :app:testDebugUnitTest
```

---

## 🔒 Security Best Practices

1. **Sandboxed Code Execution**: CodeMind AI treats all user code as untrusted. Simulated executions run with strict time and memory bounds without accessing the host device filesystem or opening socket connections.
2. **Zero-Secret Exposure**: Sensitive credentials and API keys are managed exclusively via the AI Studio Secrets panel and accessed securely via `BuildConfig`.
