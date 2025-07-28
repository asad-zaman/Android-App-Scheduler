package com.example.android.app.scheduler.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.app.scheduler.domain.model.ScheduleStatus

@Entity
data class ScheduleEntity(
    val packageName: String,
    val timeInMillis: Long,
    val status: ScheduleStatus = ScheduleStatus.PENDING,
    @PrimaryKey val id: Int? = null
)

