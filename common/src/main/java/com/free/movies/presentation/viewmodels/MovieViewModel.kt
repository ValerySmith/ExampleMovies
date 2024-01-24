package com.free.movies.presentation.viewmodels

import android.util.Log
import com.free.movies.core.BaseViewModel
import com.free.movies.data.datasource.DataStore
import com.free.movies.data.models.MovieData
import com.free.movies.domain.useCases.MovieUseCase
import com.free.movies.domain.extentions.ifFailure
import com.free.movies.domain.extentions.ifSuccess
import com.free.movies.domain.extentions.map
import com.free.movies.domain.extentions.scopeWithContext
import com.free.movies.domain.models.Quality
import com.free.movies.utils.HLS
import com.free.movies.utils.findLinkByQuality
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val useCase: MovieUseCase,
    private val dataStore: DataStore
) :
    BaseViewModel<MovieViewModel.Action, MovieViewModel.Effect, MovieViewModel.State>(initialState = State()) {


    sealed class Action {
        data class LoadMovie(val movieId: Int) : Action()
        data object OpenPlayer : Action()
        data object ShowLoader : Action()
        data object HideLoader : Action()
    }

    sealed class Effect {
        data object LoaderShown : Effect()
        data object LoaderHidden : Effect()
        data class NavigateToPlayer(val mediaUrl: String) : Effect()
    }

    data class State(
        val movieData: MovieData? = null,
        val isPlayerStart: Boolean = false,
        val stream: String? = "",
        val isLoading: Boolean = false
    )

    override fun handleAction(action: Action) {
        when (action) {
            is Action.LoadMovie -> fetchMovie(action.movieId)
            Action.OpenPlayer -> fetchDataForProxy(
                uiState.value.movieData?.rMovieId ?: return,
                uiState.value.movieData?.audioTracks?.first()?.rAudioId ?: return
            )

            Action.HideLoader -> obtainState { this.copy(isLoading = false) }
            Action.ShowLoader -> obtainState { this.copy(isLoading = true) }
        }
    }

    private fun fetchMovie(movieId: Int) = scopeWithContext {
        useCase.fetchMovie(movieId)
            .ifSuccess { data ->
                data?.let {
                    obtainState {
                        this.copy(movieData = data.movie)
                    }
                }
            }.ifFailure {

            }
    }

    private fun fetchDataForProxy(
        movieId: Int,
        audioId: Int
    ) = scopeWithContext {
        obtainEffect { Effect.LoaderShown }
        useCase.fetchProxyData(movieId, audioId).map {
            fetchJson(
                it?.data?.url,
                it?.data?.headers,
                it?.data?.innerData?.id,
                it?.data?.innerData?.translatorId,
                it?.data?.innerData?.action
            )
            return@map
        }.ifFailure {

        }
    }

    private fun fetchJson(
        url: String?,
        headerMap: Map<String, String>?,
        id: Int?,
        translatorId: Int?,
        action: String?
    ) = scopeWithContext {
        useCase.fetchJson(url, headerMap, id, translatorId, action).map {
            val requestBody =
                it?.string()?.toRequestBody("application/json".toMediaTypeOrNull())
            fetchStreams(requestBody)
            return@map
        }.ifFailure {

        }
    }

    private fun fetchStreams(requestBody: RequestBody?) = scopeWithContext {
        val defaultQuality = dataStore.getQualitySetting().first()
        useCase.fetchStreams(requestBody).map { streams ->
            Log.e("TEST", "Streams - $streams")
            redirect(findLinkByQuality(streams, defaultQuality))
            return@map
        }.ifFailure {

        }
    }

    private fun redirect(url: String?) = scopeWithContext {
        useCase.redirect(url).ifSuccess {
            obtainEffect { Effect.LoaderHidden }
            obtainEffect { Effect.NavigateToPlayer(it) }
        }
    }

}