package com.example.android.app.scheduler.domain.repository

import com.example.android.app.scheduler.domain.model.ScheduleInfo
import com.example.android.app.scheduler.domain.model.ScheduleStatus
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface ScheduleRepository {
    fun insert(scheduleInfo: ScheduleInfo): Single<Long>
    fun getAllSchedules(): Flowable<List<ScheduleInfo>>
    fun updateSchedule(scheduleId: Int, timeInMillis: Long): Completable
    fun updateStatus(scheduleId: Int, status: ScheduleStatus): Completable
    fun scheduleAlarm(scheduleInfo: ScheduleInfo)
    fun rescheduleAlarm(scheduleInfo: ScheduleInfo)
    fun cancelAlarm(scheduleInfo: ScheduleInfo)

    fun checkScheduleConflict(timeInMillis: Long): Single<Boolean>
}