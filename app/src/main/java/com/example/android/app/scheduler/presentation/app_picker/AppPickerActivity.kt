package com.example.android.app.scheduler.presentation.app_picker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.android.app.scheduler.R
import com.example.android.app.scheduler.core.Constant
import com.example.android.app.scheduler.databinding.ActivityAppPickerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppPickerActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAppPickerBinding

    private val viewModel: AppPickerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppPickerBinding.inflate(layoutInflater)
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
        supportActionBar?.title = getString(R.string.choose_app)
    }

    private fun initObservable() {
        viewModel.loading.observe(this) { isLoading ->
            binding.loadingIndicator.root.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.message.observe(this) { message ->
            Toast.makeText(this@AppPickerActivity, message, Toast.LENGTH_SHORT).show()
        }

        viewModel.apps.observe(this) { apps ->
            binding.rvAppInfo.adapter = AppInfoAdapter(apps) { appInfo ->
                Intent().apply {
                    putExtra(Constant.PACKAGE_NAME, appInfo.packageName)

                    setResult(Activity.RESULT_OK, this)
                    finish()
                }
            }
        }
    }
}