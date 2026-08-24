package com.example

import com.example.model.CalculationMode
import com.example.util.SleepCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

class SleepCalculatorTest {

    @Test
    fun testSleepNowForwardCalculation() {
        // Sleep at 11:30 PM (23:30) with 10 min buffer
        val bedtime = LocalTime.of(23, 30)
        val bufferMinutes = 10
        val results = SleepCalculator.calculateCycles(
            mode = CalculationMode.SLEEP_NOW,
            referenceTime = bedtime,
            bufferMinutes = bufferMinutes
        )

        assertEquals(5, results.size) // 3, 4, 5, 6, 7 cycles

        // 3 cycles: 23:30 + 10m buffer + 270m = 04:10 AM
        val cycle3 = results.first { it.cycles == 3 }
        assertEquals(LocalTime.of(4, 10), cycle3.targetTime)
        assertFalse(cycle3.isRecommended)

        // 5 cycles: 23:30 + 10m buffer + 450m (7.5h) = 07:10 AM
        val cycle5 = results.first { it.cycles == 5 }
        assertEquals(LocalTime.of(7, 10), cycle5.targetTime)
        assertTrue(cycle5.isRecommended)
        assertEquals("7h 30m", cycle5.sleepDurationFormatted)
        assertEquals("7h 40m", cycle5.totalDurationFormatted)

        // 6 cycles: 23:30 + 10m buffer + 540m (9h) = 08:40 AM
        val cycle6 = results.first { it.cycles == 6 }
        assertEquals(LocalTime.of(8, 40), cycle6.targetTime)
        assertTrue(cycle6.isRecommended)
    }

    @Test
    fun testWakeAtReverseCalculation() {
        // Wake up at 7:00 AM (07:00) with 10 min buffer
        val wakeTime = LocalTime.of(7, 0)
        val bufferMinutes = 10
        val results = SleepCalculator.calculateCycles(
            mode = CalculationMode.WAKE_AT,
            referenceTime = wakeTime,
            bufferMinutes = bufferMinutes
        )

        assertEquals(5, results.size)

        // 5 cycles: 07:00 AM - 10m buffer - 450m (7.5h) = 11:20 PM previous day (23:20)
        val cycle5 = results.first { it.cycles == 5 }
        assertEquals(LocalTime.of(23, 20), cycle5.targetTime)
        assertTrue(cycle5.isRecommended)

        // 6 cycles: 07:00 AM - 10m buffer - 540m (9h) = 09:50 PM previous day (21:50)
        val cycle6 = results.first { it.cycles == 6 }
        assertEquals(LocalTime.of(21, 50), cycle6.targetTime)
        assertTrue(cycle6.isRecommended)
    }

    @Test
    fun testZeroBufferCalculation() {
        val bedtime = LocalTime.of(0, 0) // Midnight
        val results = SleepCalculator.calculateCycles(
            mode = CalculationMode.SLEEP_NOW,
            referenceTime = bedtime,
            bufferMinutes = 0
        )

        val cycle5 = results.first { it.cycles == 5 }
        assertEquals(LocalTime.of(7, 30), cycle5.targetTime)
    }
}
