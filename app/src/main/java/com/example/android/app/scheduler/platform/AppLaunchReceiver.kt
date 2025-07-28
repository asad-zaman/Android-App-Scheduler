package com.example.android.app.scheduler.platform

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.android.app.scheduler.core.Constant
import com.example.android.app.scheduler.di.ScheduleEntryPoint
import com.example.android.app.scheduler.domain.model.ScheduleStatus
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class AppLaunchReceiver: BroadcastReceiver() {
    @SuppressLint("CheckResult")
    override fun onReceive(context: Context?, intent: Intent?) {
        val packageName = intent?.getStringExtra(Constant.PACKAGE_NAME) ?: return
        val scheduleId = intent.getIntExtra(Constant.SCHEDULE_ID, -1)

        context?.let {
            context.packageManager.getLaunchIntentForPackage(packageName)?.let { launchIntent ->
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                PendingIntent.getActivity(
                    it,
                    0,
                    launchIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                ).apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        send(
                            ActivityOptions.makeBasic().apply {
                                setPendingIntentBackgroundActivityStartMode(
                                    ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
                                )
                            }.toBundle()
                        )
                    } else {
                        send()
                    }
                }
            }

            EntryPointAccessors.fromApplication(
                context.applicationContext,
                ScheduleEntryPoint::class.java
            ).scheduleRepository().apply {
                updateStatus(scheduleId, ScheduleStatus.EXECUTED)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        { Log.d("Schedule", "Status updated successfully") },
                        { Log.e("Schedule", "Failed to update status: ${it.message}") }
                    )
            }
        }
    }
}