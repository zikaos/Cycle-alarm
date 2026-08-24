package com.example.util

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import java.time.LocalTime

object AlarmLauncher {
    sealed class AlarmResult {
        data class Success(val message: String) : AlarmResult()
        data class Failure(val manualTime: String, val error: String) : AlarmResult()
    }

    fun launchAlarmClock(
        context: Context,
        targetTime: LocalTime,
        cycles: Int,
        sleepDuration: String
    ): AlarmResult {
        val formattedTime = SleepCalculator.formatTime(targetTime)
        val alarmLabel = "$cycles cycles ($sleepDuration) – Cycle Alarm"

        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, targetTime.hour)
            putExtra(AlarmClock.EXTRA_MINUTES, targetTime.minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, alarmLabel)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(intent)
            AlarmResult.Success("Opening Clock app for $formattedTime ($cycles cycles)")
        } catch (e: Exception) {
            AlarmResult.Failure(
                manualTime = formattedTime,
                error = "Could not open Clock app automatically. Please set an alarm manually for $formattedTime."
            )
        }
    }
}
