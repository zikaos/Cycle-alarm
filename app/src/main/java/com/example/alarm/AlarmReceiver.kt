package com.example.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.data.PreferencesManager

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefs = PreferencesManager(context)

        when (intent.action) {
            AlarmScheduler.ACTION_TRIGGER_ALARM -> {
                val serviceIntent = Intent(context, AlarmService::class.java)
                ContextCompat.startForegroundService(context, serviceIntent)
            }

            AlarmScheduler.ACTION_SNOOZE_ALARM -> {
                // Stop ringing service
                val serviceIntent = Intent(context, AlarmService::class.java)
                context.stopService(serviceIntent)

                val snoozeMinutes = intent.getIntExtra(
                    AlarmScheduler.EXTRA_SNOOZE_MINUTES,
                    prefs.defaultSnoozeMinutes
                )

                AlarmScheduler.scheduleSnooze(context, snoozeMinutes)

                Toast.makeText(
                    context,
                    "💤 Alarm snoozed for $snoozeMinutes minutes",
                    Toast.LENGTH_SHORT
                ).show()
            }

            AlarmScheduler.ACTION_DISMISS_ALARM -> {
                // Stop ringing service
                val serviceIntent = Intent(context, AlarmService::class.java)
                context.stopService(serviceIntent)

                // Save pending rating info from the active alarm before clearing it
                if (prefs.isAlarmActive) {
                    prefs.hasPendingRating = true
                    prefs.pendingRatingCycles = prefs.activeAlarmCycles
                    prefs.pendingRatingBuffer = prefs.activeAlarmBufferMinutes
                    prefs.pendingRatingSleepMinutes = prefs.activeAlarmSleepMinutes
                    prefs.pendingRatingBedtime = prefs.activeAlarmBedtime
                    prefs.pendingRatingWakeTime = prefs.activeAlarmTargetTime
                    prefs.pendingRatingSnoozeCount = prefs.snoozeCount
                }

                AlarmScheduler.cancelAlarm(context)

                // Launch main activity to prompt sleep quality feedback
                val mainIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("EXTRA_SHOW_FEEDBACK", true)
                }
                context.startActivity(mainIntent)
            }
        }
    }
}
