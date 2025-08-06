package com.example.android.app.scheduler.presentation.shared

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.android.app.scheduler.core.Constant
import com.example.android.app.scheduler.domain.model.ScheduleInfo
import com.example.android.app.scheduler.presentation.app_picker.AppPickerScreen
import com.example.android.app.scheduler.presentation.schedule_editor.ScheduleEditorScreen
import com.example.android.app.scheduler.presentation.schedule_editor.ScheduleEditorViewModel
import com.example.android.app.scheduler.presentation.schedule_viewer.ScheduleViewerScreen

@Composable
fun NavigationStack() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.ScheduleViewer.route) {
        composable(route = Screen.ScheduleViewer.route) {
            ScheduleViewerScreen(navController = navController)
        }

        composable(route = Screen.ScheduleEditor.route) {
            val scheduleInfo = it.savedStateHandle.get<ScheduleInfo>(Constant.SCHEDULE_INFO)
            ScheduleEditorScreen(navController = navController, scheduleInfo)
        }

        composable(route = Screen.AppPicker.route) {
            AppPickerScreen(navController = navController)
        }
    }
}