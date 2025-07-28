package com.example.android.app.scheduler.di

import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Room
import com.example.android.app.scheduler.data.local.ScheduleDao
import com.example.android.app.scheduler.data.local.ScheduleDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ScheduleDatabase =
        Room.databaseBuilder(
            context,
            ScheduleDatabase::class.java,
            "schedule_database",
        ).build()

    @Provides
    @Singleton
    fun provideScheduleDao(db: ScheduleDatabase): ScheduleDao =
        db.scheduleDao()

    @Provides
    @Singleton
    fun providePackageManager(@ApplicationContext context: Context): PackageManager =
        context.packageManager

    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

}