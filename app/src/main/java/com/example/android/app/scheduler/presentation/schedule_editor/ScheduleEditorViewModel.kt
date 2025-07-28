package com.example.android.app.scheduler.presentation.schedule_editor

import androidx.lifecycle.ViewModel
import com.example.android.app.scheduler.domain.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScheduleEditorViewModel @Inject constructor(
    val scheduleRepository: ScheduleRepository
): ViewModel() {
}