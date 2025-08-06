package com.example.android.app.scheduler.domain.model

import com.example.android.app.scheduler.R
import java.io.Serializable

data class ScheduleInfo(
    val packageName: String,
    val timeInMillis: Long,
    val status: ScheduleStatus,
    val id: Int? = null,
): Serializable {
    val drawableId = when(status) {
        ScheduleStatus.PENDING -> R.drawable.ic_pending
        ScheduleStatus.EXECUTED -> R.drawable.ic_check
        ScheduleStatus.CANCELLED -> R.drawable.ic_cancel
    }
}
