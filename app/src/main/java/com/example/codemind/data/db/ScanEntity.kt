package com.example.codemind.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val timestamp: Long = System.currentTimeMillis(),
    val codePreview: String,
    val totalIssues: Int,
    val criticalIssues: Int,
    val status: String, // "CLEAN", "ISSUES_FOUND", "FIXED", "VALIDATED"
    val fullCode: String,
    val fixedCode: String? = null,
    val analysisNotes: String
)
