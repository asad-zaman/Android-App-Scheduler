package com.example.android.app.scheduler.presentation.schedule_viewer

import com.example.android.app.scheduler.domain.model.ScheduleInfo

sealed class ScheduleViewerUiState {
    data object Loading : ScheduleViewerUiState()
    data class Success(val schedules: List<ScheduleInfo>) : ScheduleViewerUiState()
    data class Error(val message: String) : ScheduleViewerUiState()
}