package com.example.android.app.scheduler.presentation.schedule_editor

import android.content.pm.PackageManager
import com.example.android.app.scheduler.domain.model.ScheduleInfo
import com.example.android.app.scheduler.domain.repository.ScheduleRepository
import com.example.android.app.scheduler.presentation.shared.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ScheduleEditorViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    packageManager: PackageManager
): BaseViewModel(packageManager) {
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
}