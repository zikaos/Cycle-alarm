package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepLogDao {
    @Query("SELECT * FROM sleep_logs ORDER BY timestamp DESC")
    fun getAllSleepLogs(): Flow<List<SleepLog>>

    @Query("SELECT * FROM sleep_logs WHERE cycles = :cycles ORDER BY timestamp DESC")
    fun getSleepLogsByCycles(cycles: Int): Flow<List<SleepLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepLog(log: SleepLog): Long

    @Update
    suspend fun updateSleepLog(log: SleepLog)

    @Delete
    suspend fun deleteSleepLog(log: SleepLog)

    @Query("DELETE FROM sleep_logs WHERE id = :id")
    suspend fun deleteSleepLogById(id: Long)

    @Query("SELECT * FROM sleep_logs WHERE id = :id LIMIT 1")
    suspend fun getSleepLogById(id: Long): SleepLog?
}
