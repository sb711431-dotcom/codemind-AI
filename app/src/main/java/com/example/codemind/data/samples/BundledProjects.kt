package com.example.codemind.data.samples

import com.example.codemind.data.model.ProjectFile

object BundledProjects {
    val projectFlaskApi = listOf(
        ProjectFile(
            name = "app.py",
            path = "flask_app/app.py",
            content = """
from flask import Flask, request, jsonify
from auth import verify_token, authenticate_user
import database

app = Flask(__name__)

@app.route("/api/login", methods=["POST"])
def login():
    data = request.json
    # Security flaw: missing None check on data
    user = data['username']
    password = data['password']
    token = authenticate_user(user, password)
    return jsonify({"token": token})

@app.route("/api/users", methods=["GET"])
def get_users():
    auth_header = request.headers.get("Authorization")
    if not verify_token(auth_header):
        return jsonify({"error": "Unauthorized"}), 401
    return jsonify(database.fetch_all_users())

if __name__ == "__main__":
    app.run(debug=True)
""".trimIndent()
        ),
        ProjectFile(
            name = "auth.py",
            path = "flask_app/auth.py",
            content = """
import hashlib
import time

SECRET_KEY = "hardcoded_super_secret_key_123" # Security: hardcoded secret

def authenticate_user(username, password):
    # Weak hash algorithm: MD5 is vulnerable to collision attacks
    hashed = hashlib.md5(password.encode()).hexdigest()
    # Missing SQL param binding
    return "token_" + username + "_" + str(time.time())

def verify_token(token):
    if not token:
        return False
    return token.startswith("token_")
""".trimIndent()
        ),
        ProjectFile(
            name = "database.py",
            path = "flask_app/database.py",
            content = """
import sqlite3

def get_connection():
    return sqlite3.connect("production.db")

def fetch_all_users():
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT id, username, email FROM users")
    rows = cursor.fetchall()
    # Resource leak: connection is never closed
    return [{"id": r[0], "username": r[1], "email": r[2]} for r in rows]
""".trimIndent()
        ),
        ProjectFile(
            name = "requirements.txt",
            path = "flask_app/requirements.txt",
            content = """
flask==2.3.2
pytest==7.4.0
requests==2.28.1
""".trimIndent()
        ),
        ProjectFile(
            name = "test_auth.py",
            path = "flask_app/tests/test_auth.py",
            content = """
import pytest
from auth import verify_token

def test_verify_token_valid():
    assert verify_token("token_alice_12345") is True

def test_verify_token_empty():
    assert verify_token("") is False

def test_verify_token_none():
    assert verify_token(None) is False
""".trimIndent()
        )
    )

    val projectMlPipeline = listOf(
        ProjectFile(
            name = "pipeline.py",
            path = "ml_project/pipeline.py",
            content = """
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier

def run_ml_pipeline(csv_path):
    df = pd.read_csv(csv_path)
    # Potential KeyError if target column missing
    X = df.drop(columns=["target"])
    y = df["target"]
    
    # Missing random_state in split causes non-reproducible runs
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)
    
    model = RandomForestClassifier(n_estimators=100)
    model.fit(X_train, y_train)
    acc = model.score(X_test, y_test)
    print("Model accuracy:", acc)
    return model
""".trimIndent()
        ),
        ProjectFile(
            name = "preprocessing.py",
            path = "ml_project/preprocessing.py",
            content = """
import numpy as np

def impute_missing_features(matrix):
    # Vectorized check: nan values handled
    matrix = np.nan_to_num(matrix, nan=0.0)
    return matrix

def normalize_min_max(vector):
    min_v = np.min(vector)
    max_v = np.max(vector)
    # Zero division risk if all values identical
    return (vector - min_v) / (max_v - min_v)
""".trimIndent()
        ),
        ProjectFile(
            name = "requirements.txt",
            path = "ml_project/requirements.txt",
            content = """
pandas>=2.0.0
numpy>=1.24.0
scikit-learn>=1.3.0
matplotlib>=3.7.0
""".trimIndent()
        )
    )
}
