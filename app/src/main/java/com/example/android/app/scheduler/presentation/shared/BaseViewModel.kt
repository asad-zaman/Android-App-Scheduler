package com.example.android.app.scheduler.presentation.shared

import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.app.scheduler.core.getAppInfoByPackage
import com.example.android.app.scheduler.domain.model.AppInfo
import io.reactivex.rxjava3.disposables.CompositeDisposable

open class BaseViewModel(private val packageManager: PackageManager): ViewModel() {
    protected val disposables = CompositeDisposable()

    protected val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    protected val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun getAppInfo(packageName: String): AppInfo? =
        packageManager.getAppInfoByPackage(packageName)

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}