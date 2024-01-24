package com.free.movies.ui.detailmovie

import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.free.movies.R
import com.free.movies.domain.extentions.exhaustive
import com.free.movies.navigation.NavigationItem
import com.free.movies.navigation.NavigationItem.Player.navigateToPlayer
import com.free.movies.presentation.components.FullScreenLoader
import com.free.movies.presentation.viewmodels.MovieViewModel

@OptIn(UnstableApi::class)
@Composable
fun DetailMovieScreen(navController: NavHostController, movieId: Int) {
    val context = LocalContext.current
    val viewModel: MovieViewModel = hiltViewModel()
    val state = viewModel.uiState.collectAsState().value
    viewModel.obtainAction(MovieViewModel.Action.LoadMovie(movieId))
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MovieViewModel.Effect.LoaderShown -> {
                    viewModel.obtainAction(MovieViewModel.Action.ShowLoader)
                }

                is MovieViewModel.Effect.LoaderHidden -> {
                    viewModel.obtainAction(MovieViewModel.Action.HideLoader)
                }

                is MovieViewModel.Effect.NavigateToPlayer -> {
                    navController.navigateToPlayer(effect.mediaUrl, movieId)
                }
            }.exhaustive
        }
    }
    FullScreenLoader(state.isLoading)
    Movie(viewModel = viewModel)
}

@Composable
fun Movie(viewModel: MovieViewModel) {
    val state = viewModel.uiState.collectAsState().value
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val (poster, title, titleEn, imdbRating, releaseDate, description, playButton) = createRefs()

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .constrainAs(poster) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            painter = rememberAsyncImagePainter(state.movieData?.posterUrl),
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )
        IconButton(
            onClick = {
                viewModel.obtainAction(MovieViewModel.Action.OpenPlayer)
            },
            modifier = Modifier
                .size(75.dp)
                .constrainAs(playButton) {
                    top.linkTo(poster.bottom)
                    bottom.linkTo(poster.bottom)
                    end.linkTo(parent.end, margin = 32.dp)
                }) {
            Image(
                modifier = Modifier.size(75.dp),
                painter = painterResource(id = R.drawable.ic_play_circle),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Blue)
            )
        }
        Text(
            text = state.movieData?.title ?: "",
            color = Color.Black,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(poster.bottom, margin = 16.dp)
                start.linkTo(parent.start, margin = 16.dp)
                end.linkTo(parent.end, margin = 16.dp)
            })
        Text(
            text = state.movieData?.titleEn ?: "",
            color = Color.Black,
            fontSize = 16.sp,
            modifier = Modifier
                .alpha(0.5f)
                .constrainAs(titleEn) {
                    top.linkTo(title.bottom, margin = 8.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                })
        Text(text = state.movieData?.imdbRating ?: "",
            color = Color.Black,
            fontSize = 16.sp,
            modifier = Modifier.constrainAs(imdbRating) {
                top.linkTo(titleEn.bottom)
                start.linkTo(parent.start, margin = 16.dp)
            })
        Text(
            text = state.movieData?.releaseDate ?: "",
            color = Color.Black,
            fontSize = 16.sp,
            modifier = Modifier.constrainAs(releaseDate) {
                top.linkTo(titleEn.bottom)
                start.linkTo(imdbRating.end, margin = 16.dp)

            })
        Text(
            text = state.movieData?.description ?: "",
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .constrainAs(description) {
                    top.linkTo(releaseDate.bottom)
                    end.linkTo(parent.end)
                })
    }
}

