package com.example.android.app.scheduler.presentation.app_picker

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.android.app.scheduler.databinding.ItemAppInfoBinding
import com.example.android.app.scheduler.domain.model.AppInfo

class AppInfoHolder(private val itemBinding: ItemAppInfoBinding): ViewHolder(itemBinding.root) {
    fun bindAppInfo(appInfo: AppInfo) {
        itemBinding.appIcon.setImageDrawable(appInfo.appIcon)
        itemBinding.appName.text = appInfo.appName
        itemBinding.packageName.text = appInfo.packageName
    }
}