package com.example.android.app.scheduler.domain.repository

import com.example.android.app.scheduler.domain.model.ScheduleInfo
import com.example.android.app.scheduler.domain.model.ScheduleStatus
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

interface ScheduleRepository {
    fun insert(scheduleInfo: ScheduleInfo): Completable
    fun getAllSchedules(): Flowable<List<ScheduleInfo>>
    fun updateSchedule(scheduleId: Int, timeInMillis: Long): Completable
    fun updateStatus(scheduleId: Int, status: ScheduleStatus): Completable
    fun scheduleAlarm(scheduleInfo: ScheduleInfo)
}