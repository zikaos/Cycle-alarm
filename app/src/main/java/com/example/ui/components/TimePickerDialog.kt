package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.SlatePillSelected
import com.example.ui.theme.TextWhite
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTimePickerDialog(
    initialTime: LocalTime,
    title: String,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("time_picker_dialog"),
        shape = RoundedCornerShape(28.dp),
        containerColor = ImmersiveDarkBg,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = LavenderAccent
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = TextWhite
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = Color.White.copy(alpha = 0.05f),
                        clockDialSelectedContentColor = DeepVioletOnAccent,
                        clockDialUnselectedContentColor = TextWhite,
                        selectorColor = LavenderAccent,
                        periodSelectorBorderColor = Color.White.copy(alpha = 0.15f),
                        periodSelectorSelectedContainerColor = SlatePillSelected,
                        periodSelectorUnselectedContainerColor = Color.White.copy(alpha = 0.04f),
                        periodSelectorSelectedContentColor = TextWhite,
                        periodSelectorUnselectedContentColor = Color.White.copy(alpha = 0.6f),
                        timeSelectorSelectedContainerColor = SlatePillSelected,
                        timeSelectorUnselectedContainerColor = Color.White.copy(alpha = 0.04f),
                        timeSelectorSelectedContentColor = TextWhite,
                        timeSelectorUnselectedContentColor = Color.White.copy(alpha = 0.6f)
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(LocalTime.of(timePickerState.hour, timePickerState.minute))
                    onDismiss()
                },
                modifier = Modifier.testTag("confirm_time_button")
            ) {
                Text("Confirm", fontWeight = FontWeight.Bold, color = LavenderAccent)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_time_button")
            ) {
                Text("Cancel", color = Color.White.copy(alpha = 0.6f))
            }
        }
    )
}
