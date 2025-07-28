package com.example.android.app.scheduler.data.local

import androidx.room.TypeConverter
import com.example.android.app.scheduler.domain.model.ScheduleStatus

class Converters {
    @TypeConverter
    fun fromScheduleStatus(value: ScheduleStatus): String = value.name

    @TypeConverter
    fun toScheduleStatus(value: String): ScheduleStatus = ScheduleStatus.valueOf(value)
}