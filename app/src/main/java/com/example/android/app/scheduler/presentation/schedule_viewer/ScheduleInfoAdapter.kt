package com.example.android.app.scheduler.presentation.schedule_viewer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.android.app.scheduler.databinding.ItemScheduleInfoBinding
import com.example.android.app.scheduler.domain.model.ScheduleInfo

class ScheduleInfoAdapter(
    private val onItemClick: (View, ScheduleInfo) -> Unit
): ListAdapter<ScheduleInfo, ScheduleInfoHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ScheduleInfo>() {
            override fun areItemsTheSame(oldItem: ScheduleInfo, newItem: ScheduleInfo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ScheduleInfo, newItem: ScheduleInfo): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleInfoHolder =
        ScheduleInfoHolder(ItemScheduleInfoBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: ScheduleInfoHolder, position: Int) {
        val scheduleInfo = getItem(position)
        holder.bindingScheduleInfo(scheduleInfo)

        holder.binding.moreOptions.setOnClickListener {
            onItemClick(it, scheduleInfo)
        }
    }
}