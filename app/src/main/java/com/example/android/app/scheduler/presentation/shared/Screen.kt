package com.example.android.app.scheduler.presentation.shared

sealed class Screen(val route: String) {
    data object ScheduleViewer: Screen("schedule_viewer_screen")
    data object ScheduleEditor: Screen("schedule_editor_screen")
    data object AppPicker: Screen("app_picker_screen")
}