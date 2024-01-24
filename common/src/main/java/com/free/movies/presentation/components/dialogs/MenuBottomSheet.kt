package com.free.movies.presentation.components.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.free.movies.data.models.AudioTrack
import com.free.movies.presentation.viewmodels.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuBottomSheet(
    onClick: (Any) -> Unit
) {
    val viewModel = hiltViewModel<PlayerViewModel>()
    val state = viewModel.uiState.collectAsState().value
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(onDismissRequest = {
        viewModel.obtainAction(PlayerViewModel.Action.HideMenu)
    }, sheetState = sheetState) {
        LazyColumn {
            items(state.menuData) { item ->
                    Row(
                        modifier = Modifier
                            .clickable {
                                onClick.invoke(item)
                            }
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 20.dp)
                    ) {
                        Text(
                            text = if (item is AudioTrack) item.title else "",
                            modifier = Modifier.padding(end = 20.dp)
                        )
                    }
            }
        }
    }

}