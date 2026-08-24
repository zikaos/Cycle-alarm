package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.theme.AppThemeMode

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var bufferMinutes: Int
        get() = prefs.getInt(KEY_BUFFER_MINUTES, DEFAULT_BUFFER_MINUTES).coerceIn(0, 30)
        set(value) {
            prefs.edit().putInt(KEY_BUFFER_MINUTES, value.coerceIn(0, 30)).apply()
        }

    var themeMode: AppThemeMode
        get() {
            val modeName = prefs.getString(KEY_THEME_MODE, AppThemeMode.DARK.name) ?: AppThemeMode.DARK.name
            return try {
                AppThemeMode.valueOf(modeName)
            } catch (e: Exception) {
                AppThemeMode.DARK
            }
        }
        set(value) {
            prefs.edit().putString(KEY_THEME_MODE, value.name).apply()
        }

    var defaultSnoozeMinutes: Int
        get() = prefs.getInt(KEY_DEFAULT_SNOOZE_MINUTES, DEFAULT_SNOOZE_MINUTES)
        set(value) {
            prefs.edit().putInt(KEY_DEFAULT_SNOOZE_MINUTES, value).apply()
        }

    // Active Scheduled Alarm State
    var isAlarmActive: Boolean
        get() = prefs.getBoolean(KEY_IS_ALARM_ACTIVE, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_ALARM_ACTIVE, value).apply()

    var activeAlarmEpochMillis: Long
        get() = prefs.getLong(KEY_ACTIVE_ALARM_EPOCH, 0L)
        set(value) = prefs.edit().putLong(KEY_ACTIVE_ALARM_EPOCH, value).apply()

    var activeAlarmCycles: Int
        get() = prefs.getInt(KEY_ACTIVE_ALARM_CYCLES, 5)
        set(value) = prefs.edit().putInt(KEY_ACTIVE_ALARM_CYCLES, value).apply()

    var activeAlarmBufferMinutes: Int
        get() = prefs.getInt(KEY_ACTIVE_ALARM_BUFFER, 10)
        set(value) = prefs.edit().putInt(KEY_ACTIVE_ALARM_BUFFER, value).apply()

    var activeAlarmSleepMinutes: Int
        get() = prefs.getInt(KEY_ACTIVE_ALARM_SLEEP_MINUTES, 450)
        set(value) = prefs.edit().putInt(KEY_ACTIVE_ALARM_SLEEP_MINUTES, value).apply()

    var activeAlarmTargetTime: String
        get() = prefs.getString(KEY_ACTIVE_ALARM_TARGET_TIME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_ACTIVE_ALARM_TARGET_TIME, value).apply()

    var activeAlarmBedtime: String
        get() = prefs.getString(KEY_ACTIVE_ALARM_BEDTIME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_ACTIVE_ALARM_BEDTIME, value).apply()

    var activeAlarmMode: String
        get() = prefs.getString(KEY_ACTIVE_ALARM_MODE, "SLEEP_NOW") ?: "SLEEP_NOW"
        set(value) = prefs.edit().putString(KEY_ACTIVE_ALARM_MODE, value).apply()

    var snoozeCount: Int
        get() = prefs.getInt(KEY_SNOOZE_COUNT, 0)
        set(value) = prefs.edit().putInt(KEY_SNOOZE_COUNT, value).apply()

    // Pending Rating Feedback (Triggered when alarm is dismissed)
    var hasPendingRating: Boolean
        get() = prefs.getBoolean(KEY_HAS_PENDING_RATING, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_PENDING_RATING, value).apply()

    var pendingRatingCycles: Int
        get() = prefs.getInt(KEY_PENDING_RATING_CYCLES, 5)
        set(value) = prefs.edit().putInt(KEY_PENDING_RATING_CYCLES, value).apply()

    var pendingRatingBuffer: Int
        get() = prefs.getInt(KEY_PENDING_RATING_BUFFER, 10)
        set(value) = prefs.edit().putInt(KEY_PENDING_RATING_BUFFER, value).apply()

    var pendingRatingSleepMinutes: Int
        get() = prefs.getInt(KEY_PENDING_RATING_SLEEP_MINUTES, 450)
        set(value) = prefs.edit().putInt(KEY_PENDING_RATING_SLEEP_MINUTES, value).apply()

    var pendingRatingBedtime: String
        get() = prefs.getString(KEY_PENDING_RATING_BEDTIME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_PENDING_RATING_BEDTIME, value).apply()

    var pendingRatingWakeTime: String
        get() = prefs.getString(KEY_PENDING_RATING_WAKE_TIME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_PENDING_RATING_WAKE_TIME, value).apply()

    var pendingRatingSnoozeCount: Int
        get() = prefs.getInt(KEY_PENDING_RATING_SNOOZE_COUNT, 0)
        set(value) = prefs.edit().putInt(KEY_PENDING_RATING_SNOOZE_COUNT, value).apply()

    fun clearActiveAlarm() {
        prefs.edit()
            .putBoolean(KEY_IS_ALARM_ACTIVE, false)
            .putLong(KEY_ACTIVE_ALARM_EPOCH, 0L)
            .putInt(KEY_SNOOZE_COUNT, 0)
            .apply()
    }

    fun clearPendingRating() {
        prefs.edit().putBoolean(KEY_HAS_PENDING_RATING, false).apply()
    }

    companion object {
        private const val PREFS_NAME = "cycle_alarm_prefs"
        private const val KEY_BUFFER_MINUTES = "fall_asleep_buffer_minutes"
        private const val KEY_THEME_MODE = "app_theme_mode"
        private const val KEY_DEFAULT_SNOOZE_MINUTES = "default_snooze_minutes"

        private const val KEY_IS_ALARM_ACTIVE = "is_alarm_active"
        private const val KEY_ACTIVE_ALARM_EPOCH = "active_alarm_epoch"
        private const val KEY_ACTIVE_ALARM_CYCLES = "active_alarm_cycles"
        private const val KEY_ACTIVE_ALARM_BUFFER = "active_alarm_buffer"
        private const val KEY_ACTIVE_ALARM_SLEEP_MINUTES = "active_alarm_sleep_minutes"
        private const val KEY_ACTIVE_ALARM_TARGET_TIME = "active_alarm_target_time"
        private const val KEY_ACTIVE_ALARM_BEDTIME = "active_alarm_bedtime"
        private const val KEY_ACTIVE_ALARM_MODE = "active_alarm_mode"
        private const val KEY_SNOOZE_COUNT = "snooze_count"

        private const val KEY_HAS_PENDING_RATING = "has_pending_rating"
        private const val KEY_PENDING_RATING_CYCLES = "pending_rating_cycles"
        private const val KEY_PENDING_RATING_BUFFER = "pending_rating_buffer"
        private const val KEY_PENDING_RATING_SLEEP_MINUTES = "pending_rating_sleep_minutes"
        private const val KEY_PENDING_RATING_BEDTIME = "pending_rating_bedtime"
        private const val KEY_PENDING_RATING_WAKE_TIME = "pending_rating_wake_time"
        private const val KEY_PENDING_RATING_SNOOZE_COUNT = "pending_rating_snooze_count"

        const val DEFAULT_BUFFER_MINUTES = 10
        const val DEFAULT_SNOOZE_MINUTES = 10
    }
}
