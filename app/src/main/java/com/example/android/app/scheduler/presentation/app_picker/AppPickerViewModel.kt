package com.example.android.app.scheduler.presentation.app_picker

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScheduleViewerViewModel @Inject constructor(
    val packageManager: PackageManager
): ViewModel() {
}