package com.example.android.app.scheduler.presentation.app_picker

import com.example.android.app.scheduler.domain.model.AppInfo

sealed class AppPickerUiState {
    data object Loading : AppPickerUiState()
    data class Success(val apps: List<AppInfo>) : AppPickerUiState()
    data class Error(val message: String) : AppPickerUiState()
}