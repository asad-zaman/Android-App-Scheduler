package com.example.android.app.scheduler.presentation.schedule_viewer

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.android.app.scheduler.R
import com.example.android.app.scheduler.core.getAppInfoByPackage
import com.example.android.app.scheduler.core.getFormattedDateTime
import com.example.android.app.scheduler.databinding.ItemScheduleInfoBinding
import com.example.android.app.scheduler.domain.model.ScheduleInfo
import com.example.android.app.scheduler.domain.model.ScheduleStatus

class ScheduleInfoHolder(val binding: ItemScheduleInfoBinding): ViewHolder(binding.root) {

    fun bindingScheduleInfo(scheduleInfo: ScheduleInfo) {
        binding.scheduleTime.text = scheduleInfo.timeInMillis.getFormattedDateTime()

        val drawableId = when(scheduleInfo.status) {
            ScheduleStatus.PENDING -> R.drawable.ic_pending
            ScheduleStatus.EXECUTED -> R.drawable.ic_check
            ScheduleStatus.CANCELLED -> R.drawable.ic_cancel
        }

        ContextCompat.getDrawable(binding.root.context, drawableId).apply {
            binding.scheduleStatus.setImageDrawable(this)
        }

        binding.root.context.packageManager.getAppInfoByPackage(scheduleInfo.packageName)?.let { appInfo ->
            binding.appName.text = appInfo.appName
            binding.appIcon.setImageDrawable(appInfo.appIcon)
        }

        binding.moreOptions.visibility =
            if(scheduleInfo.status == ScheduleStatus.PENDING) View.VISIBLE else View.INVISIBLE
    }
}