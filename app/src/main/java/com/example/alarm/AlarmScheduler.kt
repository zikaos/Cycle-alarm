package com.example.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.example.MainActivity
import com.example.data.PreferencesManager

object AlarmScheduler {
    const val ACTION_TRIGGER_ALARM = "com.example.alarm.ACTION_TRIGGER_ALARM"
    const val ACTION_SNOOZE_ALARM = "com.example.alarm.ACTION_SNOOZE_ALARM"
    const val ACTION_DISMISS_ALARM = "com.example.alarm.ACTION_DISMISS_ALARM"
    const val EXTRA_SNOOZE_MINUTES = "extra_snooze_minutes"

    private const val ALARM_REQUEST_CODE = 1001

    fun scheduleAlarm(
        context: Context,
        epochMillis: Long,
        cycles: Int,
        bufferMinutes: Int,
        sleepMinutes: Int,
        bedtime: String,
        wakeTime: String,
        mode: String
    ): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return false
        val prefs = PreferencesManager(context)

        // Save active alarm state
        prefs.isAlarmActive = true
        prefs.activeAlarmEpochMillis = epochMillis
        prefs.activeAlarmCycles = cycles
        prefs.activeAlarmBufferMinutes = bufferMinutes
        prefs.activeAlarmSleepMinutes = sleepMinutes
        prefs.activeAlarmBedtime = bedtime
        prefs.activeAlarmTargetTime = wakeTime
        prefs.activeAlarmMode = mode
        prefs.snoozeCount = 0

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val showPendingIntent = PendingIntent.getActivity(
            context,
            0,
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(epochMillis, showPendingIntent),
                    pendingIntent
                )
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, epochMillis, pendingIntent)
            }
            return true
        } catch (e: SecurityException) {
            try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, epochMillis, pendingIntent)
                return true
            } catch (e2: Exception) {
                return false
            }
        } catch (e: Exception) {
            return false
        }
    }

    fun scheduleSnooze(context: Context, snoozeMinutes: Int): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return false
        val prefs = PreferencesManager(context)

        val snoozeEpoch = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000L)
        prefs.isAlarmActive = true
        prefs.activeAlarmEpochMillis = snoozeEpoch
        prefs.snoozeCount = prefs.snoozeCount + 1

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val showPendingIntent = PendingIntent.getActivity(
            context,
            0,
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(snoozeEpoch, showPendingIntent),
                    pendingIntent
                )
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozeEpoch, pendingIntent)
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }

        PreferencesManager(context).clearActiveAlarm()
    }
}
