package com.example.android.app.scheduler.core

import android.content.Intent
import android.content.pm.PackageManager
import com.example.android.app.scheduler.domain.model.AppInfo
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun PackageManager.getAppInfoByPackage(packageName: String): AppInfo? =
    /*Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
        setPackage(packageName)
    }.let { intent ->
        queryIntentActivities(intent, 0)
    }.firstOrNull()?.let { activityInfo ->
        AppInfo(
            appName = activityInfo.loadLabel(this).toString(),
            appIcon = activityInfo.loadIcon(this),
            packageName = packageName
        )
    }*/

    try {
        val appInfo = getApplicationInfo(packageName, 0)
        val appName = getApplicationLabel(appInfo).toString()
        val appIcon = getApplicationIcon(appInfo)

        AppInfo(
            appName = appName,
            appIcon = appIcon,
            packageName = packageName,
        )
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }

fun Long.getFormattedDateTime(pattern: String = Constant.YYYY_MM_DD_HH_MM_A): String =
    DateTimeFormatter
        .ofPattern(pattern)
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochMilli(this))