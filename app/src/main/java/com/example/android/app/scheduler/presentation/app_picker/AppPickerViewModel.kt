package com.example.android.app.scheduler.presentation.app_picker

import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.app.scheduler.domain.model.AppInfo
import com.example.android.app.scheduler.presentation.shared.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class AppPickerViewModel @Inject constructor(
    private val packageManager: PackageManager
): BaseViewModel(packageManager) {
    private val _apps = MutableLiveData<List<AppInfo>>()
    val apps: LiveData<List<AppInfo>> = _apps

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        disposables.add(
            Observable.fromCallable { getInstalledApps() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _loading.value  = true }
                .doOnTerminate { _loading.value = false }
                .subscribe(
                    {
                        _apps.value = it
                    },
                    { _message.value = "Failed to load installed apps" }
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