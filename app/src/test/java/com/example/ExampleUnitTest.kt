package com.example

import com.example.codemind.data.samples.BundledDatasets
import com.example.codemind.engine.MachineLearningEngine
import com.example.codemind.engine.PythonFixEngine
import com.example.codemind.engine.PythonSyntaxEngine
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testPythonSyntaxEngineDetectsZeroDivision() {
        val buggyCode = """
            def calc(grades):
                return sum(grades) / len(grades)
        """.trimIndent()

        val result = PythonSyntaxEngine.analyzeCode(buggyCode)
        assertFalse(result.isClean)
        assertTrue(result.issues.any { it.ruleId == "W0702" })
    }

    @Test
    fun testPythonSyntaxEngineDetectsEvalSecurityRisk() {
        val securityFlawCode = """
            def execute(cmd):
                return eval(cmd)
        """.trimIndent()

        val result = PythonSyntaxEngine.analyzeCode(securityFlawCode)
        assertTrue(result.issues.any { it.ruleId == "B307" })
    }

    @Test
    fun testPythonFixEngineGeneratesRemediation() {
        val buggyCode = """
            def calc(grades):
                total = sum(grades)
                average = total / len(grades)
                return average
        """.trimIndent()

        val analysis = PythonSyntaxEngine.analyzeCode(buggyCode)
        val fix = PythonFixEngine.generateFix(buggyCode, analysis.issues)
        assertTrue(fix.fixedCode.contains("if not grades:"))
    }

    @Test
    fun testMachineLearningEngineClassification() {
        val dataset = BundledDatasets.irisDataset
        val model = MachineLearningEngine.trainModel(dataset, "Logistic Regression")
        assertNotNull(model.evaluation.accuracy)
        assertTrue(model.evaluation.accuracy!! >= 0.70)
        assertNotNull(model.evaluation.confusionMatrix)
    }
}
