package com.example.android.app.scheduler.presentation.schedule_viewer

import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.app.scheduler.domain.model.AppInfo
import com.example.android.app.scheduler.domain.model.ScheduleInfo
import com.example.android.app.scheduler.domain.model.ScheduleStatus
import com.example.android.app.scheduler.domain.repository.ScheduleRepository
import com.example.android.app.scheduler.presentation.shared.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ScheduleViewerViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    packageManager: PackageManager
): BaseViewModel(packageManager) {
    private val _uiState = MutableStateFlow<ScheduleViewerUiState>(ScheduleViewerUiState.Loading)
    val uiState: StateFlow<ScheduleViewerUiState> = _uiState

    init {
        loadSchedules()
    }

    private fun loadSchedules() {
        disposables.add(
            scheduleRepository.getAllSchedules()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _uiState.value = ScheduleViewerUiState.Success(it)
                    },
                    {
                        _uiState.value = ScheduleViewerUiState.Error("Failed to load schedules")
                    }
                )
        )
    }

    fun cancelApp(scheduleInfo: ScheduleInfo) {
        disposables.add(scheduleRepository.updateStatus(scheduleInfo.id!!, ScheduleStatus.CANCELLED)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _loading.value = true }
            .doOnTerminate { _loading.value = false }
            .subscribe(
                {
                    scheduleRepository.cancelAlarm(scheduleInfo)
                    _message.value = "Cancel app successfully"
                },
                {
                    _message.value = "Failed to cancel app"
                }
            )
        )
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}