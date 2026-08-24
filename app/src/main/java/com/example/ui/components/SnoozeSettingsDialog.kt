package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepVioletOnAccent
import com.example.ui.theme.ImmersiveDarkBg
import com.example.ui.theme.LavenderAccent
import com.example.ui.theme.TextWhite

@Composable
fun SnoozeSettingsDialog(
    currentSnoozeMinutes: Int,
    onSnoozeSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMinutes by remember { mutableIntStateOf(currentSnoozeMinutes) }

    val options = listOf(
        5 to "5 Minutes (Quick Rest)",
        10 to "10 Minutes (Standard Default)",
        15 to "15 Minutes (Micro Sleep)",
        20 to "20 Minutes (Power Nap)",
        90 to "90 Minutes (1 Full Sleep Cycle)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("snooze_settings_dialog"),
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
                            imageVector = Icons.Default.Snooze,
                            contentDescription = null,
                            tint = DeepVioletOnAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "Snooze Duration",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = TextWhite
                    )
                    Text(
                        text = "Choose default snooze time",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { (minutes, label) ->
                    val isSelected = selectedMinutes == minutes
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) LavenderAccent.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.04f),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) LavenderAccent else Color.White.copy(alpha = 0.08f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMinutes = minutes }
                            .testTag("snooze_option_$minutes")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) LavenderAccent else TextWhite
                            )

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = LavenderAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSnoozeSelected(selectedMinutes)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LavenderAccent,
                    contentColor = DeepVioletOnAccent
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("save_snooze_button")
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_snooze_button")
            ) {
                Text("Cancel", color = Color.White.copy(alpha = 0.6f))
            }
        }
    )
}
