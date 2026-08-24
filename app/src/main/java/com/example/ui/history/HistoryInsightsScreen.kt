package com.example.ui.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.database.SleepLog
import com.example.data.repository.CycleCorrelation
import com.example.data.repository.SleepPatternInsights
import com.example.ui.theme.DeepVioletOnAccent
import com.example.ui.theme.LavenderAccent
import com.example.ui.theme.MintRecommended
import com.example.ui.theme.MintRecommendedContainer
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistoryInsightsScreen(
    logs: List<SleepLog>,
    insights: SleepPatternInsights,
    onOpenFeedbackDialog: () -> Unit,
    onDeleteLog: (SleepLog) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Ideal Sleep Pattern Card
            item {
                IdealPatternHeroCard(
                    insights = insights,
                    onLogNow = onOpenFeedbackDialog
                )
            }

            // Cycle Correlations Matrix
            item {
                CyclePerformanceCard(
                    correlations = insights.cycleCorrelations,
                    bestCycle = insights.bestCycleCount
                )
            }

            // History Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 4.dp, end = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WAKE-UP HISTORY (${logs.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.8.sp,
                            fontSize = 11.sp
                        ),
                        color = Color.White.copy(alpha = 0.4f)
                    )

                    Button(
                        onClick = onOpenFeedbackDialog,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LavenderAccent.copy(alpha = 0.2f),
                            contentColor = LavenderAccent
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("log_feedback_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Log Feedback",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // List of Logs or Empty Placeholder
            if (logs.isEmpty()) {
                item {
                    EmptyHistoryPlaceholder(onLogFirst = onOpenFeedbackDialog)
                }
            } else {
                items(
                    items = logs,
                    key = { it.id }
                ) { log ->
                    SleepLogCard(
                        log = log,
                        onDelete = { onDeleteLog(log) }
                    )
                }
            }
        }
    }
}

@Composable
private fun IdealPatternHeroCard(
    insights: SleepPatternInsights,
    onLogNow: () -> Unit
) {
    val hasData = insights.totalLogs > 0 && insights.bestCycleCount != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ideal_pattern_hero_card"),
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(
            1.5.dp,
            if (hasData) LavenderAccent.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f)
        ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (hasData) {
                        Brush.linearGradient(
                            listOf(
                                LavenderAccent.copy(alpha = 0.15f),
                                Color.White.copy(alpha = 0.03f)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(
                                Color.White.copy(alpha = 0.05f),
                                Color.White.copy(alpha = 0.02f)
                            )
                        )
                    }
                )
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (hasData) LavenderAccent else Color.White.copy(alpha = 0.1f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (hasData) Icons.Default.EmojiEvents else Icons.Default.Insights,
                                    contentDescription = null,
                                    tint = if (hasData) DeepVioletOnAccent else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Text(
                            text = "IDEAL SLEEP PATTERN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp,
                                fontSize = 11.sp
                            ),
                            color = LavenderAccent
                        )
                    }

                    if (hasData) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = LavenderAccent.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Based on ${insights.totalLogs} logs",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp
                                ),
                                color = LavenderAccent,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                if (hasData) {
                    val best = insights.bestCycleCount!!
                    val durationHours = best * 90 / 60
                    val durationMins = (best * 90) % 60
                    val durationStr = if (durationMins == 0) "${durationHours}h" else "${durationHours}h ${durationMins}m"

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "$best Cycles ($durationStr)",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                ),
                                color = TextWhite
                            )

                            Text(
                                text = String.format(Locale.US, "★ %.1f / 5.0", insights.bestCycleAvgRating),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = LavenderAccent,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }

                        Text(
                            text = "Your wake-up ratings show you feel most energized after $best full sleep cycles. Try targeting $durationStr of sleep with your buffer.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp
                            ),
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Identify Your Perfect Sleep Duration",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            color = TextWhite
                        )
                        Text(
                            text = "Rate your morning energy after waking up. Cycle Alarm will correlate your ratings with each sleep cycle count to identify your personal sweet spot!",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            ),
                            color = Color.White.copy(alpha = 0.6f)
                        )

                        Button(
                            onClick = onLogNow,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LavenderAccent,
                                contentColor = DeepVioletOnAccent
                            ),
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .testTag("start_logging_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Log First Wake-Up Rating",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CyclePerformanceCard(
    correlations: List<CycleCorrelation>,
    bestCycle: Int?
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cycle_performance_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "CYCLE RATING CORRELATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    fontSize = 11.sp
                ),
                color = Color.White.copy(alpha = 0.5f)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                correlations.forEach { item ->
                    val isBest = bestCycle != null && item.cycles == bestCycle && item.count > 0
                    val hasLogs = item.count > 0
                    val sleepH = item.totalSleepMinutes / 60
                    val sleepM = item.totalSleepMinutes % 60
                    val timeStr = if (sleepM == 0) "${sleepH}h" else "${sleepH}h ${sleepM}m"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isBest) LavenderAccent.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.02f)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isBest) LavenderAccent else Color.White.copy(alpha = 0.06f)
                            ) {
                                Text(
                                    text = "${item.cycles}x",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isBest) DeepVioletOnAccent else Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "$timeStr sleep",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isBest) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = TextWhite
                                )
                                Text(
                                    text = if (hasLogs) "${item.count} log${if (item.count > 1) "s" else ""}" else "No logs yet",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = Color.White.copy(alpha = 0.4f)
                                )
                            }
                        }

                        if (hasLogs) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = LavenderAccent,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = String.format(Locale.US, "%.1f", item.averageRating),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = if (isBest) LavenderAccent else TextWhite
                                )
                                if (isBest) {
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = LavenderAccent.copy(alpha = 0.2f),
                                        modifier = Modifier.padding(start = 4.dp)
                                    ) {
                                        Text(
                                            text = "BEST",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 9.sp
                                            ),
                                            color = LavenderAccent,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "—",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SleepLogCard(
    log: SleepLog,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("EEE, MMM d • hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(log.timestamp) { dateFormat.format(Date(log.timestamp)) }
    val durationHours = log.sleepMinutes / 60
    val durationMins = log.sleepMinutes % 60
    val durationStr = if (durationMins == 0) "${durationHours}h" else "${durationHours}h ${durationMins}m"

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.White.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sleep_log_card_${log.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Date + Stars + Delete Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp
                        ),
                        color = Color.White.copy(alpha = 0.45f)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (i <= log.rating) LavenderAccent else Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "${log.rating}.0",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = LavenderAccent,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete log",
                        tint = Color.White.copy(alpha = 0.35f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Specs badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = LavenderAccent.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${log.cycles} Cycles ($durationStr)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = LavenderAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.06f)
                ) {
                    Text(
                        text = "${log.bufferMinutes}m buffer",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp
                        ),
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                if (log.snoozeCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = 0.06f)
                    ) {
                        Text(
                            text = "💤 ${log.snoozeCount} Snooze${if (log.snoozeCount > 1) "s" else ""}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.5.sp
                            ),
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Tags
            if (log.feelingTags.isNotBlank()) {
                val tags = log.feelingTags.split(",").filter { it.isNotBlank() }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.04f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Notes
            if (log.notes.isNotBlank()) {
                Text(
                    text = "\"${log.notes}\"",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp
                    ),
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyHistoryPlaceholder(
    onLogFirst: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.White.copy(alpha = 0.02f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Bedtime,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(40.dp)
            )

            Text(
                text = "No sleep logs recorded yet",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White.copy(alpha = 0.7f)
            )

            Text(
                text = "When your alarm rings and you wake up, rate your morning feeling to track your sleep trends!",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp),
                color = Color.White.copy(alpha = 0.4f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
