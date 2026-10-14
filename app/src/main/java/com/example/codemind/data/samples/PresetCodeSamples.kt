package com.example.codemind.data.samples

data class CodeSnippetPreset(
    val title: String,
    val description: String,
    val category: String,
    val code: String,
    val expectedIssuesCount: Int
)

object PresetCodeSamples {
    val presets = listOf(
        CodeSnippetPreset(
            title = "ZeroDivision & Type Error",
            description = "Calculates average scores with zero-division vulnerability and unchecked string types.",
            category = "Runtime Bug",
            expectedIssuesCount = 3,
            code = """
def calculate_metrics(grades, multiplier):
    # Potential ZeroDivisionError if list is empty
    total = sum(grades)
    average = total / len(grades)
    
    # TypeError: multiplying string by float without casting
    adjusted = average * multiplier
    print("Final grade: " + adjusted)
    return adjusted

scores = []
# Will crash at runtime
result = calculate_metrics(scores, "1.2")
""".trimIndent()
        ),
        CodeSnippetPreset(
            title = "Security Vulnerability: eval & SQL",
            description = "Dangerous dynamic execution with eval() and unsafe string formatting in SQL query.",
            category = "Security",
            expectedIssuesCount = 3,
            code = """
import sqlite3

def run_user_calculation(user_formula):
    # Critical Security Risk: arbitrary remote code execution via eval
    result = eval(user_formula)
    return result

def query_user_account(username, password):
    conn = sqlite3.connect("users.db")
    cursor = conn.cursor()
    # Critical Security Risk: SQL injection via string concatenation
    query = "SELECT * FROM users WHERE user = '" + username + "' AND pass = '" + password + "'"
    cursor.execute(query)
    return cursor.fetchall()
""".trimIndent()
        ),
        CodeSnippetPreset(
            title = "Missing Imports & Undefined Variable",
            description = "Uses NumPy and Math methods without imports, and references out-of-scope variables.",
            category = "Syntax / Import",
            expectedIssuesCount = 3,
            code = """
def compute_euclidean_distance(point_a, point_b):
    # Missing import: numpy is used without 'import numpy as np'
    diff = np.array(point_a) - np.array(point_b)
    
    # Missing import: math.sqrt is called without 'import math'
    distance = math.sqrt(sum(diff ** 2))
    
    # Undefined variable reference: log_message is not declared
    print(log_message)
    return distance

p1 = [1, 2, 3]
p2 = [4, 5, 6]
compute_euclidean_distance(p1, p2)
""".trimIndent()
        ),
        CodeSnippetPreset(
            title = "Quadratic Bottleneck & String Concat",
            description = "O(N^2) nested loop with repeatedly reallocated string concatenation in loop.",
            category = "Performance",
            expectedIssuesCount = 2,
            code = """
def build_report_string(transactions):
    output = ""
    # Inefficient string concatenation in loop (O(N^2) memory reallocation)
    for tx in transactions:
        output += "Tx: " + str(tx['id']) + ", Amount: " + str(tx['amount']) + "\n"
        
        # Quadratic search in list instead of set lookup
        for other in transactions:
            if other['id'] == tx['id']:
                pass
    return output
""".trimIndent()
        ),
        CodeSnippetPreset(
            title = "IndexError & KeyError in ETL",
            description = "Unsafe list indexing and dictionary bracket access without key existence checks.",
            category = "Data Pipeline",
            expectedIssuesCount = 2,
            code = """
def process_customer_record(raw_records):
    parsed = []
    for item in raw_records:
        # Potential IndexError if tokens list has fewer than 3 elements
        tokens = item.split(",")
        user_id = tokens[0]
        region = tokens[1]
        tier = tokens[2]
        
        # Potential KeyError if dictionary misses 'discount'
        metadata = {"id": user_id, "region": region}
        discount_rate = metadata["discount"]
        parsed.append((user_id, discount_rate))
    return parsed
""".trimIndent()
        ),
        CodeSnippetPreset(
            title = "Production Clean: Validated Service",
            description = "Well-structured Python module with type annotations, error handling, and clean imports.",
            category = "Best Practice",
            expectedIssuesCount = 0,
            code = """
from typing import List, Optional
import math

class DataSanitizer:
    def __init__(self, fallback_value: float = 0.0) -> None:
        self.fallback = fallback_value

    def safe_average(self, values: List[float]) -> float:
        if not values:
            return self.fallback
        return sum(values) / len(values)

    def compute_standard_deviation(self, values: List[float]) -> float:
        if len(values) < 2:
            return 0.0
        avg = self.safe_average(values)
        variance = sum((x - avg) ** 2 for x in values) / (len(values) - 1)
        return math.sqrt(variance)
""".trimIndent()
        )
    )
}
