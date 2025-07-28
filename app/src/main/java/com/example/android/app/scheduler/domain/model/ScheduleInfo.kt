package com.example.android.app.scheduler.domain.model

import java.io.Serializable

data class ScheduleInfo(
    val packageName: String,
    val timeInMillis: Long,
    val status: ScheduleStatus,
    val id: Int? = null,
): Serializable
