package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarm.AlarmScheduler
import com.example.data.PreferencesManager
import com.example.data.database.AppDatabase
import com.example.data.database.SleepLog
import com.example.data.repository.SleepLogRepository
import com.example.data.repository.SleepPatternInsights
import com.example.model.CalculationMode
import com.example.model.SleepCycleResult
import com.example.ui.theme.AppThemeMode
import com.example.util.AlarmLauncher
import com.example.util.SleepCalculator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

enum class AppTab {
    CALCULATOR,
    HISTORY
}

data class CycleAlarmUiState(
    val mode: CalculationMode = CalculationMode.SLEEP_NOW,
    val sleepNowTime: LocalTime = LocalTime.now().withSecond(0).withNano(0),
    val desiredWakeTime: LocalTime = LocalTime.of(7, 0),
    val bufferMinutes: Int = PreferencesManager.DEFAULT_BUFFER_MINUTES,
    val defaultSnoozeMinutes: Int = PreferencesManager.DEFAULT_SNOOZE_MINUTES,
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val selectedTab: AppTab = AppTab.CALCULATOR,
    val isTimePickerVisible: Boolean = false,
    val isScienceSheetVisible: Boolean = false,
    val isFeedbackDialogVisible: Boolean = false,
    val isSnoozeSettingsVisible: Boolean = false,
    val isAlarmRinging: Boolean = false,
    val isAlarmActive: Boolean = false,
    val activeAlarmTargetTime: String = "",
    val activeAlarmCycles: Int = 5,
    val activeAlarmBufferMinutes: Int = 10,
    val activeAlarmSleepMinutes: Int = 450,
    val activeAlarmBedtime: String = "",
    val snoozeCount: Int = 0,
    val feedbackInitialCycles: Int = 5,
    val feedbackInitialBuffer: Int = 10,
    val feedbackInitialSleepMinutes: Int = 450,
    val feedbackInitialBedtime: String = "11:00 PM",
    val feedbackInitialWakeTime: String = "07:00 AM",
    val feedbackInitialSnoozeCount: Int = 0,
    val results: List<SleepCycleResult> = emptyList()
)

class CycleAlarmViewModel(application: Application) : AndroidViewModel(application) {
    private val preferencesManager = PreferencesManager(application)
    private val database = AppDatabase.getDatabase(application)
    private val repository = SleepLogRepository(database.sleepLogDao())

    val sleepLogs: StateFlow<List<SleepLog>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val sleepInsights: StateFlow<SleepPatternInsights> = repository.insights
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SleepPatternInsights(0, null, 0.0, emptyList(), 0.0, 0)
        )

    private val _uiState = MutableStateFlow(
        CycleAlarmUiState(
            bufferMinutes = preferencesManager.bufferMinutes,
            defaultSnoozeMinutes = preferencesManager.defaultSnoozeMinutes,
            themeMode = preferencesManager.themeMode,
            isAlarmActive = preferencesManager.isAlarmActive,
            activeAlarmTargetTime = preferencesManager.activeAlarmTargetTime,
            activeAlarmCycles = preferencesManager.activeAlarmCycles,
            activeAlarmBufferMinutes = preferencesManager.activeAlarmBufferMinutes,
            activeAlarmSleepMinutes = preferencesManager.activeAlarmSleepMinutes,
            activeAlarmBedtime = preferencesManager.activeAlarmBedtime,
            snoozeCount = preferencesManager.snoozeCount
        )
    )
    val uiState: StateFlow<CycleAlarmUiState> = _uiState.asStateFlow()

    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    init {
        recalculate()
        checkForPendingRating()
    }

    fun checkForPendingRating() {
        if (preferencesManager.hasPendingRating) {
            _uiState.update {
                it.copy(
                    isFeedbackDialogVisible = true,
                    feedbackInitialCycles = preferencesManager.pendingRatingCycles,
                    feedbackInitialBuffer = preferencesManager.pendingRatingBuffer,
                    feedbackInitialSleepMinutes = preferencesManager.pendingRatingSleepMinutes,
                    feedbackInitialBedtime = preferencesManager.pendingRatingBedtime,
                    feedbackInitialWakeTime = preferencesManager.pendingRatingWakeTime,
                    feedbackInitialSnoozeCount = preferencesManager.pendingRatingSnoozeCount
                )
            }
            preferencesManager.clearPendingRating()
        }
    }

    fun setTab(tab: AppTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setMode(mode: CalculationMode) {
        _uiState.update { it.copy(mode = mode) }
        recalculate()
    }

    fun setSleepNowTimeToNow() {
        val now = LocalTime.now().withSecond(0).withNano(0)
        _uiState.update { it.copy(sleepNowTime = now) }
        recalculate()
    }

    fun updateSelectedTime(newTime: LocalTime) {
        _uiState.update { current ->
            if (current.mode == CalculationMode.SLEEP_NOW) {
                current.copy(sleepNowTime = newTime)
            } else {
                current.copy(desiredWakeTime = newTime)
            }
        }
        recalculate()
    }

    fun updateBuffer(minutes: Int) {
        val clamped = minutes.coerceIn(0, 30)
        preferencesManager.bufferMinutes = clamped
        _uiState.update { it.copy(bufferMinutes = clamped) }
        recalculate()
    }

    fun updateDefaultSnoozeMinutes(minutes: Int) {
        preferencesManager.defaultSnoozeMinutes = minutes
        _uiState.update { it.copy(defaultSnoozeMinutes = minutes) }
    }

    fun setThemeMode(mode: AppThemeMode) {
        preferencesManager.themeMode = mode
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun cycleThemeMode() {
        val nextMode = when (_uiState.value.themeMode) {
            AppThemeMode.DARK -> AppThemeMode.LIGHT
            AppThemeMode.LIGHT -> AppThemeMode.SYSTEM
            AppThemeMode.SYSTEM -> AppThemeMode.DARK
        }
        setThemeMode(nextMode)
    }

    fun setTimePickerVisibility(visible: Boolean) {
        _uiState.update { it.copy(isTimePickerVisible = visible) }
    }

    fun setScienceSheetVisibility(visible: Boolean) {
        _uiState.update { it.copy(isScienceSheetVisible = visible) }
    }

    fun setFeedbackDialogVisibility(visible: Boolean) {
        _uiState.update { it.copy(isFeedbackDialogVisible = visible) }
    }

    fun setSnoozeSettingsVisibility(visible: Boolean) {
        _uiState.update { it.copy(isSnoozeSettingsVisible = visible) }
    }

    fun setAlarmRingingState(isRinging: Boolean) {
        _uiState.update { it.copy(isAlarmRinging = isRinging) }
    }

    fun onSetAlarmClicked(result: SleepCycleResult) {
        val context = getApplication<Application>()

        // Calculate target alarm epoch timestamp
        val now = LocalDateTime.now()
        var targetDateTime = LocalDateTime.of(LocalDate.now(), result.targetTime)
        if (targetDateTime.isBefore(now) || targetDateTime.isEqual(now)) {
            targetDateTime = targetDateTime.plusDays(1)
        }
        val epochMillis = targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val bedtimeStr = if (result.mode == CalculationMode.SLEEP_NOW) {
            SleepCalculator.formatTime(result.referenceTime)
        } else {
            SleepCalculator.formatTime(result.targetTime)
        }

        val wakeTimeStr = if (result.mode == CalculationMode.SLEEP_NOW) {
            SleepCalculator.formatTime(result.targetTime)
        } else {
            SleepCalculator.formatTime(result.referenceTime)
        }

        val scheduled = AlarmScheduler.scheduleAlarm(
            context = context,
            epochMillis = epochMillis,
            cycles = result.cycles,
            bufferMinutes = result.bufferMinutes,
            sleepMinutes = result.sleepMinutes,
            bedtime = bedtimeStr,
            wakeTime = wakeTimeStr,
            mode = result.mode.name
        )

        // Launch system alarm clock
        val alarmClockResult = AlarmLauncher.launchAlarmClock(
            context = context,
            targetTime = result.targetTime,
            cycles = result.cycles,
            sleepDuration = result.sleepDurationFormatted
        )

        _uiState.update {
            it.copy(
                isAlarmActive = true,
                activeAlarmTargetTime = SleepCalculator.formatTime(result.targetTime),
                activeAlarmCycles = result.cycles,
                activeAlarmBufferMinutes = result.bufferMinutes,
                activeAlarmSleepMinutes = result.sleepMinutes,
                activeAlarmBedtime = bedtimeStr,
                snoozeCount = 0
            )
        }

        viewModelScope.launch {
            val message = if (scheduled) {
                "⏰ Alarm set for ${SleepCalculator.formatTime(result.targetTime)} (${result.cycles} cycles • ${result.sleepDurationFormatted})"
            } else {
                "Opening Clock app for ${SleepCalculator.formatTime(result.targetTime)}"
            }
            _snackbarEvents.emit(message)
        }
    }

    fun cancelActiveAlarm() {
        val context = getApplication<Application>()
        AlarmScheduler.cancelAlarm(context)
        _uiState.update {
            it.copy(
                isAlarmActive = false,
                isAlarmRinging = false,
                snoozeCount = 0
            )
        }
        viewModelScope.launch {
            _snackbarEvents.emit("Alarm cancelled")
        }
    }

    fun snoozeActiveAlarm(snoozeMinutes: Int) {
        val context = getApplication<Application>()
        AlarmScheduler.scheduleSnooze(context, snoozeMinutes)
        val newSnoozeCount = _uiState.value.snoozeCount + 1
        _uiState.update {
            it.copy(
                isAlarmRinging = false,
                snoozeCount = newSnoozeCount
            )
        }
        viewModelScope.launch {
            _snackbarEvents.emit("💤 Snoozed for $snoozeMinutes minutes")
        }
    }

    fun dismissAlarmAndShowFeedback() {
        val context = getApplication<Application>()
        val current = _uiState.value

        AlarmScheduler.cancelAlarm(context)

        _uiState.update {
            it.copy(
                isAlarmActive = false,
                isAlarmRinging = false,
                isFeedbackDialogVisible = true,
                feedbackInitialCycles = current.activeAlarmCycles,
                feedbackInitialBuffer = current.activeAlarmBufferMinutes,
                feedbackInitialSleepMinutes = current.activeAlarmSleepMinutes,
                feedbackInitialBedtime = current.activeAlarmBedtime,
                feedbackInitialWakeTime = current.activeAlarmTargetTime,
                feedbackInitialSnoozeCount = current.snoozeCount
            )
        }
    }

    fun openManualFeedbackDialog() {
        _uiState.update {
            it.copy(
                isFeedbackDialogVisible = true,
                feedbackInitialCycles = 5,
                feedbackInitialBuffer = preferencesManager.bufferMinutes,
                feedbackInitialSleepMinutes = 450,
                feedbackInitialBedtime = "11:00 PM",
                feedbackInitialWakeTime = "07:00 AM",
                feedbackInitialSnoozeCount = 0
            )
        }
    }

    fun saveSleepFeedback(log: SleepLog) {
        viewModelScope.launch {
            repository.insertLog(log)
            _snackbarEvents.emit("🌟 Wake-up rating saved! Sleep patterns updated.")
        }
    }

    fun deleteSleepLog(log: SleepLog) {
        viewModelScope.launch {
            repository.deleteLog(log)
            _snackbarEvents.emit("Sleep log removed")
        }
    }

    private fun recalculate() {
        val current = _uiState.value
        val referenceTime = if (current.mode == CalculationMode.SLEEP_NOW) {
            current.sleepNowTime
        } else {
            current.desiredWakeTime
        }
        val computedResults = SleepCalculator.calculateCycles(
            mode = current.mode,
            referenceTime = referenceTime,
            bufferMinutes = current.bufferMinutes
        )
        _uiState.update { it.copy(results = computedResults) }
    }
}
