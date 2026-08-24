package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.CalculationMode
import com.example.ui.components.ActiveAlarmBanner
import com.example.ui.components.AlarmRingingOverlay
import com.example.ui.components.AppTimePickerDialog
import com.example.ui.components.BufferSelector
import com.example.ui.components.CycleCard
import com.example.ui.components.SleepFeedbackDialog
import com.example.ui.components.SleepScienceSheet
import com.example.ui.components.SnoozeSettingsDialog
import com.example.ui.history.HistoryInsightsScreen
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.DeepVioletOnAccent
import com.example.ui.theme.ImmersiveDarkBg
import com.example.ui.theme.ImmersiveGlowTop
import com.example.ui.theme.LavenderAccent
import com.example.ui.theme.SlatePillSelected
import com.example.ui.theme.TextWhite
import com.example.util.SleepCalculator
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CycleAlarmScreen(
    viewModel: CycleAlarmViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sleepLogs by viewModel.sleepLogs.collectAsStateWithLifecycle()
    val sleepInsights by viewModel.sleepInsights.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvents.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    if (uiState.isTimePickerVisible) {
        val selectedTime = if (uiState.mode == CalculationMode.SLEEP_NOW) {
            uiState.sleepNowTime
        } else {
            uiState.desiredWakeTime
        }
        val dialogTitle = if (uiState.mode == CalculationMode.SLEEP_NOW) {
            "Time you'll close your eyes"
        } else {
            "Desired wake-up time"
        }

        AppTimePickerDialog(
            initialTime = selectedTime,
            title = dialogTitle,
            onTimeSelected = { viewModel.updateSelectedTime(it) },
            onDismiss = { viewModel.setTimePickerVisibility(false) }
        )
    }

    if (uiState.isScienceSheetVisible) {
        SleepScienceSheet(
            onDismiss = { viewModel.setScienceSheetVisibility(false) }
        )
    }

    if (uiState.isSnoozeSettingsVisible) {
        SnoozeSettingsDialog(
            currentSnoozeMinutes = uiState.defaultSnoozeMinutes,
            onSnoozeSelected = { viewModel.updateDefaultSnoozeMinutes(it) },
            onDismiss = { viewModel.setSnoozeSettingsVisibility(false) }
        )
    }

    if (uiState.isFeedbackDialogVisible) {
        SleepFeedbackDialog(
            initialCycles = uiState.feedbackInitialCycles,
            initialBuffer = uiState.feedbackInitialBuffer,
            initialSleepMinutes = uiState.feedbackInitialSleepMinutes,
            initialBedtime = uiState.feedbackInitialBedtime,
            initialWakeTime = uiState.feedbackInitialWakeTime,
            initialSnoozeCount = uiState.feedbackInitialSnoozeCount,
            onSaveFeedback = { viewModel.saveSleepFeedback(it) },
            onDismiss = { viewModel.setFeedbackDialogVisibility(false) }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                ImmersiveTopHeader(
                    themeMode = uiState.themeMode,
                    defaultSnoozeMinutes = uiState.defaultSnoozeMinutes,
                    onToggleTheme = { viewModel.cycleThemeMode() },
                    onOpenSnoozeSettings = { viewModel.setSnoozeSettingsVisibility(true) },
                    onOpenScienceSheet = { viewModel.setScienceSheetVisibility(true) }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                ImmersiveGlowTop.copy(alpha = 0.45f),
                                ImmersiveDarkBg,
                                ImmersiveDarkBg
                            )
                        )
                    ),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App Navigation Tabs: Calculator vs History & Insights
                    MainAppTabSwitcher(
                        selectedTab = uiState.selectedTab,
                        historyCount = sleepLogs.size,
                        onTabSelected = { viewModel.setTab(it) },
                        modifier = Modifier
                            .widthIn(max = 640.dp)
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                    )

                    when (uiState.selectedTab) {
                        AppTab.CALCULATOR -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .widthIn(max = 640.dp)
                                    .padding(horizontal = 20.dp),
                                contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Active Alarm Banner if scheduled
                                if (uiState.isAlarmActive) {
                                    item {
                                        ActiveAlarmBanner(
                                            targetTime = uiState.activeAlarmTargetTime,
                                            cycles = uiState.activeAlarmCycles,
                                            snoozeMinutes = uiState.defaultSnoozeMinutes,
                                            snoozeCount = uiState.snoozeCount,
                                            onCancelAlarm = { viewModel.cancelActiveAlarm() },
                                            onConfigureSnooze = { viewModel.setSnoozeSettingsVisibility(true) }
                                        )
                                    }
                                }

                                // Mode Pill Toggle (Sleep Now vs Wake At)
                                item {
                                    ImmersivePillSwitcher(
                                        currentMode = uiState.mode,
                                        onModeSelected = { viewModel.setMode(it) }
                                    )
                                }

                                // Immersive Target Time Hero with Radial Glow
                                item {
                                    ImmersiveTimeHero(
                                        mode = uiState.mode,
                                        time = if (uiState.mode == CalculationMode.SLEEP_NOW) uiState.sleepNowTime else uiState.desiredWakeTime,
                                        onChangeTimeClicked = { viewModel.setTimePickerVisibility(true) },
                                        onSleepNowClicked = { viewModel.setSleepNowTimeToNow() }
                                    )
                                }

                                // Buffer Selector
                                item {
                                    BufferSelector(
                                        bufferMinutes = uiState.bufferMinutes,
                                        onBufferChanged = { viewModel.updateBuffer(it) }
                                    )
                                }

                                // Suggestions Section Header
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 6.dp, start = 4.dp, end = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (uiState.mode == CalculationMode.SLEEP_NOW) "WAKE-UP SUGGESTIONS" else "BEDTIME SUGGESTIONS",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 1.8.sp,
                                                fontSize = 11.sp
                                            ),
                                            color = Color.White.copy(alpha = 0.4f)
                                        )
                                        Text(
                                            text = "Incl. ${uiState.bufferMinutes}m buffer",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 11.sp
                                            ),
                                            color = Color.White.copy(alpha = 0.3f)
                                        )
                                    }
                                }

                                // List of Sleep Cycle cards
                                items(
                                    items = uiState.results,
                                    key = { "${uiState.mode}_${it.cycles}_${it.targetTime}" }
                                ) { result ->
                                    CycleCard(
                                        result = result,
                                        onSetAlarm = { viewModel.onSetAlarmClicked(it) }
                                    )
                                }

                                item {
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            }
                        }

                        AppTab.HISTORY -> {
                            HistoryInsightsScreen(
                                logs = sleepLogs,
                                insights = sleepInsights,
                                onOpenFeedbackDialog = { viewModel.openManualFeedbackDialog() },
                                onDeleteLog = { viewModel.deleteSleepLog(it) }
                            )
                        }
                    }
                }
            }
        }

        // Full Screen Ringing Overlay
        if (uiState.isAlarmRinging) {
            AlarmRingingOverlay(
                cycles = uiState.activeAlarmCycles,
                defaultSnoozeMinutes = uiState.defaultSnoozeMinutes,
                onSnooze = { minutes -> viewModel.snoozeActiveAlarm(minutes) },
                onDismissAndRate = { viewModel.dismissAlarmAndShowFeedback() }
            )
        }
    }
}

@Composable
private fun MainAppTabSwitcher(
    selectedTab: AppTab,
    historyCount: Int,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Calculator Tab
            val isCalc = selectedTab == AppTab.CALCULATOR
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(if (isCalc) SlatePillSelected else Color.Transparent)
                    .clickable { onTabSelected(AppTab.CALCULATOR) }
                    .padding(vertical = 9.dp)
                    .testTag("tab_calculator"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = if (isCalc) LavenderAccent else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Alarm & Cycles",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isCalc) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        ),
                        color = if (isCalc) TextWhite else Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            // History & Insights Tab
            val isHistory = selectedTab == AppTab.HISTORY
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(if (isHistory) SlatePillSelected else Color.Transparent)
                    .clickable { onTabSelected(AppTab.HISTORY) }
                    .padding(vertical = 9.dp)
                    .testTag("tab_history_insights"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Insights,
                        contentDescription = null,
                        tint = if (isHistory) LavenderAccent else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "History & Insights",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isHistory) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        ),
                        color = if (isHistory) TextWhite else Color.White.copy(alpha = 0.6f)
                    )

                    if (historyCount > 0) {
                        Surface(
                            shape = CircleShape,
                            color = if (isHistory) LavenderAccent else Color.White.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "$historyCount",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = if (isHistory) DeepVioletOnAccent else TextWhite,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImmersiveTopHeader(
    themeMode: AppThemeMode,
    defaultSnoozeMinutes: Int,
    onToggleTheme: () -> Unit,
    onOpenSnoozeSettings: () -> Unit,
    onOpenScienceSheet: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Logo Icon & Brand
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LavenderAccent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = DeepVioletOnAccent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "Cycle Alarm",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.2).sp,
                    fontSize = 18.sp
                ),
                color = TextWhite
            )
        }

        // Action Buttons: Snooze setting, Theme, Info
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Snooze config icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .clickable { onOpenSnoozeSettings() }
                    .testTag("top_snooze_settings_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Snooze,
                    contentDescription = "Snooze Settings (${defaultSnoozeMinutes}m)",
                    tint = LavenderAccent,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Theme toggle icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .clickable { onToggleTheme() }
                    .testTag("toggle_theme_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (themeMode) {
                        AppThemeMode.DARK -> Icons.Default.Brightness4
                        AppThemeMode.LIGHT -> Icons.Default.Brightness7
                        AppThemeMode.SYSTEM -> Icons.Default.Brightness4
                    },
                    contentDescription = "Toggle Theme",
                    tint = LavenderAccent,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Sleep Science Info icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .clickable { onOpenScienceSheet() }
                    .testTag("sleep_science_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "Sleep Science Info",
                    tint = LavenderAccent,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ImmersivePillSwitcher(
    currentMode: CalculationMode,
    onModeSelected: (CalculationMode) -> Unit
) {
    val isSleepNow = currentMode == CalculationMode.SLEEP_NOW

    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Sleep Now Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isSleepNow) SlatePillSelected else Color.Transparent
                    )
                    .clickable { onModeSelected(CalculationMode.SLEEP_NOW) }
                    .padding(vertical = 10.dp)
                    .testTag("mode_tab_SLEEP_NOW"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sleep Now",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSleepNow) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.5.sp
                    ),
                    color = if (isSleepNow) TextWhite else Color.White.copy(alpha = 0.6f)
                )
            }

            // Wake Up At Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (!isSleepNow) SlatePillSelected else Color.Transparent
                    )
                    .clickable { onModeSelected(CalculationMode.WAKE_AT) }
                    .padding(vertical = 10.dp)
                    .testTag("mode_tab_WAKE_AT"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Wake Up At",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (!isSleepNow) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.5.sp
                    ),
                    color = if (!isSleepNow) TextWhite else Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun ImmersiveTimeHero(
    mode: CalculationMode,
    time: java.time.LocalTime,
    onChangeTimeClicked: () -> Unit,
    onSleepNowClicked: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("time_hero_card"),
        shape = RoundedCornerShape(28.dp),
        color = Color.White.copy(alpha = 0.03f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 22.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (mode == CalculationMode.SLEEP_NOW) "TARGET SLEEP TIME" else "TARGET WAKE UP",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                        fontSize = 11.5.sp
                    ),
                    color = LavenderAccent
                )

                if (mode == CalculationMode.SLEEP_NOW) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = 0.06f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                        modifier = Modifier
                            .clickable { onSleepNowClicked() }
                            .testTag("sleep_now_button")
                    ) {
                        Text(
                            text = "Now",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = LavenderAccent,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Large Hero Time Display with glowing background
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onChangeTimeClicked() }
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                // Subtle ambient glow behind time
                Box(
                    modifier = Modifier
                        .size(160.dp, 60.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    LavenderAccent.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                AnimatedContent(
                    targetState = time,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "hero_time_animation"
                ) { targetTime ->
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = SleepCalculator.formatTimeOnly(targetTime),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Light,
                                fontSize = 60.sp,
                                letterSpacing = (-1).sp
                            ),
                            color = TextWhite
                        )
                        Text(
                            text = SleepCalculator.formatAmPm(targetTime),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 22.sp
                            ),
                            color = LavenderAccent,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }
            }

            // Tap hint button
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.04f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier
                    .clickable { onChangeTimeClicked() }
                    .testTag("change_time_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Tap to change time",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.5.sp
                        ),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
