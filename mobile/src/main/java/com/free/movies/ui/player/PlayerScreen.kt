package com.free.movies.ui.player

import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.free.movies.domain.extentions.exhaustive
import com.free.movies.domain.extentions.setScreenOrientation
import com.free.movies.presentation.components.PlayerControls
import com.free.movies.presentation.components.FullScreenLoader
import com.free.movies.presentation.components.dialogs.MenuBottomSheet
import com.free.movies.presentation.viewmodels.PlayerViewModel
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(navController: NavHostController, mediaUrl: String, movieId: Int) {
    val viewModel = hiltViewModel<PlayerViewModel>()
    val state = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    val isControlsShown = state.isControlsShown
    viewModel.obtainAction(PlayerViewModel.Action.LoadMovie(movieId))
    val player = remember {
        ExoPlayer.Builder(context).setMediaSourceFactory(DefaultMediaSourceFactory(context))
            .setTrackSelector(DefaultTrackSelector(context)).setLoadControl(DefaultLoadControl())
            .build().apply {
                with(context) {
                    setScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                }
                addListener(viewModel.listener)
                viewModel.obtainAction(PlayerViewModel.Action.BuildMediaSource(mediaUrl))
                prepare()
            }
    }
    val playerView = rememberUpdatedState(newValue = PlayerView(context)).value

    BackHandler {
        viewModel.navigateUpWithChangeOrientation(context, player, navController)
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect {
            when (it) {
                is PlayerViewModel.Effect.GetMediaSource -> {
                    player.playWhenReady = true
                    player.setMediaSource(it.mediaSource)
                    player.play()
                }

                PlayerViewModel.Effect.LoaderHidden -> viewModel.obtainAction(PlayerViewModel.Action.HideLoader)
                PlayerViewModel.Effect.LoaderShown -> viewModel.obtainAction(PlayerViewModel.Action.ShowLoader)
                PlayerViewModel.Effect.NavBack -> viewModel.navigateUpWithChangeOrientation(
                    context,
                    player,
                    navController
                )

                is PlayerViewModel.Effect.Toggle -> {
                    if (it.isPlaying)
                        player.pause()
                    else
                        player.play()
                }

                PlayerViewModel.Effect.Forward -> player.seekForward()
                PlayerViewModel.Effect.Replay -> player.seekBack()
                is PlayerViewModel.Effect.ShowMenu -> player.pause()
                is PlayerViewModel.Effect.OnSeekTo -> player.seekTo(it.value)
            }.exhaustive
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        playerView.onPause()
        player.pause()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        playerView.onResume()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        player.removeListener(viewModel.listener)
        player.release()
    }

    FullScreenLoader(isLoading = state.isLoading)

    LaunchedEffect(isControlsShown) {
        delay(3000)
        viewModel.obtainAction(PlayerViewModel.Action.HideControlsVisible)
    }

    if (state.isShowMenu) MenuBottomSheet {
        viewModel.obtainAction(PlayerViewModel.Action.SetLanguage(it))
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .clickable {
            if (state.isControlsVisible && !isControlsShown)
                viewModel.obtainAction(PlayerViewModel.Action.ShowControlsVisible)
            else
                viewModel.obtainAction(PlayerViewModel.Action.HideControlsVisible)
        }) {

        AndroidView(
            factory = {
                playerView.apply {
                    this.player = player
                    resizeMode = RESIZE_MODE_FILL
                    useController = false
                    setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                }
            }, modifier = Modifier.fillMaxSize()
        )

        if (state.isControlsVisible)
            AnimatedVisibility(
                visible = isControlsShown,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                viewModel.obtainAction(
                    PlayerViewModel.Action.InitPlayerData(
                        player.currentPosition, player.duration
                    )
                )
                PlayerControls(modifier = Modifier.fillMaxSize(), state = state, onReplayClick = {
                    viewModel.obtainAction(PlayerViewModel.Action.Replay)
                }, onPauseToggle = {
                    viewModel.obtainAction(PlayerViewModel.Action.Toggle)
                }, onForwardClick = {
                    viewModel.obtainAction(PlayerViewModel.Action.Forward)
                }, onSetAudio = {
                    viewModel.obtainAction(PlayerViewModel.Action.ShowMenu(PlayerViewModel.TypeMenu.AUDIO))
                }, onSetQuality = {
                    viewModel.obtainAction(PlayerViewModel.Action.ShowMenu(PlayerViewModel.TypeMenu.QUALITY))
                }, onBack = {
                    viewModel.obtainAction(PlayerViewModel.Action.NavBack)
                }, onSeekTo = {
                    viewModel.obtainAction(PlayerViewModel.Action.SeekTo(it))
                })
            }
    }

}
