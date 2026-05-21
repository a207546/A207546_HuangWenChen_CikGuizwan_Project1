package com.example.a207546_huangwenchen_cikguizwan

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthRecordDao {
    @Insert
    suspend fun insert(record: HealthRecordEntity)

    @Query("SELECT * FROM health_records ORDER BY id DESC")
    fun getAllRecords(): Flow<List<HealthRecordEntity>>
}