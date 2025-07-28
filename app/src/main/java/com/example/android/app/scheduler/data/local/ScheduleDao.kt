package com.example.android.app.scheduler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.app.scheduler.domain.model.ScheduleStatus
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(schedule: ScheduleEntity): Completable

    @Delete
    fun delete(schedule: ScheduleEntity): Completable

    @Query("SELECT * FROM ScheduleEntity ORDER BY timeInMillis ASC")
    fun getAllSchedules(): Flowable<List<ScheduleEntity>>

    @Query("UPDATE ScheduleEntity SET status = :status WHERE id = :scheduleId")
    fun updateStatus(scheduleId: Int, status: ScheduleStatus): Completable

    @Query("UPDATE ScheduleEntity SET timeInMillis = :timeInMillis WHERE id = :scheduleId")
    fun updateSchedule(scheduleId: Int, timeInMillis: Long): Completable
}