package com.example.android.app.scheduler.di

import com.example.android.app.scheduler.domain.repository.ScheduleRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ScheduleEntryPoint {
    fun scheduleRepository(): ScheduleRepository
}