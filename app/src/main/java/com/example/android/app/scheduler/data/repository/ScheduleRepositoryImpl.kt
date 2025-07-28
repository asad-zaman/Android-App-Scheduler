package com.example.android.app.scheduler.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.android.app.scheduler.core.Constant
import com.example.android.app.scheduler.data.local.ScheduleDao
import com.example.android.app.scheduler.data.mapper.toScheduleEntity
import com.example.android.app.scheduler.data.mapper.toScheduleInfo
import com.example.android.app.scheduler.domain.model.ScheduleInfo
import com.example.android.app.scheduler.domain.model.ScheduleStatus
import com.example.android.app.scheduler.domain.repository.ScheduleRepository
import com.example.android.app.scheduler.platform.AppLaunchReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleDao: ScheduleDao,
    private val alarmManager: AlarmManager,
    @ApplicationContext private val context: Context,
): ScheduleRepository {
    override fun insert(scheduleInfo: ScheduleInfo): Single<Long> =
        scheduleDao
            .insert(scheduleInfo.toScheduleEntity())
            .subscribeOn(Schedulers.io())

    override fun getAllSchedules(): Flowable<List<ScheduleInfo>> =
        scheduleDao
            .getAllSchedules()
            .subscribeOn(Schedulers.io())
            .map { list -> list.map { it.toScheduleInfo() } }

    override fun updateSchedule(scheduleId: Int, timeInMillis: Long): Completable =
        scheduleDao.updateSchedule(scheduleId, timeInMillis).subscribeOn(Schedulers.io())

    override fun updateStatus(scheduleId: Int, status: ScheduleStatus): Completable =
        scheduleDao.updateStatus(scheduleId, status).subscribeOn(Schedulers.io())

    override fun checkScheduleConflict(timeInMillis: Long): Single<Boolean> =
        scheduleDao.hasConflict(timeInMillis).map { it > 0 }

    override fun scheduleAlarm(scheduleInfo: ScheduleInfo) {
        getPendingIntent(scheduleInfo).apply {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                scheduleInfo.timeInMillis,
                this
            )
        }
    }

    override fun rescheduleAlarm(scheduleInfo: ScheduleInfo) {
        getPendingIntent(scheduleInfo).apply {
            alarmManager.cancel(this)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                scheduleInfo.timeInMillis,
                this
            )
        }
    }

    override fun cancelAlarm(scheduleInfo: ScheduleInfo) {
        getPendingIntent(scheduleInfo).apply {
            alarmManager.cancel(this)
        }
    }

    private fun getPendingIntent(scheduleInfo: ScheduleInfo): PendingIntent {
        return  Intent(context, AppLaunchReceiver::class.java).apply {
            putExtra(Constant.PACKAGE_NAME, scheduleInfo.packageName)
            putExtra(Constant.SCHEDULE_ID, scheduleInfo.id)
        }.run {
            PendingIntent.getBroadcast(
                context,
                scheduleInfo.id.hashCode(),
                this,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}