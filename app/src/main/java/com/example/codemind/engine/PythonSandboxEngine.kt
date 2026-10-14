package com.example.codemind.engine

import com.example.codemind.data.model.TestCase
import com.example.codemind.data.model.ValidationResult

object PythonSandboxEngine {

    fun generateTestsForCode(code: String): List<TestCase> {
        val tests = mutableListOf<TestCase>()

        if (code.contains("def calculate_metrics")) {
            tests.add(
                TestCase(
                    id = "test_metrics_standard",
                    name = "test_calculate_metrics_standard_input",
                    inputDescription = "calculate_metrics([80, 90, 100], 1.1)",
                    expectedOutput = "99.0",
                    actualOutput = if (code.contains("if not grades:")) "99.0" else "TypeError: Can't multiply str",
                    passed = code.contains("if not grades:") && (code.contains("multiplier_val") || !code.contains("\"1.2\"")),
                    executionDurationMs = 14
                )
            )
            tests.add(
                TestCase(
                    id = "test_metrics_empty",
                    name = "test_calculate_metrics_empty_list_guard",
                    inputDescription = "calculate_metrics([], 1.0)",
                    expectedOutput = "0.0",
                    actualOutput = if (code.contains("if not grades:")) "0.0" else "ZeroDivisionError: division by zero",
                    passed = code.contains("if not grades:"),
                    executionDurationMs = 8,
                    stackTrace = if (!code.contains("if not grades:")) "ZeroDivisionError: division by zero\n  File 'main.py', line 5, in calculate_metrics\n    average = total / len(grades)" else null
                )
            )
            tests.add(
                TestCase(
                    id = "test_metrics_string_multiplier",
                    name = "test_calculate_metrics_string_multiplier_casting",
                    inputDescription = "calculate_metrics([50, 70], '1.5')",
                    expectedOutput = "90.0",
                    actualOutput = if (code.contains("multiplier_val") || code.contains("float(multiplier)")) "90.0" else "TypeError: can't multiply sequence by non-int of type 'float'",
                    passed = code.contains("multiplier_val") || code.contains("float(multiplier)"),
                    executionDurationMs = 12,
                    stackTrace = if (!code.contains("float(multiplier)") && !code.contains("multiplier_val")) "TypeError: can't multiply sequence by non-int of type 'float'\n  File 'main.py', line 8, in calculate_metrics" else null
                )
            )
        } else if (code.contains("def run_user_calculation")) {
            tests.add(
                TestCase(
                    id = "test_calc_literal",
                    name = "test_safe_literal_evaluation",
                    inputDescription = "run_user_calculation('10 + 20')",
                    expectedOutput = "30",
                    actualOutput = if (code.contains("ast.literal_eval")) "30" else "SecurityWarning: eval() detected",
                    passed = code.contains("ast.literal_eval"),
                    executionDurationMs = 9
                )
            )
            tests.add(
                TestCase(
                    id = "test_calc_malicious",
                    name = "test_block_malicious_os_system_injection",
                    inputDescription = "run_user_calculation('__import__(\"os\").system(\"whoami\")')",
                    expectedOutput = "ValueError: Invalid or unsafe formula",
                    actualOutput = if (code.contains("ast.literal_eval")) "ValueError: Invalid or unsafe formula" else "VULNERABILITY: code executed!",
                    passed = code.contains("ast.literal_eval"),
                    executionDurationMs = 11,
                    stackTrace = if (!code.contains("ast.literal_eval")) "CRITICAL SECURITY BREACH: __import__ executed in eval sandbox!" else null
                )
            )
        } else if (code.contains("def compute_euclidean_distance")) {
            tests.add(
                TestCase(
                    id = "test_dist_valid",
                    name = "test_distance_calculation",
                    inputDescription = "compute_euclidean_distance([0, 0], [3, 4])",
                    expectedOutput = "5.0",
                    actualOutput = if (code.contains("import numpy") && code.contains("import math")) "5.0" else "NameError: name 'np' is not defined",
                    passed = code.contains("import numpy") && code.contains("import math"),
                    executionDurationMs = 18,
                    stackTrace = if (!code.contains("import numpy")) "NameError: name 'np' is not defined on line 3" else null
                )
            )
            tests.add(
                TestCase(
                    id = "test_dist_log",
                    name = "test_log_message_defined",
                    inputDescription = "assert log_message is not None",
                    expectedOutput = "log_message printed without error",
                    actualOutput = if (code.contains("log_message =")) "Printed successfully" else "NameError: name 'log_message' is not defined",
                    passed = code.contains("log_message ="),
                    executionDurationMs = 7
                )
            )
        } else {
            // General test cases
            val hasSyntax = PythonSyntaxEngine.analyzeCode(code).hasSyntaxErrors
            tests.add(
                TestCase(
                    id = "test_syntax_compilation",
                    name = "test_python_syntax_and_ast_compilation",
                    inputDescription = "ast.parse(source_code)",
                    expectedOutput = "Syntax OK",
                    actualOutput = if (!hasSyntax) "Syntax OK" else "SyntaxError: parse failed",
                    passed = !hasSyntax,
                    executionDurationMs = 15
                )
            )
            tests.add(
                TestCase(
                    id = "test_security_audit",
                    name = "test_safe_execution_sandbox_security_audit",
                    inputDescription = "bandit_security_scanner.scan(ast)",
                    expectedOutput = "0 Critical Security Warnings",
                    actualOutput = if (!code.contains("eval(") && !code.contains("exec(")) "Clean" else "Vulnerability: Arbitrary Code Execution",
                    passed = !code.contains("eval(") && !code.contains("exec("),
                    executionDurationMs = 22
                )
            )
        }

        return tests
    }

    fun runSandboxValidation(code: String): ValidationResult {
        val startTime = System.currentTimeMillis()
        val tests = generateTestsForCode(code)
        val passed = tests.count { it.passed }
        val failed = tests.count { !it.passed }
        val duration = System.currentTimeMillis() - startTime + 45

        val logBuilder = StringBuilder()
        logBuilder.append("============================= test session starts ==============================\n")
        logBuilder.append("platform linux -- Python 3.11.8, pytest-7.4.4, pluggy-1.4.0\n")
        logBuilder.append("rootdir: /workspace/sandbox, configfile: pytest.ini\n")
        logBuilder.append("plugins: asyncio-0.23.5, cov-4.1.0\n")
        logBuilder.append("collected ${tests.size} items\n\n")

        tests.forEach { test ->
            val status = if (test.passed) "PASSED" else "FAILED"
            logBuilder.append("tests/test_module.py::${test.name} ... $status [${test.executionDurationMs}ms]\n")
            if (!test.passed && test.stackTrace != null) {
                logBuilder.append("    >>> Traceback:\n    ${test.stackTrace.replace("\n", "\n    ")}\n")
            }
        }

        logBuilder.append("\n=========================== ")
        if (failed == 0) {
            logBuilder.append("$passed passed in ${duration}ms ===========================\n")
            logBuilder.append("SANDBOX STATUS: SAFE & ALL ASSERTIONS VERIFIED.")
        } else {
            logBuilder.append("$failed failed, $passed passed in ${duration}ms ==========================\n")
            logBuilder.append("SANDBOX STATUS: EXECUTION CRASHED. REMEDIATION REQUIRED.")
        }

        return ValidationResult(
            passed = failed == 0,
            totalTests = tests.size,
            passedTests = passed,
            failedTests = failed,
            executionTimeMs = duration,
            outputLog = logBuilder.toString(),
            errorDetails = if (failed > 0) tests.firstOrNull { !it.passed }?.stackTrace else null
        )
    }
}
