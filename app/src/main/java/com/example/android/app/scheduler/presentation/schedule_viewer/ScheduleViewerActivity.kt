package com.example.android.app.scheduler.presentation.schedule_viewer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels

@AndroidEntryPoint
class ScheduleViewerActivity: AppCompatActivity() {
    private val viewModel: ScheduleViewerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}