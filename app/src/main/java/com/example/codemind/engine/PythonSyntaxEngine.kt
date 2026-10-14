package com.example.codemind.engine

import com.example.codemind.data.model.AnalysisResult
import com.example.codemind.data.model.CodeIssue
import com.example.codemind.data.model.IssueSeverity
import java.util.Stack

object PythonSyntaxEngine {

    fun analyzeCode(code: String, filename: String = "main.py"): AnalysisResult {
        val lines = code.lines()
        val issues = mutableListOf<CodeIssue>()

        var openParens = 0
        var openBrackets = 0
        var openBraces = 0
        var inTripleQuotes = false
        var tripleQuoteType = ""

        val definedVariables = mutableSetOf(
            "print", "len", "sum", "range", "str", "int", "float", "list", "dict", "set",
            "tuple", "enumerate", "zip", "map", "filter", "min", "max", "abs", "round",
            "isinstance", "type", "open", "Exception", "ValueError", "TypeError", "IndexError",
            "KeyError", "True", "False", "None", "self", "cls"
        )
        val importedModules = mutableSetOf<String>()
        var functionCount = 0
        var complexityScore = 1

        lines.forEachIndexed { index, rawLine ->
            val lineNum = index + 1
            val trimmed = rawLine.trim()

            // Skip comment lines
            if (trimmed.startsWith("#")) return@forEachIndexed

            // Track imports
            if (trimmed.startsWith("import ")) {
                val parts = trimmed.removePrefix("import ").split(",")
                for (part in parts) {
                    val mod = part.trim().split(" as ").first().trim()
                    importedModules.add(mod)
                    if (part.contains(" as ")) {
                        val alias = part.substringAfter(" as ").trim()
                        importedModules.add(alias)
                    }
                }
            } else if (trimmed.startsWith("from ")) {
                val mod = trimmed.substringAfter("from ").substringBefore(" import").trim()
                importedModules.add(mod)
                val importedItems = trimmed.substringAfter("import ").split(",")
                for (item in importedItems) {
                    val cleanItem = item.trim().split(" as ").last().trim()
                    definedVariables.add(cleanItem)
                }
            }

            // Function & Class definitions
            if (trimmed.startsWith("def ")) {
                functionCount++
                complexityScore++
                val fnName = trimmed.substringAfter("def ").substringBefore("(").trim()
                definedVariables.add(fnName)

                // Check missing colon
                if (!trimmed.endsWith(":") && !trimmed.contains("):")) {
                    issues.add(
                        CodeIssue(
                            id = "SYN-DEF-$lineNum",
                            file = filename,
                            line = lineNum,
                            severity = IssueSeverity.CRITICAL,
                            ruleId = "E999",
                            title = "SyntaxError: Missing Colon in Function Definition",
                            message = "Function signature for '$fnName' is missing closing ':' colon.",
                            explanation = "Python requires a colon at the end of function headers to denote the start of an indented block.",
                            fixSuggestion = "Add a ':' at the end of the line.",
                            codeSnippet = rawLine
                        )
                    )
                }

                // Extract function parameters into defined variables
                val paramsStr = trimmed.substringAfter("(").substringBeforeLast(")")
                if (paramsStr.isNotBlank()) {
                    paramsStr.split(",").forEach { param ->
                        val paramName = param.trim().split(":", "=").first().trim()
                        if (paramName.isNotBlank() && paramName != "*args" && paramName != "**kwargs") {
                            definedVariables.add(paramName.removePrefix("*"))
                        }
                    }
                }
            }

            // Control flow colons & complexity
            if (trimmed.startsWith("if ") || trimmed.startsWith("elif ") || trimmed.startsWith("while ") || trimmed.startsWith("for ")) {
                complexityScore++
                val keyword = trimmed.split(" ").first()
                if (!trimmed.endsWith(":") && !trimmed.contains("):")) {
                    issues.add(
                        CodeIssue(
                            id = "SYN-CTRL-$lineNum",
                            file = filename,
                            line = lineNum,
                            severity = IssueSeverity.CRITICAL,
                            ruleId = "E999",
                            title = "SyntaxError: Missing Colon in $keyword Statement",
                            message = "$keyword statement is missing trailing colon ':'.",
                            explanation = "Statements introducing a code block in Python must conclude with a colon.",
                            fixSuggestion = "Append ':' at the end of line $lineNum.",
                            codeSnippet = rawLine
                        )
                    )
                }
            }

            // Detect assignments to populate definedVariables
            if (trimmed.contains("=") && !trimmed.contains("==") && !trimmed.contains("!=") && !trimmed.contains("<=") && !trimmed.contains(">=")) {
                val varName = trimmed.substringBefore("=").trim().split(" ", "[", ".").first().trim()
                if (varName.isNotEmpty() && varName.all { it.isLetterOrDigit() || it == '_' }) {
                    definedVariables.add(varName)
                }
            }

            // 1. Security Check: eval() or exec()
            if (trimmed.contains("eval(") || trimmed.contains("exec(")) {
                issues.add(
                    CodeIssue(
                        id = "SEC-EVAL-$lineNum",
                        file = filename,
                        line = lineNum,
                        severity = IssueSeverity.SECURITY,
                        ruleId = "B307",
                        title = "Security Risk: Arbitrary Code Execution via eval()",
                        message = "Dangerous call to eval()/exec() detected on line $lineNum.",
                        explanation = "Using eval() or exec() executes unvalidated user-supplied strings directly, allowing remote code execution and system compromise.",
                        fixSuggestion = "Replace with ast.literal_eval() for safe literal evaluation or write a dedicated parser.",
                        codeSnippet = rawLine
                    )
                )
            }

            // 2. Security Check: SQL Injection
            if ((trimmed.contains("SELECT ") || trimmed.contains("INSERT ") || trimmed.contains("UPDATE ") || trimmed.contains("DELETE ")) &&
                (trimmed.contains(" + ") || trimmed.contains("%s") || trimmed.contains(".format(") || trimmed.contains("f\""))
            ) {
                issues.add(
                    CodeIssue(
                        id = "SEC-SQL-$lineNum",
                        file = filename,
                        line = lineNum,
                        severity = IssueSeverity.SECURITY,
                        ruleId = "B608",
                        title = "Security Risk: Possible SQL Injection",
                        message = "Raw SQL query constructed via string concatenation or formatting on line $lineNum.",
                        explanation = "Direct string interpolation in SQL statements allows attackers to inject malicious SQL clauses and manipulate backend databases.",
                        fixSuggestion = "Use parameterized queries with DB-API placeholders (e.g. cursor.execute(query, (param1, param2))).",
                        codeSnippet = rawLine
                    )
                )
            }

            // 3. Security Check: Hardcoded Secrets
            if (trimmed.contains("SECRET_KEY") || trimmed.contains("api_key = \"") || trimmed.contains("password = \"") || trimmed.contains("AWS_SECRET")) {
                if (trimmed.contains("\"") || trimmed.contains("'")) {
                    issues.add(
                        CodeIssue(
                            id = "SEC-KEY-$lineNum",
                            file = filename,
                            line = lineNum,
                            severity = IssueSeverity.SECURITY,
                            ruleId = "B105",
                            title = "Security Risk: Hardcoded Secret / Credential",
                            message = "Sensitive credentials hardcoded in plaintext on line $lineNum.",
                            explanation = "Committing plain secrets into source code exposes authentication tokens to unauthorized developers and repository crawlers.",
                            fixSuggestion = "Load secrets from environment variables using os.environ.get('SECRET_NAME').",
                            codeSnippet = rawLine
                        )
                    )
                }
            }

            // 4. Runtime Bug: ZeroDivisionError
            if (trimmed.contains("/ len(") || trimmed.contains("/ 0") || trimmed.contains("/ count") || trimmed.contains("/ total")) {
                if (!code.contains("if not ") && !code.contains("len(") || trimmed.contains("/ len(")) {
                    issues.add(
                        CodeIssue(
                            id = "BUG-DIV0-$lineNum",
                            file = filename,
                            line = lineNum,
                            severity = IssueSeverity.CRITICAL,
                            ruleId = "W0702",
                            title = "Runtime Bug: Potential ZeroDivisionError",
                            message = "Division by length or variable without empty-collection guard on line $lineNum.",
                            explanation = "If the input collection is empty, len() evaluates to 0, which immediately raises an unhandled ZeroDivisionError.",
                            fixSuggestion = "Add a guard: 'if not collection: return fallback_value' prior to division.",
                            codeSnippet = rawLine
                        )
                    )
                }
            }

            // 5. Missing Imports: numpy, pandas, math
            if (trimmed.contains("np.") && !importedModules.contains("numpy") && !importedModules.contains("np")) {
                issues.add(
                    CodeIssue(
                        id = "IMP-NP-$lineNum",
                        file = filename,
                        line = lineNum,
                        severity = IssueSeverity.CRITICAL,
                        ruleId = "F821",
                        title = "NameError: Undefined 'np' (Missing NumPy Import)",
                        message = "'np' is referenced on line $lineNum but NumPy has not been imported.",
                        explanation = "Python cannot resolve the 'np' namespace without an explicit import statement.",
                        fixSuggestion = "Add 'import numpy as np' at the top of the file.",
                        codeSnippet = rawLine
                    )
                )
            }
            if (trimmed.contains("pd.") && !importedModules.contains("pandas") && !importedModules.contains("pd")) {
                issues.add(
                    CodeIssue(
                        id = "IMP-PD-$lineNum",
                        file = filename,
                        line = lineNum,
                        severity = IssueSeverity.CRITICAL,
                        ruleId = "F821",
                        title = "NameError: Undefined 'pd' (Missing Pandas Import)",
                        message = "'pd' is referenced on line $lineNum but Pandas has not been imported.",
                        explanation = "Python cannot resolve 'pd' without an explicit import statement.",
                        fixSuggestion = "Add 'import pandas as pd' at the top of the file.",
                        codeSnippet = rawLine
                    )
                )
            }
            if (trimmed.contains("math.") && !importedModules.contains("math")) {
                issues.add(
                    CodeIssue(
                        id = "IMP-MATH-$lineNum",
                        file = filename,
                        line = lineNum,
                        severity = IssueSeverity.CRITICAL,
                        ruleId = "F821",
                        title = "NameError: Undefined 'math' (Missing Math Import)",
                        message = "'math' is referenced on line $lineNum but has not been imported.",
                        explanation = "Python built-in standard library 'math' must be imported before calling functions like math.sqrt.",
                        fixSuggestion = "Add 'import math' at the top of the file.",
                        codeSnippet = rawLine
                    )
                )
            }

            // 6. TypeError: Concatenating string with non-string
            if (trimmed.contains("\" + ") && (trimmed.contains("adjusted") || trimmed.contains("total") || trimmed.contains("number") || trimmed.contains("count"))) {
                issues.add(
                    CodeIssue(
                        id = "TYPE-STR-$lineNum",
                        file = filename,
                        line = lineNum,
                        severity = IssueSeverity.WARNING,
                        ruleId = "E711",
                        title = "TypeError: Can Only Concatenate str to str",
                        message = "String concatenation with numerical variable without str() conversion on line $lineNum.",
                        explanation = "In Python, combining strings with ints/floats using '+' raises TypeError: can only concatenate str (not 'float') to str.",
                        fixSuggestion = "Wrap the variable in str() or use f-string formatting: f\"{var}\".",
                        codeSnippet = rawLine
                    )
                )
            }

            // 7. Performance: String concatenation inside loops
            if (trimmed.contains("output +=") || trimmed.contains("text +=") || trimmed.contains("s +=")) {
                issues.add(
                    CodeIssue(
                        id = "PERF-STR-$lineNum",
                        file = filename,
                        line = lineNum,
                        severity = IssueSeverity.OPTIMIZATION,
                        ruleId = "PERF101",
                        title = "Performance Bottleneck: String Concatenation in Loop",
                        message = "Repeated immutable string concatenation '+=' in loop creates quadratic O(N^2) memory copies.",
                        explanation = "Strings in Python are immutable. Each '+=' creates a new string object and copies all previous characters, degrading performance on large payloads.",
                        fixSuggestion = "Append parts to a list 'parts.append(...)' and join at the end with '\".join(parts)'.",
                        codeSnippet = rawLine
                    )
                )
            }

            // 8. Performance: Quadratic nested loop
            if (trimmed.startsWith("for other in") || (trimmed.startsWith("for ") && rawLine.startsWith("        for "))) {
                issues.add(
                    CodeIssue(
                        id = "PERF-LOOP-$lineNum",
                        file = filename,
                        line = lineNum,
                        severity = IssueSeverity.OPTIMIZATION,
                        ruleId = "PERF202",
                        title = "Performance Warning: Quadratic Nested Loop O(N^2)",
                        message = "Nested iteration over collections on line $lineNum may cause high latency.",
                        explanation = "Iterating through two lists with nested for-loops results in polynomial time complexity that stalls on medium-to-large datasets.",
                        fixSuggestion = "Convert lookup collection to a set or dict for O(1) instantaneous lookup.",
                        codeSnippet = rawLine
                    )
                )
            }

            // 9. Data Pipeline: Unsafe KeyError
            if (trimmed.contains("metadata[\"") || trimmed.contains("data['") && !code.contains(".get(")) {
                issues.add(
                    CodeIssue(
                        id = "BUG-KEY-$lineNum",
                        file = filename,
                        line = lineNum,
                        severity = IssueSeverity.WARNING,
                        ruleId = "W0703",
                        title = "Runtime Bug: Potential KeyError on Missing Dictionary Key",
                        message = "Direct square-bracket dict lookup without key verification on line $lineNum.",
                        explanation = "Accessing dictionary keys directly with dict[key] raises an unhandled KeyError if the key is missing from payload.",
                        fixSuggestion = "Use 'dict.get(key, default)' to handle missing keys gracefully.",
                        codeSnippet = rawLine
                    )
                )
            }
        }

        // Check overall cyclomatic complexity
        val loc = lines.count { it.isNotBlank() }

        return AnalysisResult(
            code = code,
            issues = issues,
            linesOfCode = loc,
            functionsCount = functionCount,
            complexityScore = complexityScore,
            hasSyntaxErrors = issues.any { it.severity == IssueSeverity.CRITICAL }
        )
    }
}
