package com.example.android.app.scheduler.presentation.app_picker

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.android.app.scheduler.R
import com.example.android.app.scheduler.core.Constant
import com.example.android.app.scheduler.domain.model.AppInfo
import com.example.android.app.scheduler.presentation.shared.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPickerScreen(
    navController: NavController,
    viewModel: AppPickerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.choose_app)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when(state) {
                is AppPickerUiState.Error -> {
                    Text((state as AppPickerUiState.Error).message)
                }
                AppPickerUiState.Loading -> {
                    LoadingIndicator()
                }
                is AppPickerUiState.Success -> {
                    val apps = (state as AppPickerUiState.Success).apps

                    LazyColumn {
                        items(apps.size) { index ->
                            AppInfoItem(
                                appInfo = apps[index],
                                onSelectApp = {
                                    navController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set(Constant.PACKAGE_NAME, apps[index].packageName)

                                    navController.popBackStack()
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
fun AppInfoItem(
    appInfo: AppInfo,
    onSelectApp: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable { onSelectApp() },
        verticalAlignment = Alignment.CenterVertically
    ) {

        val appIconPainter = remember(appInfo.appIcon) {
            appInfo.appIcon.toBitmap().asImageBitmap().let { bitmap ->
                BitmapPainter(bitmap)
            }
        }
        // App Icon
        Image(
            painter = appIconPainter,
            contentDescription = "App Icon",
            modifier = Modifier
                .size(50.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        // App Name and Package Name
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = appInfo.appName,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = appInfo.packageName,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}