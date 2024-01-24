package com.free.movies.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.free.movies.core.BaseViewModel
import com.free.movies.data.models.MovieData
import com.free.movies.data.models.MovieType
import com.free.movies.domain.extentions.scopeWithContext
import com.free.movies.domain.useCases.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val useCase: SearchUseCase) :
    BaseViewModel<SearchViewModel.Action, SearchViewModel.Effect, SearchViewModel.State>(
        initialState = State()
    ) {

    sealed class Action {
        data class NavigateToMovie(val movieId: Int) : Action()
        data object NavigateUp : Action()
        data class QueryChange(val query: String) : Action()
        data class SetStateSearch(val isActive: Boolean) : Action()
    }

    sealed class Effect {
        data class NavigateToMovie(val movieId: Int) : Effect()
        data object NavigateUp : Effect()
    }

    data class State(
        val searchResultsMovies: Flow<PagingData<MovieData>> = emptyFlow(),
        val searchResultsSerials: Flow<PagingData<MovieData>> = emptyFlow(),
        val query: String = "",
        val isActiveSearch: Boolean = false
    )

    override fun handleAction(action: Action) {
        when (action) {
            is Action.NavigateToMovie -> obtainEffect {
                Effect.NavigateToMovie(
                    action.movieId
                )
            }

            is Action.QueryChange -> {
                if (action.query.length > 3)
                    fetchSearch(query = action.query.trim())
                obtainState { this.copy(query = action.query) }
            }

            is Action.SetStateSearch -> obtainState { this.copy(isActiveSearch = action.isActive) }
            Action.NavigateUp -> obtainEffect { Effect.NavigateUp }
        }
    }

    private fun fetchSearch(query: String) = scopeWithContext {
        val movies = useCase.fetchSearch(movieType = MovieType.MOVIE.type, query = query)
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
        val serials =
            useCase.fetchSearch(movieType = MovieType.SERIAL.type, query = query)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
        obtainState { this.copy(searchResultsMovies = movies, searchResultsSerials = serials) }
    }

}