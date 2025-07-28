package com.example.android.app.scheduler.domain.model

data class ScheduleInfo(
    val id: Int,
    val packageName: String,
    val timeInMillis: Long,
    val status: ScheduleStatus
)
