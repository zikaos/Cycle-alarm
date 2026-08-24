package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculationMode
import com.example.model.SleepCycleResult
import com.example.ui.theme.DeepVioletOnAccent
import com.example.ui.theme.LavenderAccent
import com.example.ui.theme.TextWhite

@Composable
fun CycleCard(
    result: SleepCycleResult,
    onSetAlarm: (SleepCycleResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val isRecommended = result.isRecommended

    val cardBorder = if (isRecommended) {
        BorderStroke(1.5.dp, LavenderAccent.copy(alpha = 0.45f))
    } else {
        BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    }

    val cardBackground = if (isRecommended) {
        Brush.linearGradient(
            listOf(
                LavenderAccent.copy(alpha = 0.12f),
                Color.White.copy(alpha = 0.03f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.04f),
                Color.White.copy(alpha = 0.02f)
            )
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cycle_card_${result.cycles}"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = cardBorder
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBackground)
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header with Cycle count pill, recommendation badge, and duration
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
                            shape = RoundedCornerShape(50),
                            color = if (isRecommended) LavenderAccent.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f)
                        ) {
                            Text(
                                text = "${result.cycles} CYCLES",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.8.sp
                                ),
                                color = if (isRecommended) LavenderAccent else Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        if (isRecommended) {
                            Text(
                                text = "RECOMMENDED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    fontSize = 10.5.sp
                                ),
                                color = LavenderAccent
                            )
                        }
                    }

                    Text(
                        text = "${result.sleepDurationFormatted} Sleep",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White.copy(alpha = 0.45f)
                    )
                }

                // Center row: Big time presentation and Action button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = result.timeFormatted,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 32.sp,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = TextWhite
                            )
                            Text(
                                text = result.timePeriod,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp
                                ),
                                color = Color.White.copy(alpha = 0.55f),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        Text(
                            text = if (result.mode == CalculationMode.SLEEP_NOW) "Wake-up time" else "Bedtime target",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp
                            ),
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }

                    if (isRecommended) {
                        Button(
                            onClick = { onSetAlarm(result) },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LavenderAccent,
                                contentColor = DeepVioletOnAccent
                            ),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("set_alarm_button_${result.cycles}")
                        ) {
                            Icon(
                                imageVector = if (result.mode == CalculationMode.SLEEP_NOW) Icons.Default.Alarm else Icons.Default.Bedtime,
                                contentDescription = null,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Set Alarm",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                        }
                    } else {
                        Button(
                            onClick = { onSetAlarm(result) },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.08f),
                                contentColor = TextWhite
                            ),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("set_alarm_button_${result.cycles}")
                        ) {
                            Icon(
                                imageVector = if (result.mode == CalculationMode.SLEEP_NOW) Icons.Default.Alarm else Icons.Default.Bedtime,
                                contentDescription = null,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Set Alarm",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.5.sp
                            )
                        }
                    }
                }

                // Visual cycle bars & timeline subtitle
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.03f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(result.cycles) { index ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (isRecommended) {
                                            Brush.horizontalGradient(
                                                listOf(
                                                    LavenderAccent.copy(alpha = 0.6f),
                                                    LavenderAccent.copy(alpha = 0.9f)
                                                )
                                            )
                                        } else {
                                            Brush.horizontalGradient(
                                                listOf(
                                                    Color.White.copy(alpha = 0.2f),
                                                    Color.White.copy(alpha = 0.4f)
                                                )
                                            )
                                        }
                                    )
                            )
                        }
                    }

                    Text(
                        text = result.timelineSubtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp
                        ),
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}
