package com.example.android.app.scheduler.presentation.app_picker

import android.content.Intent
import android.content.pm.PackageManager
import com.example.android.app.scheduler.domain.model.AppInfo
import com.example.android.app.scheduler.presentation.schedule_viewer.ScheduleViewerUiState
import com.example.android.app.scheduler.presentation.shared.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AppPickerViewModel @Inject constructor(
    private val packageManager: PackageManager
): BaseViewModel(packageManager) {
    private val _uiState = MutableStateFlow<AppPickerUiState>(AppPickerUiState.Loading)
    val uiState: StateFlow<AppPickerUiState> = _uiState

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        disposables.add(
            Observable.fromCallable { getInstalledApps() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { _uiState.value = AppPickerUiState.Success(it) },
                    { _uiState.value = AppPickerUiState.Error("Failed to load installed apps") }
                )
        )
    }

    private fun getInstalledApps(): List<AppInfo> {
        return Intent(Intent.ACTION_MAIN, null)
            .apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }.run {
                packageManager.queryIntentActivities(this, PackageManager.MATCH_ALL).map { resolveInfo ->
                    AppInfo(
                        appName = resolveInfo.activityInfo.loadLabel(packageManager).toString(),
                        appIcon = resolveInfo.activityInfo.loadIcon(packageManager),
                        packageName = resolveInfo.activityInfo.packageName
                    )
                }.sortedBy { it.appName }
            }
    }
}