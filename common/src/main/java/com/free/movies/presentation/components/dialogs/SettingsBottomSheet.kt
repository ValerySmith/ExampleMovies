package com.free.movies.presentation.components.dialogs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.free.movies.domain.models.Quality
import com.free.movies.domain.models.Quality.Companion.fromValue
import com.free.movies.presentation.viewmodels.MoviesViewModel
import com.free.movies.presentation.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet() {
    val settingViewModel = hiltViewModel<SettingsViewModel>()
    val moviesViewModel = hiltViewModel<MoviesViewModel>()
    val state = settingViewModel.uiState.collectAsState().value
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(onDismissRequest = {
        moviesViewModel.obtainAction(MoviesViewModel.Action.HideSettings)
    }, sheetState = sheetState) {

        Slider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            value = state.quality?.pos?.toFloat() ?: 0f,
            valueRange = 1f..4f,
            steps = 1,
            onValueChange = {
                val quality = Quality.fromValue(it.toInt())
                settingViewModel.obtainAction(
                    SettingsViewModel.Action.OnChangeQualitySettings(
                        quality
                    )
                )
            })
    }
}