package com.free.movies.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<Action, Effect, State>(initialState: State): ViewModel()  {

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _action: MutableSharedFlow<Action> = MutableSharedFlow()
    val action = _action.asSharedFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            action.collect {
                handleAction(it)
            }
        }
    }

    abstract fun handleAction(action: Action)

    protected fun obtainState(reduce: State.() -> State) {
        _uiState.value = uiState.value.reduce()
    }

    protected fun obtainEffect(builder: () -> Effect) {
        viewModelScope.launch { _effect.send(builder()) }
    }

    fun obtainAction(action: Action) {
        viewModelScope.launch { _action.emit(action) }
    }
}