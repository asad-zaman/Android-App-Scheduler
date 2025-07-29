package com.example.android.app.scheduler.presentation.app_picker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.app.scheduler.databinding.ItemAppInfoBinding
import com.example.android.app.scheduler.domain.model.AppInfo

class AppInfoAdapter(
    private val appInfos: List<AppInfo>,
    private val onItemClick: (AppInfo) -> Unit
): RecyclerView.Adapter<AppInfoHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppInfoHolder {
        return AppInfoHolder(ItemAppInfoBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount() = appInfos.size

    override fun onBindViewHolder(holder: AppInfoHolder, position: Int) {
        val appInfo = appInfos[position]
        holder.bindAppInfo(appInfo)

        holder.itemView.setOnClickListener {
            onItemClick(appInfo)
        }
    }
}