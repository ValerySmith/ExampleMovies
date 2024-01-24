package com.free.movies.ui.movies

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.LoadState.Loading
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.free.movies.data.models.MovieData
import com.free.movies.domain.extentions.exhaustive
import com.free.movies.navigation.NavigationItem.DetailMovie.navigateToDetailMovie
import com.free.movies.navigation.NavigationItem.Search.navigateToSearch
import com.free.movies.presentation.viewmodels.MoviesViewModel
import com.free.movies.presentation.components.FullScreenLoader
import com.free.movies.presentation.components.dialogs.SettingsBottomSheet
import com.free.movies.presentation.theme.AppThemeMobile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(navController: NavHostController) {
    val viewModel: MoviesViewModel = hiltViewModel()
    val state = viewModel.uiState.collectAsState().value
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect {
            when (it) {
                is MoviesViewModel.Effect.NavigateToMovie -> {
                    navController.navigateToDetailMovie(it.movieId)
                }

                MoviesViewModel.Effect.NavigateToSearch -> navController.navigateToSearch()
            }.exhaustive
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = stringResource(id = com.free.movies.common.R.string.app_name))
        }, colors = TopAppBarDefaults.topAppBarColors(),
            actions = {
                IconButton(onClick = {
                    viewModel.obtainAction(MoviesViewModel.Action.NavigateToSearch)
                }) {
                    Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
                }
                IconButton(onClick = {
                    viewModel.obtainAction(MoviesViewModel.Action.ShowSettings)
                }) {
                    Icon(imageVector = Icons.Rounded.Settings, contentDescription = null)
                }
            })
    }) {
        MoviesVerticalGrid(viewModel, Modifier.padding(it))
    }

    if (state.isShowSettings)
        SettingsBottomSheet()
}


@Composable
fun MoviesVerticalGrid(
    viewModel: MoviesViewModel,
    modifier: Modifier
) {
    val data = viewModel.uiState.collectAsState().value.movies.collectAsLazyPagingItems()
    val lazyGridScrollState = rememberLazyGridState()

    LazyVerticalGrid(
        modifier = modifier.padding(top = 16.dp),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(0.dp),
        state = lazyGridScrollState
    ) {
        items(data.itemCount) { index ->
            MovieItem(data, index, onClick = { movieId ->
                viewModel.obtainAction(MoviesViewModel.Action.NavigateToMovie(movieId))
            })
        }
    }

    val isLoading = data.loadState.refresh is Loading || data.loadState.append is Loading
    FullScreenLoader(isLoading = isLoading)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MovieItem(data: LazyPagingItems<MovieData>, index: Int, onClick: (Int) -> Unit) {
    val item = data[index]
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            onClick = {
                onClick.invoke(
                    item?.id ?: 0
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp, top = 8.dp)
                .height(150.dp)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onBackground)
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(8.dp)),
                    painter = rememberAsyncImagePainter(item?.posterUrl),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
            }
        }
        data.apply {
            when {
                loadState.refresh is LoadState.Error -> {
                    // refresh()
                }

                loadState.append is Loading -> {
                    //SmallLoader(true)
                }

                loadState.append is LoadState.Error -> {
                    // refresh()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MoviesScreenPreview() {
    AppThemeMobile {

    }
}