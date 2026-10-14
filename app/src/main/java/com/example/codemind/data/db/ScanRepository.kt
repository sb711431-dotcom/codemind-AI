package com.example.codemind.data.db

import kotlinx.coroutines.flow.Flow

class ScanRepository(private val scanDao: ScanDao) {
    val allScans: Flow<List<ScanEntity>> = scanDao.getAllScans()

    suspend fun saveScan(scan: ScanEntity): Long {
        return scanDao.insertScan(scan)
    }

    suspend fun deleteScan(id: Long) {
        scanDao.deleteScan(id)
    }

    suspend fun clearHistory() {
        scanDao.clearAll()
    }
}
