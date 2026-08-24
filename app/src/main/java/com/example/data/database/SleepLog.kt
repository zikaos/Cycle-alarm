package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_logs")
data class SleepLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val rating: Int, // 1 to 5 stars
    val notes: String = "",
    val cycles: Int, // e.g. 3, 4, 5, 6, 7
    val bufferMinutes: Int, // e.g. 10
    val sleepMinutes: Int, // total minutes of sleep e.g. 450 (7h 30m)
    val bedtimeFormatted: String, // e.g. "11:15 PM"
    val wakeTimeFormatted: String, // e.g. "07:00 AM"
    val calculationMode: String = "SLEEP_NOW",
    val snoozeCount: Int = 0,
    val feelingTags: String = "" // comma separated tags e.g. "Refreshed,Instant Wakeup"
)
