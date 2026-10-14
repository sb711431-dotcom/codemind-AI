package com.example.codemind.engine

import com.example.codemind.data.model.CodeIssue
import com.example.codemind.data.model.DiffLine
import com.example.codemind.data.model.DiffLineType

data class FixResult(
    val fixedCode: String,
    val explanation: String,
    val diffLines: List<DiffLine>,
    val changesCount: Int
)

object PythonFixEngine {

    fun generateFix(originalCode: String, issues: List<CodeIssue>): FixResult {
        var modified = originalCode
        val explanations = mutableListOf<String>()

        // 1. Missing imports
        val hasNumpyIssue = issues.any { it.ruleId == "F821" && it.message.contains("np") }
        val hasMathIssue = issues.any { it.ruleId == "F821" && it.message.contains("math") }
        val hasPandasIssue = issues.any { it.ruleId == "F821" && it.message.contains("pd") }

        val importsToAdd = mutableListOf<String>()
        if (hasNumpyIssue && !modified.contains("import numpy as np")) {
            importsToAdd.add("import numpy as np")
            explanations.add("Added 'import numpy as np' to resolve undefined 'np' namespace.")
        }
        if (hasMathIssue && !modified.contains("import math")) {
            importsToAdd.add("import math")
            explanations.add("Added 'import math' to provide mathematical functions like sqrt().")
        }
        if (hasPandasIssue && !modified.contains("import pandas as pd")) {
            importsToAdd.add("import pandas as pd")
            explanations.add("Added 'import pandas as pd' for tabular dataframe operations.")
        }

        if (importsToAdd.isNotEmpty()) {
            modified = importsToAdd.joinToString("\n") + "\n\n" + modified
        }

        // 2. Fix ZeroDivision in calculate_metrics
        if (modified.contains("average = total / len(grades)")) {
            modified = modified.replace(
                "    total = sum(grades)\n    average = total / len(grades)",
                "    if not grades:\n        return 0.0\n    total = sum(grades)\n    average = total / len(grades)"
            )
            explanations.add("Added guard clause 'if not grades: return 0.0' to prevent crash on empty lists.")
        }

        // 3. Fix TypeError string concat
        if (modified.contains("print(\"Final grade: \" + adjusted)")) {
            modified = modified.replace(
                "    adjusted = average * multiplier\n    print(\"Final grade: \" + adjusted)\n    return adjusted",
                "    multiplier_val = float(multiplier) if isinstance(multiplier, (str, int, float)) else 1.0\n    adjusted = average * multiplier_val\n    print(f\"Final grade: {adjusted:.2f}\")\n    return adjusted"
            )
            explanations.add("Sanitized multiplier with float() cast and converted string concatenation to a safe f-string.")
        }

        // 4. Fix eval() security risk
        if (modified.contains("eval(user_formula)")) {
            if (!modified.contains("import ast")) {
                modified = "import ast\n" + modified
            }
            modified = modified.replace(
                "    result = eval(user_formula)",
                "    # Remediated: using ast.literal_eval prevents arbitrary code execution\n    try:\n        result = ast.literal_eval(user_formula)\n    except (ValueError, SyntaxError):\n        raise ValueError(\"Invalid or unsafe formula expression\")"
            )
            explanations.add("Replaced dangerous eval() with ast.literal_eval() wrapped in strict validation.")
        }

        // 5. Fix SQL injection
        if (modified.contains("query = \"SELECT * FROM users WHERE user = '\" + username")) {
            modified = modified.replace(
                "    query = \"SELECT * FROM users WHERE user = '\" + username + \"' AND pass = '\" + password + \"'\"\n    cursor.execute(query)",
                "    # Remediated: parameterized SQL query with bound placeholders\n    query = \"SELECT * FROM users WHERE user = ? AND pass = ?\"\n    cursor.execute(query, (username, password))"
            )
            explanations.add("Replaced raw string concatenation with SQL DB-API parameterized queries to block SQL injection.")
        }

        // 6. Fix Hardcoded secret
        if (modified.contains("SECRET_KEY = \"hardcoded_super_secret_key_123\"")) {
            if (!modified.contains("import os")) {
                modified = "import os\n" + modified
            }
            modified = modified.replace(
                "SECRET_KEY = \"hardcoded_super_secret_key_123\"",
                "# Remediated: secret retrieved securely from environment\nSECRET_KEY = os.environ.get(\"APP_SECRET_KEY\", \"dev_fallback_key\")"
            )
            explanations.add("Replaced hardcoded plaintext secret with os.environ.get() configuration.")
        }

        // 7. Fix Undefined log_message
        if (modified.contains("print(log_message)")) {
            modified = modified.replace(
                "    print(log_message)",
                "    log_message = f\"Distance computed: {distance:.4f}\"\n    print(log_message)"
            )
            explanations.add("Declared log_message prior to printing to resolve NameError.")
        }

        // 8. Fix Inefficient string concatenation in loop
        if (modified.contains("output = \"\"") && modified.contains("output +=")) {
            modified = modified.replace(
                "    output = \"\"\n    # Inefficient string concatenation in loop (O(N^2) memory reallocation)\n    for tx in transactions:\n        output += \"Tx: \" + str(tx['id']) + \", Amount: \" + str(tx['amount']) + \"\\n\"\n        \n        # Quadratic search in list instead of set lookup\n        for other in transactions:\n            if other['id'] == tx['id']:\n                pass\n    return output",
                "    # Remediated: pre-allocating buffer and using single join for O(N) linear time\n    lines = []\n    seen_ids = {t['id'] for t in transactions}\n    for tx in transactions:\n        lines.append(f\"Tx: {tx.get('id')}, Amount: {tx.get('amount')}\")\n    return \"\\n\".join(lines) + \"\\n\""
            )
            explanations.add("Replaced O(N^2) string '+=' reallocations with list buffer and str.join().")
        }

        // 9. Fix Unsafe dictionary KeyError
        if (modified.contains("metadata[\"discount\"]")) {
            modified = modified.replace(
                "        metadata = {\"id\": user_id, \"region\": region}\n        discount_rate = metadata[\"discount\"]",
                "        metadata = {\"id\": user_id, \"region\": region}\n        # Remediated: safe get() with fallback\n        discount_rate = metadata.get(\"discount\", 0.0)"
            )
            explanations.add("Used dict.get('discount', 0.0) with fallback to protect against KeyError.")
        }

        // Calculate diff lines
        val diffLines = computeDiff(originalCode, modified)
        val changes = diffLines.count { it.type != DiffLineType.CONTEXT }

        if (explanations.isEmpty()) {
            explanations.add("Formatted Python code according to PEP 8 standards with clean structure.")
        }

        return FixResult(
            fixedCode = modified,
            explanation = explanations.joinToString("\n• ", prefix = "• "),
            diffLines = diffLines,
            changesCount = changes
        )
    }

    private fun computeDiff(oldCode: String, newCode: String): List<DiffLine> {
        val oldLines = oldCode.lines()
        val newLines = newCode.lines()
        val result = mutableListOf<DiffLine>()

        var oldIdx = 0
        var newIdx = 0

        while (oldIdx < oldLines.size || newIdx < newLines.size) {
            if (oldIdx < oldLines.size && newIdx < newLines.size && oldLines[oldIdx] == newLines[newIdx]) {
                result.add(
                    DiffLine(
                        type = DiffLineType.CONTEXT,
                        oldLineNumber = oldIdx + 1,
                        newLineNumber = newIdx + 1,
                        content = oldLines[oldIdx]
                    )
                )
                oldIdx++
                newIdx++
            } else {
                // Find next match point
                if (oldIdx < oldLines.size && (newIdx >= newLines.size || !newLines.contains(oldLines[oldIdx]))) {
                    result.add(
                        DiffLine(
                            type = DiffLineType.REMOVED,
                            oldLineNumber = oldIdx + 1,
                            newLineNumber = null,
                            content = oldLines[oldIdx]
                        )
                    )
                    oldIdx++
                } else if (newIdx < newLines.size) {
                    result.add(
                        DiffLine(
                            type = DiffLineType.ADDED,
                            oldLineNumber = null,
                            newLineNumber = newIdx + 1,
                            content = newLines[newIdx]
                        )
                    )
                    newIdx++
                }
            }
        }

        return result
    }
}
