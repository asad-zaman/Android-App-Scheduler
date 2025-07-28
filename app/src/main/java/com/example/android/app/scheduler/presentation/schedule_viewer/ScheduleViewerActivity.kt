package com.example.android.app.scheduler.presentation.schedule_viewer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import com.example.android.app.scheduler.R
import com.example.android.app.scheduler.core.Constant
import com.example.android.app.scheduler.databinding.ActivitySchduleViewerBinding
import com.example.android.app.scheduler.domain.model.ScheduleInfo
import com.example.android.app.scheduler.presentation.schedule_editor.ScheduleEditorActivity

@AndroidEntryPoint
class ScheduleViewerActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySchduleViewerBinding
    private lateinit var adapter: ScheduleInfoAdapter

    private val viewModel: ScheduleViewerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySchduleViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initObservable()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.schedule_list)

        adapter = ScheduleInfoAdapter { view,  scheduleInfo ->
            showPopupMenu(view, scheduleInfo)
        }

        binding.rvScheduleInfo.adapter = adapter

        binding.fab.setOnClickListener {
            navigateToScheduleEditor()
        }
    }

    private fun showPopupMenu(view: View, scheduleInfo: ScheduleInfo) {
        PopupMenu(this, view).apply {
            menuInflater.inflate(R.menu.schedule_item_menu, menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.actionEdit -> {
                        navigateToScheduleEditor(scheduleInfo)
                        true
                    }
                    R.id.actionCancel -> {
                        viewModel.cancelApp(scheduleInfo)
                        true
                    }
                    else -> false
                }
            }
        }.show()
    }


    private fun initObservable() {
        viewModel.loading.observe(this) { isLoading ->
            binding.loadingIndicator.root.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.message.observe(this) { message ->
            Toast.makeText(this@ScheduleViewerActivity, message, Toast.LENGTH_SHORT).show()
        }

        viewModel.schedules.observe(this) { schedules ->
            adapter.submitList(schedules)
        }
    }

    private fun navigateToScheduleEditor(scheduleInfo: ScheduleInfo? = null) {
        Intent(this@ScheduleViewerActivity, ScheduleEditorActivity::class.java).apply {
            putExtra(Constant.SCHEDULE_INFO, scheduleInfo)
            startActivity(this)
        }
    }
}