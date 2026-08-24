package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.SleepLog
import com.example.ui.theme.DeepVioletOnAccent
import com.example.ui.theme.ImmersiveDarkBg
import com.example.ui.theme.LavenderAccent
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SleepFeedbackDialog(
    initialCycles: Int = 5,
    initialBuffer: Int = 10,
    initialSleepMinutes: Int = 450,
    initialBedtime: String = "11:00 PM",
    initialWakeTime: String = "07:00 AM",
    initialSnoozeCount: Int = 0,
    onSaveFeedback: (SleepLog) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rating by remember { mutableIntStateOf(5) }
    var notes by remember { mutableStateOf("") }
    var selectedCycles by remember { mutableIntStateOf(initialCycles) }
    var selectedTags by remember { mutableStateOf(setOf("Refreshed", "Instant Wake")) }

    val availableTags = listOf(
        "⚡ Energized",
        "✨ Instant Wake",
        "🧘 Refreshed",
        "☕ Slight Grogginess",
        "🥱 Tired",
        "💤 Deep Sleep",
        "🌙 Vivid Dreams",
        "⏰ Snoozed"
    )

    val ratingLabels = listOf(
        "Exhausted",
        "Groggy",
        "Okay",
        "Refreshed",
        "Energized!"
    )

    val ratingIcons = listOf(
        Icons.Default.SentimentVeryDissatisfied,
        Icons.Default.SentimentDissatisfied,
        Icons.Default.SentimentNeutral,
        Icons.Default.SentimentSatisfied,
        Icons.Default.SentimentVerySatisfied
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("sleep_feedback_dialog"),
        shape = RoundedCornerShape(28.dp),
        containerColor = ImmersiveDarkBg,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LavenderAccent,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = null,
                            tint = DeepVioletOnAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "How did you sleep?",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        ),
                        color = TextWhite
                    )
                    Text(
                        text = "Rate your wake-up energy",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Session Specs Badges
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.04f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SESSION USED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp
                                ),
                                color = LavenderAccent
                            )
                            Text(
                                text = "$selectedCycles Cycles • ${selectedCycles * 90 / 60}h ${(selectedCycles * 90) % 60}m",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = TextWhite
                            )
                        }

                        if (initialSnoozeCount > 0) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.White.copy(alpha = 0.08f)
                            ) {
                                Text(
                                    text = "$initialSnoozeCount Snooze${if (initialSnoozeCount > 1) "s" else ""}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = LavenderAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Cycle selector chips if user wants to adjust logged cycles
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "CYCLES SLEPT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White.copy(alpha = 0.5f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(3, 4, 5, 6, 7).forEach { c ->
                            val isSelected = selectedCycles == c
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) LavenderAccent else Color.White.copy(alpha = 0.05f)
                                    )
                                    .clickable { selectedCycles = c }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${c}x",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) DeepVioletOnAccent else TextWhite
                                )
                            }
                        }
                    }
                }

                // 1-5 Star & Emotion Rating
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (starIndex in 1..5) {
                            val isFilled = starIndex <= rating
                            Icon(
                                imageVector = if (isFilled) Icons.Default.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Rate $starIndex stars",
                                tint = if (isFilled) LavenderAccent else Color.White.copy(alpha = 0.3f),
                                modifier = Modifier
                                    .size(38.dp)
                                    .clickable { rating = starIndex }
                                    .testTag("star_rating_$starIndex")
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = ratingIcons[rating - 1],
                            contentDescription = null,
                            tint = LavenderAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = ratingLabels[rating - 1],
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = LavenderAccent
                        )
                    }
                }

                // Feeling Tags
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "HOW DO YOU FEEL?",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White.copy(alpha = 0.5f)
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        availableTags.forEach { tag ->
                            val cleanTagName = tag.substringAfter(" ")
                            val isSelected = selectedTags.contains(cleanTagName)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedTags = if (isSelected) {
                                        selectedTags - cleanTagName
                                    } else {
                                        selectedTags + cleanTagName
                                    }
                                },
                                label = {
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LavenderAccent.copy(alpha = 0.25f),
                                    selectedLabelColor = LavenderAccent,
                                    containerColor = Color.White.copy(alpha = 0.04f),
                                    labelColor = Color.White.copy(alpha = 0.6f)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isSelected) LavenderAccent.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f),
                                    enabled = true,
                                    selected = isSelected
                                ),
                                shape = RoundedCornerShape(50)
                            )
                        }
                    }
                }

                // Notes input
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "OPTIONAL NOTES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White.copy(alpha = 0.5f)
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = {
                            Text(
                                "e.g., Felt sharp immediately, no afternoon crash...",
                                color = Color.White.copy(alpha = 0.35f),
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("feedback_notes_input"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LavenderAccent,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedContainerColor = Color.White.copy(alpha = 0.03f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val log = SleepLog(
                        timestamp = System.currentTimeMillis(),
                        rating = rating,
                        notes = notes.trim(),
                        cycles = selectedCycles,
                        bufferMinutes = initialBuffer,
                        sleepMinutes = selectedCycles * 90,
                        bedtimeFormatted = initialBedtime,
                        wakeTimeFormatted = initialWakeTime,
                        snoozeCount = initialSnoozeCount,
                        feelingTags = selectedTags.joinToString(",")
                    )
                    onSaveFeedback(log)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LavenderAccent,
                    contentColor = DeepVioletOnAccent
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("save_feedback_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Feedback", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_feedback_button")
            ) {
                Text("Skip", color = Color.White.copy(alpha = 0.6f))
            }
        }
    )
}
