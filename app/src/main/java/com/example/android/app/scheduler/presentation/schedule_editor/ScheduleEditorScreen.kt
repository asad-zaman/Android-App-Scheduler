package com.example.android.app.scheduler.presentation.schedule_editor

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.android.app.scheduler.domain.model.ScheduleInfo
import java.util.Calendar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.android.app.scheduler.R
import com.example.android.app.scheduler.R.string.create_update_schedule
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import com.example.android.app.scheduler.core.Constant
import com.example.android.app.scheduler.core.getAppInfoByPackage
import com.example.android.app.scheduler.core.getFormattedDateTime
import com.example.android.app.scheduler.domain.model.ScheduleStatus
import com.example.android.app.scheduler.presentation.shared.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleEditorScreen(
    navController: NavController,
    scheduleInfo: ScheduleInfo?,
    viewModel: ScheduleEditorViewModel = hiltViewModel()
) {
    val selectedPackageName by viewModel.selectedPackageName.collectAsState()
    var selectedTimeInMillis by remember { mutableStateOf<Long?>(null) }
    var isDirty by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>(Constant.PACKAGE_NAME)
            ?.observeForever {
                viewModel.setPackageName(it)
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = create_update_schedule)) },
                navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = { Icon(Icons.Default.Alarm, contentDescription = null) },
                text = { Text(text = stringResource(R.string.set_schedule)) },
                onClick = {
                    if(!isDirty) {
                        Toast.makeText(context,
                            context.getString(R.string.please_update_datetime_to_schedule), Toast.LENGTH_SHORT).show()
                        return@ExtendedFloatingActionButton
                    }

                    if(selectedPackageName == null && scheduleInfo == null) {
                        Toast.makeText(context,
                            context.getString(R.string.please_choose_app_to_schedule), Toast.LENGTH_SHORT).show()
                        return@ExtendedFloatingActionButton
                    }

                    if(scheduleInfo == null) {
                        viewModel.scheduleApp(
                            ScheduleInfo(
                                packageName = selectedPackageName!!,
                                timeInMillis = selectedTimeInMillis!!,
                                status = ScheduleStatus.PENDING,
                            ),
                            onnSuccess = {
                                navController.popBackStack()
                            }
                        )
                    } else {
                        val newSchedule = scheduleInfo.copy(timeInMillis = selectedTimeInMillis!!)
                        viewModel.rescheduleApp(
                            scheduleInfo = newSchedule,
                            onnSuccess = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AppCardWithDateTimePickerField(
                packageName = scheduleInfo?.packageName ?: selectedPackageName ?: "",
                initialTime = scheduleInfo?.timeInMillis ?: selectedTimeInMillis,
                isEditing = scheduleInfo != null,
                onPickApp = {
                    navController.navigate(Screen.AppPicker.route)
                }
            ) {
                selectedTimeInMillis = it.timeInMillis
                isDirty = true
            }
        }
    }
}

@Composable
fun AppCardWithDateTimePickerField(
    packageName: String,
    initialTime: Long? = null,
    isEditing: Boolean = false,
    onPickApp: () -> Unit,
    onDateTimeSelected: (Calendar) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance().apply {
        initialTime?.let {
            timeInMillis = it
        }
    } }

    var selectedDateTime by remember {
        mutableStateOf(if(isEditing) calendar.timeInMillis.getFormattedDateTime() else  "" )
    }

    val appInfo = remember(packageName) {
        context.packageManager.getAppInfoByPackage(packageName)
    }

    val appIconPainter = remember(appInfo?.appIcon) {
        appInfo?.appIcon?.toBitmap()?.asImageBitmap()?.let { bitmap ->
            BitmapPainter(bitmap)
        }
    }

    // Show date picker when flag is true
    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                showDatePicker = false
                showTimePicker = true
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    }

    // Show time picker when flag is true
    if (showTimePicker) {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                showTimePicker = false
                selectedDateTime = calendar.timeInMillis.getFormattedDateTime()
                onDateTimeSelected(calendar)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Card(
            modifier = Modifier
                .size(100.dp)
                .then(
                    if (isEditing) Modifier else Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onPickApp() }
                ),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Image(
                painter = appIconPainter ?: painterResource(id = R.drawable.ic_add),
                contentDescription = "App Icon",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }

        // App name
        Text(
            text = appInfo?.appName ?: stringResource(R.string.choose_app),
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 10.dp)
        )

        // Date time input field
        OutlinedTextField(
            value = selectedDateTime,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            label = { Text(stringResource(R.string.select_date_and_time)) },
            trailingIcon = {
                IconButton(onClick = {
                    showDatePicker = true // ← onCalendarClick triggers this
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar),
                        contentDescription = "Calendar Icon"
                    )
                }
            }
        )
    }
}
