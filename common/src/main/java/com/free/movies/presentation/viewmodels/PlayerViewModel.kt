package com.free.movies.presentation.viewmodels

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.common.Player.STATE_BUFFERING
import androidx.media3.common.Player.STATE_READY
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.dash.DefaultDashChunkSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.navigation.NavHostController
import com.free.movies.core.BaseViewModel
import com.free.movies.data.models.AudioTrack
import com.free.movies.domain.useCases.MovieUseCase
import com.free.movies.domain.extentions.ifFailure
import com.free.movies.domain.extentions.ifSuccess
import com.free.movies.domain.extentions.map
import com.free.movies.domain.extentions.setScreenOrientation
import com.free.movies.domain.models.Format
import com.free.movies.presentation.viewmodels.PlayerViewModel.TypeMenu.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(private val interactor: MovieUseCase) :
    BaseViewModel<PlayerViewModel.Action, PlayerViewModel.Effect, PlayerViewModel.State>(
        initialState = State()
    ) {

    enum class TypeMenu {
        QUALITY,
        AUDIO
    }

    sealed class Action {
        data class BuildMediaSource(val mediaUrl: String) : Action()
        data class LoadMovie(val movieId: Int) : Action()
        data object Toggle : Action()
        data object Forward : Action()
        data object Replay : Action()
        data class ShowMenu(val type: TypeMenu) : Action()
        data object HideMenu : Action()
        data object ShowControlsVisible : Action()
        data object HideControlsVisible : Action()
        data object ShowLoader : Action()
        data object HideLoader : Action()
        data object NavBack : Action()
        data class SeekTo(val value: Long) : Action()
        data class SetLanguage(val item: Any) : Action()
        data class InitPlayerData(val currentPosition: Long, val duration: Long) : Action()
    }

    sealed class Effect {
        data class GetMediaSource(val mediaSource: MediaSource) : Effect()
        data object LoaderShown : Effect()
        data object LoaderHidden : Effect()
        data object NavBack : Effect()
        data class Toggle(val isPlaying: Boolean) : Effect()
        data object Replay : Effect()
        data object Forward : Effect()
        data object ShowMenu : Effect()
        data class OnSeekTo(val value: Long) : Effect()
    }

    data class State(
        val mediaUrl: String = "",
        val isLoading: Boolean = false,
        val isPlaying: Boolean = false,
        val isControlsShown: Boolean = false,
        val isControlsVisible: Boolean = true,
        val title: String = "",
        val position: Long = 0L,
        val menuData: List<Any> = emptyList(),
        val isShowMenu: Boolean = false,
        val audioTracks: List<AudioTrack> = emptyList(),
        val rMovieId: Int = 0,
        val passedTime: String = "",
        val leftTime: String = "",
        val currentPosition: Float = 0.0f,
        val duration: Float = 0.0f
    )

    @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
    override fun handleAction(action: Action) {
        when (action) {
            is Action.LoadMovie -> fetchMovie(action.movieId)
            is Action.BuildMediaSource -> obtainEffect {
                Effect.GetMediaSource(
                    buildMediaSource(
                        action.mediaUrl
                    )
                )
            }

            Action.HideLoader -> obtainState { this.copy(isLoading = false) }
            Action.ShowLoader -> obtainState { this.copy(isLoading = true) }
            Action.NavBack -> obtainEffect { Effect.NavBack }
            Action.Toggle -> {
                obtainEffect { Effect.Toggle(isPlaying = uiState.value.isPlaying) }
                obtainState { this.copy(isPlaying = isPlaying.not()) }
            }

            Action.Forward -> obtainEffect { Effect.Forward }
            Action.Replay -> obtainEffect { Effect.Replay }
            Action.HideControlsVisible -> obtainState { this.copy(isControlsShown = false) }
            Action.ShowControlsVisible -> obtainState { this.copy(isControlsShown = true) }
            is Action.ShowMenu -> {
                obtainEffect { Effect.ShowMenu }
                obtainState {
                    this.copy(
                        menuData = setupBottomMenu(action.type),
                        isShowMenu = true
                    )
                }
            }

            Action.HideMenu -> obtainState { this.copy(isShowMenu = false) }
            is Action.SetLanguage -> changeAudio(action.item)
            is Action.SeekTo -> obtainEffect { Effect.OnSeekTo((action.value * 1000)) }
            is Action.InitPlayerData -> {
                if (uiState.value.isPlaying)
                obtainState {
                    this.copy(
                        currentPosition = (action.currentPosition / 1000).toFloat(),
                        duration = (action.duration / 1000).toFloat(),
                        passedTime = getPassedTime(action.currentPosition),
                        leftTime = getLeftTime(action.currentPosition, action.duration)
                    )
                }
            }
        }
    }

    private fun setupBottomMenu(type: TypeMenu): List<Any> {
        return when (type) {
            QUALITY -> {
                emptyList()
            }

            AUDIO -> {
                uiState.value.audioTracks
            }
        }
    }

    @OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun buildMediaSource(mediaUrl: String): MediaSource {
        if (mediaUrl.endsWith(Format.MP4.value))
            return ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                .createMediaSource(MediaItem.fromUri(mediaUrl))
        else if (mediaUrl.endsWith(Format.M3U8.value))
            return HlsMediaSource.Factory(DefaultHttpDataSource.Factory())
                .createMediaSource(MediaItem.fromUri(mediaUrl))
        val manifestDataSourceFactory = DefaultHttpDataSource.Factory()
        val dashChunkSourceFactory = DefaultDashChunkSource.Factory(DefaultHttpDataSource.Factory())
        return DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(mediaUrl))
    }

    val listener = object : Listener {
        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Log.e("TEST", "playerError -${error.message}")
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                STATE_BUFFERING -> {

                }

                STATE_READY -> {
                    obtainState { this.copy(isPlaying = true, isControlsVisible = true) }
                }

                else -> {}
            }
            Log.e("TEST", "$playbackState")
        }
    }

    @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
    private fun getLeftTime(currentPosition: Long, duration: Long): String {
        val difference = (duration - currentPosition)
        val hours = TimeUnit.MILLISECONDS.toHours(difference)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(difference) % 60
        val remainingSeconds = TimeUnit.MILLISECONDS.toSeconds(difference) % 60
        return String.format(
            "%02d:%02d:%02d",
            hours,
            minutes,
            remainingSeconds
        )
    }

    @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
    private fun getPassedTime(currentPosition: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(currentPosition)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(currentPosition) % 60
        val remainingSeconds = TimeUnit.MILLISECONDS.toSeconds(currentPosition) % 60
        return String.format(
            "%02d:%02d:%02d",
            hours,
            minutes,
            remainingSeconds
        )
    }

    fun navigateUpWithChangeOrientation(
        context: Context,
        player: Player,
        navController: NavHostController
    ) {
        with(context) {
            setScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }
        navController.navigateUp()
        player.removeListener(listener)
        player.release()
    }

    private fun changeAudio(item: Any) {
//        if (item is AudioTrack)
//            fetchDataForProxy(item.rAudioId)
    }

    private fun fetchMovie(movieId: Int) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                interactor.fetchMovie(movieId)
                    .ifSuccess { data ->
                        data?.let {
                            obtainState {
                                this.copy(
                                    title = data.movie.title,
                                    audioTracks = data.movie.audioTracks,
                                    rMovieId = data.movie.rMovieId
                                )
                            }
                        }
                    }.ifFailure {

                    }
            }
        }

    private fun fetchDataForProxy(
        audioId: Int
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            obtainEffect { Effect.LoaderShown }
            interactor.fetchProxyData(uiState.value.rMovieId, audioId).map {
                return@map
            }.ifFailure {

            }
        }
    }

}