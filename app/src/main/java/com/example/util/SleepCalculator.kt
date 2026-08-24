package com.example.util

import com.example.model.CalculationMode
import com.example.model.SleepCycleResult
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object SleepCalculator {
    private const val MINUTES_PER_CYCLE = 90
    val CYCLE_COUNTS = listOf(3, 4, 5, 6, 7)

    private val TIME_12H_FORMATTER = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
    private val TIME_ONLY_FORMATTER = DateTimeFormatter.ofPattern("h:mm", Locale.US)
    private val AM_PM_FORMATTER = DateTimeFormatter.ofPattern("a", Locale.US)

    fun formatTime(time: LocalTime): String = time.format(TIME_12H_FORMATTER)
    fun formatTimeOnly(time: LocalTime): String = time.format(TIME_ONLY_FORMATTER)
    fun formatAmPm(time: LocalTime): String = time.format(AM_PM_FORMATTER)

    fun formatDuration(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val mins = totalMinutes % 60
        return if (mins > 0) "${hours}h ${mins}m" else "${hours}h"
    }

    fun calculateCycles(
        mode: CalculationMode,
        referenceTime: LocalTime,
        bufferMinutes: Int
    ): List<SleepCycleResult> {
        val safeBuffer = bufferMinutes.coerceIn(0, 30)

        return CYCLE_COUNTS.map { cycles ->
            val sleepMinutes = cycles * MINUTES_PER_CYCLE
            val totalMinutes = sleepMinutes + safeBuffer
            val isRecommended = cycles == 5 || cycles == 6

            val (bedtime, wakeTime, targetTime) = when (mode) {
                CalculationMode.SLEEP_NOW -> {
                    val bedtime = referenceTime
                    val wakeTime = referenceTime.plusMinutes(totalMinutes.toLong())
                    Triple(bedtime, wakeTime, wakeTime)
                }
                CalculationMode.WAKE_AT -> {
                    val wakeTime = referenceTime
                    val bedtime = referenceTime.minusMinutes(totalMinutes.toLong())
                    Triple(bedtime, wakeTime, bedtime)
                }
            }

            val cycleTag = when (cycles) {
                3 -> "Short Rest · 4.5h Sleep"
                4 -> "Power Sleep · 6.0h Sleep"
                5 -> "Recommended · Sweet Spot"
                6 -> "Recommended · Deep Recovery"
                7 -> "Long Rest · Extended Sleep"
                else -> "$cycles Cycles"
            }

            val bufferNote = if (safeBuffer > 0) "incl. ${safeBuffer}m to fall asleep" else "instant sleep"
            val timelineSubtitle = "${formatTime(bedtime)} → ${formatTime(wakeTime)} · ${formatDuration(totalMinutes)} ($bufferNote)"

            SleepCycleResult(
                cycles = cycles,
                targetTime = targetTime,
                referenceTime = referenceTime,
                sleepMinutes = sleepMinutes,
                bufferMinutes = safeBuffer,
                totalMinutes = totalMinutes,
                isRecommended = isRecommended,
                sleepDurationFormatted = formatDuration(sleepMinutes),
                totalDurationFormatted = formatDuration(totalMinutes),
                cycleTag = cycleTag,
                timeFormatted = formatTimeOnly(targetTime),
                timePeriod = formatAmPm(targetTime),
                timelineSubtitle = timelineSubtitle,
                mode = mode
            )
        }
    }
}
