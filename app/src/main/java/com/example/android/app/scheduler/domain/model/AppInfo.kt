package com.example.android.app.scheduler.domain.model

import android.graphics.drawable.Drawable
import java.io.Serializable

data class AppInfo(
    val appName: String,
    val appIcon: Drawable,
    val packageName: String,
): Serializable
