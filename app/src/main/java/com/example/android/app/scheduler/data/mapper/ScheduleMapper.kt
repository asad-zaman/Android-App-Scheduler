package com.example.android.app.scheduler.data.mapper

import com.example.android.app.scheduler.data.local.ScheduleEntity
import com.example.android.app.scheduler.domain.model.ScheduleInfo

fun ScheduleEntity.toScheduleInfo(): ScheduleInfo {
    return ScheduleInfo(
        id = id,
        packageName = packageName,
        timeInMillis = timeInMillis,
        status = status,
    )
}

fun ScheduleInfo.toScheduleEntity(): ScheduleEntity {
    return ScheduleEntity(
        packageName = packageName,
        timeInMillis = timeInMillis,
        status = status,
    )
}