package com.free.movies.ui.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.free.movies.data.models.MovieData
import com.free.movies.domain.extentions.exhaustive
import com.free.movies.navigation.NavigationItem.DetailMovie.navigateToDetailMovie
import com.free.movies.presentation.components.FullScreenLoader
import com.free.movies.presentation.viewmodels.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavHostController) {
    val viewModel = hiltViewModel<SearchViewModel>()
    val state = viewModel.uiState.collectAsState().value

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect {
            when (it) {
                is SearchViewModel.Effect.NavigateToMovie -> navController.navigateToDetailMovie(it.movieId)
                SearchViewModel.Effect.NavigateUp -> navController.navigateUp()
            }.exhaustive
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = {}, navigationIcon = {
            IconButton(onClick = {
                viewModel.obtainAction(SearchViewModel.Action.NavigateUp)
            }) {
                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
            }
        })
    }) { innerPadding ->
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(8.dp),
            query = state.query,
            onQueryChange = {
                viewModel.obtainAction(SearchViewModel.Action.QueryChange(it))
            },
            onSearch = {
                viewModel.obtainAction(SearchViewModel.Action.SetStateSearch(false))
            },
            active = state.isActiveSearch,
            onActiveChange = {
                viewModel.obtainAction(SearchViewModel.Action.SetStateSearch(it))
            }
        ) {
            SearchResults(viewModel, Modifier.padding(innerPadding))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchResults(
    viewModel: SearchViewModel,
    modifier: Modifier
) {
    val movies =
        viewModel.uiState.collectAsState().value.searchResultsMovies.collectAsLazyPagingItems()
    val serials =
        viewModel.uiState.collectAsState().value.searchResultsSerials.collectAsLazyPagingItems()
    val lazyGridScrollState = rememberLazyGridState()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
    ) {
        stickyHeader {
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(id = com.free.movies.common.R.string.movies)
            )
        }
        items(movies.itemCount) { index ->
            SearchResultsItem(movies, index, onClick = { movieId ->
                viewModel.obtainAction(SearchViewModel.Action.NavigateToMovie(movieId))
            })
        }
        stickyHeader {
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(id = com.free.movies.common.R.string.serials)
            )
        }
        items(serials.itemCount) { index ->
            SearchResultsItem(serials, index, onClick = { movieId ->
                viewModel.obtainAction(SearchViewModel.Action.NavigateToMovie(movieId))
            })
        }
    }

    val isLoading =
        (movies.loadState.refresh is LoadState.Loading || movies.loadState.append is LoadState.Loading)
                || (serials.loadState.refresh is LoadState.Loading || serials.loadState.append is LoadState.Loading)
    FullScreenLoader(isLoading = isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsItem(data: LazyPagingItems<MovieData>, index: Int, onClick: (Int) -> Unit) {
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
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    text = "${item?.title} ${item?.releaseDate}"
                )
            }
        }
        data.apply {
            when {
                loadState.refresh is LoadState.Error -> {
                    // refresh()
                }

                loadState.append is LoadState.Loading -> {
                    //SmallLoader(true)
                }

                loadState.append is LoadState.Error -> {
                    // refresh()
                }
            }
        }
    }
}