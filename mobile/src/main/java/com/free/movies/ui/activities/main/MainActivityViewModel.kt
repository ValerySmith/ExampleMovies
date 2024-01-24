package com.free.movies.ui.activities.main

import com.free.movies.core.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() :
    BaseViewModel<MainActivityViewModel.Action, MainActivityViewModel.Effect, MainActivityViewModel.State>(
        initialState = State()
    ) {

    sealed class Action {

    }

    sealed class Effect {

    }

    data class State(
        val test: Int = 0
    )

    override fun handleAction(action: Action) {

    }
}