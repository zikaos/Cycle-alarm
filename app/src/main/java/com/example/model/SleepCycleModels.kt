package com.example.model

import java.time.LocalTime

enum class CalculationMode {
    SLEEP_NOW,
    WAKE_AT
}

data class SleepCycleResult(
    val cycles: Int,
    val targetTime: LocalTime,
    val referenceTime: LocalTime,
    val sleepMinutes: Int,
    val bufferMinutes: Int,
    val totalMinutes: Int,
    val isRecommended: Boolean,
    val sleepDurationFormatted: String,
    val totalDurationFormatted: String,
    val cycleTag: String,
    val timeFormatted: String,
    val timePeriod: String,
    val timelineSubtitle: String,
    val mode: CalculationMode
)
