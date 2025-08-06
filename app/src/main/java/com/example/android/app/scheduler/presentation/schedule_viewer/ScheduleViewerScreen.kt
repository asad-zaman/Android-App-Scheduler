package com.example.android.app.scheduler.presentation.schedule_viewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.android.app.scheduler.R
import com.example.android.app.scheduler.core.getAppInfoByPackage
import com.example.android.app.scheduler.core.getFormattedDateTime
import com.example.android.app.scheduler.domain.model.ScheduleInfo
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.android.app.scheduler.R.string.schedule_list
import com.example.android.app.scheduler.core.Constant
import com.example.android.app.scheduler.domain.model.ScheduleStatus
import com.example.android.app.scheduler.presentation.shared.LoadingIndicator
import com.example.android.app.scheduler.presentation.shared.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleViewerScreen(
    navController: NavController,
    viewModel: ScheduleViewerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = schedule_list)) },

            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(text = stringResource(R.string.add_schedule)) },
                onClick = {
                    navController.navigate(Screen.ScheduleEditor.route)
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when(state) {
                is ScheduleViewerUiState.Error -> {
                    Text((state as ScheduleViewerUiState.Error).message)
                }
                ScheduleViewerUiState.Loading -> {
                    LoadingIndicator()
                }
                is ScheduleViewerUiState.Success -> {
                    val schedules = (state as ScheduleViewerUiState.Success).schedules
                    LazyColumn {
                        items(schedules.size) { index ->
                            ScheduleItem(
                                scheduleInfo = schedules[index],
                                onEditClick = {
                                    navController.navigate(Screen.ScheduleEditor.route)

                                    navController
                                        .getBackStackEntry(Screen.ScheduleEditor.route)
                                        .savedStateHandle[Constant.SCHEDULE_INFO] = schedules[index]
                                },
                                onCancelClick = {
                                    viewModel.cancelApp(schedules[index])
                                },
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun ScheduleItem(
    scheduleInfo: ScheduleInfo,
    onEditClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val context = LocalContext.current
    val appInfo = remember(scheduleInfo.packageName) {
        context.packageManager.getAppInfoByPackage(scheduleInfo.packageName)
    }

    val appIconPainter = remember(appInfo?.appIcon) {
        appInfo?.appIcon?.toBitmap()?.asImageBitmap()?.let { bitmap ->
            BitmapPainter(bitmap)
        }
    }

    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Icon
        Image(
            painter = appIconPainter ?: painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "App Icon",
            modifier = Modifier
                .size(50.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        // App Name and Schedule Time
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = appInfo?.appName ?: "",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = scheduleInfo.timeInMillis.getFormattedDateTime(),
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Schedule Status Icon
        Image(
            painter = painterResource(id = scheduleInfo.drawableId),
            contentDescription = "Schedule Status",
            modifier = Modifier
                .size(40.dp)
                .padding(horizontal = 10.dp)
        )

        Box {
            IconButton(
                onClick = { menuExpanded = true },
                enabled = scheduleInfo.status == ScheduleStatus.PENDING,
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu"
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = {
                        menuExpanded = false
                        onEditClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Cancel") },
                    onClick = {
                        menuExpanded = false
                        onCancelClick()
                    }
                )
            }
        }
    }
}