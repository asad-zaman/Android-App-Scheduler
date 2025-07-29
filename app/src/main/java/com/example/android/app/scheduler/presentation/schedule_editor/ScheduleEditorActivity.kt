package com.example.android.app.scheduler.presentation.schedule_editor

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.android.app.scheduler.R
import com.example.android.app.scheduler.core.Constant
import com.example.android.app.scheduler.core.getFormattedDateTime
import com.example.android.app.scheduler.databinding.ActivitySchduleEditorBinding
import com.example.android.app.scheduler.domain.model.ScheduleInfo
import com.example.android.app.scheduler.domain.model.ScheduleStatus
import com.example.android.app.scheduler.presentation.app_picker.AppPickerActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class ScheduleEditorActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySchduleEditorBinding
    private var scheduleInfo: ScheduleInfo? = null
    private lateinit var calendar: Calendar

    private val viewModel: ScheduleEditorViewModel by viewModels()

    private val appPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val packageName = result.data?.getStringExtra(Constant.PACKAGE_NAME) ?: return@registerForActivityResult

            scheduleInfo = ScheduleInfo(
                packageName = packageName,
                timeInMillis = calendar.timeInMillis,
                status = ScheduleStatus.PENDING,
            )

            initScheduleInformation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySchduleEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewWithToolBar()
        initObservable()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun initViewWithToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.create_update_schedule)

        calendar = Calendar.getInstance()
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        scheduleInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(Constant.SCHEDULE_INFO, ScheduleInfo::class.java)
        } else {
            intent.getSerializableExtra(Constant.SCHEDULE_INFO) as? ScheduleInfo
        }

        if(scheduleInfo != null) {
            binding.appIcon.isEnabled = false
        }
        initScheduleInformation()

        binding.dateTimeInputLayout.setEndIconOnClickListener {
            showDateTimePicker {
                binding.fab.isEnabled = true
                val scheduleDateTime = calendar.timeInMillis.getFormattedDateTime()
                binding.dateTimeEditText.setText(scheduleDateTime)
            }
        }

        binding.appIcon.setOnClickListener {
            navigateToAppPicker()
        }

        binding.fab.setOnClickListener {
            scheduleInfo?.let {
                if(it.id == null) {
                    val newScheduleInfo = it.copy(timeInMillis = calendar.timeInMillis)
                    viewModel.scheduleApp(newScheduleInfo) {
                        this@ScheduleEditorActivity.finish()
                    }
                } else {
                    val newScheduleInfo = it.copy(timeInMillis = calendar.timeInMillis)
                    viewModel.rescheduleApp(newScheduleInfo)  {
                        this@ScheduleEditorActivity.finish()
                    }
                }
            }
        }
    }

    private fun initScheduleInformation() {
        scheduleInfo?.let {
            binding.dateTimeEditText.setText(it.timeInMillis.getFormattedDateTime())
            viewModel.getAppInfo(it.packageName)?.let { appInfo ->
                binding.appName.text = appInfo.appName
                binding.appIcon.setImageDrawable(appInfo.appIcon)
            }

            calendar.timeInMillis = it.timeInMillis
        }
    }

    private fun showDateTimePicker(onDateTimeSelected: () -> Unit) {
        DatePickerDialog(
            this@ScheduleEditorActivity,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                showTimePicker(onDateTimeSelected)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = calendar.timeInMillis
        }.show()
    }

    private fun showTimePicker(onDateTimeSelected: () -> Unit) {
        TimePickerDialog(
            this@ScheduleEditorActivity,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                onDateTimeSelected()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun initObservable() {
        viewModel.loading.observe(this) { isLoading ->
            binding.loadingIndicator.root.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.message.observe(this) { message ->
            Toast.makeText(this@ScheduleEditorActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToAppPicker() {
        Intent(this@ScheduleEditorActivity, AppPickerActivity::class.java).apply {
            appPickerLauncher.launch(this)
        }
    }
}