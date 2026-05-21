package com.example.a207546_huangwenchen_cikguizwan

import kotlinx.coroutines.flow.Flow

class HealthRepository(private val dao: HealthRecordDao) {
    fun allRecords(): Flow<List<HealthRecordEntity>> = dao.getAllRecords()

    suspend fun addRecord(record: HealthRecordEntity) {
        dao.insert(record)
    }
}