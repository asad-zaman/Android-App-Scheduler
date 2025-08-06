package com.example.android.app.scheduler.presentation.schedule_editor

import android.content.pm.PackageManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.android.app.scheduler.core.Constant
import com.example.android.app.scheduler.domain.model.AppInfo
import com.example.android.app.scheduler.domain.model.ScheduleInfo
import com.example.android.app.scheduler.domain.repository.ScheduleRepository
import com.example.android.app.scheduler.presentation.shared.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ScheduleEditorViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    packageManager: PackageManager,
    savedStateHandle: SavedStateHandle,
): BaseViewModel(packageManager) {

    private val _selectedPackageName = MutableStateFlow<String?>(null)
    val selectedPackageName: StateFlow<String?> = _selectedPackageName

    private val _scheduleInfo = MutableStateFlow<ScheduleInfo?>(null)
    val scheduleInfo: StateFlow<ScheduleInfo?> = _scheduleInfo

    init {
        savedStateHandle.getStateFlow<String?>(Constant.PACKAGE_NAME, null)
            .onEach { _selectedPackageName.value = it }
            .launchIn(viewModelScope)

        savedStateHandle.getStateFlow<ScheduleInfo?>(Constant.SCHEDULE_INFO, null)
            .onEach { _scheduleInfo.value = it }
            .launchIn(viewModelScope)
    }

    fun setPackageName(packageName: String?) {
        _selectedPackageName.value = packageName
    }

    fun scheduleApp(scheduleInfo: ScheduleInfo, onnSuccess: () -> Unit) {
        disposables.add(scheduleRepository.checkScheduleConflict(scheduleInfo.timeInMillis)
            .flatMapCompletable { hasConflict ->
                if (hasConflict) {
                    Completable.error(Throwable("Schedule conflict at this time"))
                } else {
                    scheduleRepository.insert(scheduleInfo)
                        .flatMapCompletable { id ->
                            val scheduledWithId = scheduleInfo.copy(id = id.toInt())
                            Completable.fromAction {
                                scheduleRepository.scheduleAlarm(scheduledWithId)
                            }
                        }
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _loading.value = true }
            .doOnTerminate { _loading.value = false }
            .subscribe(
                {
                    _message.value = "App scheduled successfully"
                    onnSuccess()
                },
                { _message.value = it.message ?: "Failed to schedule app" }
            )
        )
    }

    fun rescheduleApp(scheduleInfo: ScheduleInfo, onnSuccess: () -> Unit) {
        disposables.add(scheduleRepository.checkScheduleConflict(scheduleInfo.timeInMillis)
            .flatMapCompletable { hasConflict ->
                if (hasConflict) {
                    Completable.error(Throwable("Reschedule conflict at this time"))
                } else {
                    scheduleRepository.updateSchedule(scheduleInfo.id!!, scheduleInfo.timeInMillis)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _loading.value = true }
            .doOnTerminate { _loading.value = false }
            .subscribe(
                {
                    scheduleRepository.rescheduleAlarm(scheduleInfo)
                    _message.value = "Reschedule app successfully"
                    onnSuccess()
                },
                { _message.value = it.message ?: "Failed to reschedule app" }
            )
        )
    }

    fun isEditing() = _scheduleInfo.value != null
}