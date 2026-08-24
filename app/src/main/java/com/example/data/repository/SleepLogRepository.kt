package com.example.data.repository

import com.example.data.database.SleepLog
import com.example.data.database.SleepLogDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class CycleCorrelation(
    val cycles: Int,
    val count: Int,
    val averageRating: Double,
    val totalSleepMinutes: Int,
    val averageBufferMinutes: Double
)

data class SleepPatternInsights(
    val totalLogs: Int,
    val bestCycleCount: Int?,
    val bestCycleAvgRating: Double,
    val cycleCorrelations: List<CycleCorrelation>,
    val averageOverallRating: Double,
    val totalSnoozesRecorded: Int
)

class SleepLogRepository(private val sleepLogDao: SleepLogDao) {
    val allLogs: Flow<List<SleepLog>> = sleepLogDao.getAllSleepLogs()

    val insights: Flow<SleepPatternInsights> = allLogs.map { logs ->
        calculateInsights(logs)
    }

    suspend fun insertLog(log: SleepLog): Long = sleepLogDao.insertSleepLog(log)

    suspend fun updateLog(log: SleepLog) = sleepLogDao.updateSleepLog(log)

    suspend fun deleteLog(log: SleepLog) = sleepLogDao.deleteSleepLog(log)

    suspend fun deleteLogById(id: Long) = sleepLogDao.deleteSleepLogById(id)

    private fun calculateInsights(logs: List<SleepLog>): SleepPatternInsights {
        if (logs.isEmpty()) {
            return SleepPatternInsights(
                totalLogs = 0,
                bestCycleCount = null,
                bestCycleAvgRating = 0.0,
                cycleCorrelations = emptyList(),
                averageOverallRating = 0.0,
                totalSnoozesRecorded = 0
            )
        }

        val totalLogs = logs.size
        val totalSnoozes = logs.sumOf { it.snoozeCount }
        val overallAvgRating = logs.map { it.rating }.average()

        val grouped = logs.groupBy { it.cycles }
        val allCycleOptions = listOf(3, 4, 5, 6, 7)

        val correlations = allCycleOptions.map { cycleNum ->
            val logsForCycle = grouped[cycleNum] ?: emptyList()
            val count = logsForCycle.size
            val avgRating = if (count > 0) logsForCycle.map { it.rating }.average() else 0.0
            val avgBuffer = if (count > 0) logsForCycle.map { it.bufferMinutes }.average() else 10.0
            CycleCorrelation(
                cycles = cycleNum,
                count = count,
                averageRating = avgRating,
                totalSleepMinutes = cycleNum * 90,
                averageBufferMinutes = avgBuffer
            )
        }

        // Find best cycle count among cycles with at least 1 log
        val bestCycle = correlations
            .filter { it.count > 0 }
            .maxByOrNull { it.averageRating }

        return SleepPatternInsights(
            totalLogs = totalLogs,
            bestCycleCount = bestCycle?.cycles,
            bestCycleAvgRating = bestCycle?.averageRating ?: 0.0,
            cycleCorrelations = correlations,
            averageOverallRating = overallAvgRating,
            totalSnoozesRecorded = totalSnoozes
        )
    }
}
