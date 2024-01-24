package com.free.movies.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.map
import com.free.movies.core.BaseViewModel
import com.free.movies.data.models.MovieData
import com.free.movies.data.models.MovieType
import com.free.movies.domain.useCases.MovieUseCase
import com.free.movies.domain.extentions.ifSuccess
import com.free.movies.domain.extentions.map
import com.free.movies.domain.extentions.scopeWithContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(private val useCase: MovieUseCase) :
    BaseViewModel<MoviesViewModel.Action, MoviesViewModel.Effect, MoviesViewModel.State>(
        initialState = State()
    ) {

    sealed class Action {
        data object InitLoad : Action()
        data class NavigateToMovie(val movieId: Int) : Action()
        data object NavigateToSearch: Action()
        data object ShowSettings: Action()
        data object HideSettings: Action()

    }

    sealed class Effect {
        data object NavigateToSearch: Effect()
        data class NavigateToMovie(val movieId: Int) : Effect()
    }

    data class State(
        val movies: Flow<PagingData<MovieData>> = emptyFlow(),
        val isShowSettings: Boolean = false
    )

    init {
        obtainAction(Action.InitLoad)
    }


    override fun handleAction(action: Action) {
        when (action) {
            Action.InitLoad -> fetchAllMovies()
            is Action.NavigateToMovie -> obtainEffect {
                Effect.NavigateToMovie(
                    action.movieId
                )
            }

            Action.NavigateToSearch -> obtainEffect { Effect.NavigateToSearch }
            Action.ShowSettings -> obtainState { this.copy(isShowSettings = true) }
            Action.HideSettings -> obtainState { this.copy(isShowSettings = false) }
        }
    }

    private fun fetchAllMovies() = scopeWithContext {
        val data = useCase.fetchAllMovies()
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
        obtainState { this.copy(movies = data) }
    }
}