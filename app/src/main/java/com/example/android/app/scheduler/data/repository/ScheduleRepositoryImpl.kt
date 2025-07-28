package com.example.android.app.scheduler.data.repository

import com.example.android.app.scheduler.data.local.ScheduleDao
import com.example.android.app.scheduler.domain.model.ScheduleInfo
import com.example.android.app.scheduler.domain.model.ScheduleStatus
import com.example.android.app.scheduler.domain.repository.ScheduleRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    val scheduleDao: ScheduleDao
): ScheduleRepository {
    override fun insert(scheduleInfo: ScheduleInfo): Completable {
        TODO("Not yet implemented")
    }

    override fun getAllSchedules(): Flowable<List<ScheduleInfo>> {
        TODO("Not yet implemented")
    }

    override fun updateSchedule(scheduleId: Int, timeInMillis: Long): Completable {
        TODO("Not yet implemented")
    }

    override fun updateStatus(scheduleId: Int, status: ScheduleStatus): Completable {
        TODO("Not yet implemented")
    }

    override fun scheduleAlarm(scheduleInfo: ScheduleInfo) {
        TODO("Not yet implemented")
    }
}