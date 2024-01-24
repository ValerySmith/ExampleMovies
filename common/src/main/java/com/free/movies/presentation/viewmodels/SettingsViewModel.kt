package com.free.movies.presentation.viewmodels

import android.util.Log
import com.free.movies.core.BaseViewModel
import com.free.movies.data.datasource.DataStore
import com.free.movies.domain.extentions.scopeWithContext
import com.free.movies.domain.models.Quality
import com.free.movies.utils.logs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val dataStore: DataStore) :
    BaseViewModel<SettingsViewModel.Action, SettingsViewModel.Effect, SettingsViewModel.State>(
        initialState = State()
    ) {

    sealed class Action {

        data object InitQuality : Action()
        data class OnChangeQualitySettings(val quality: Quality) : Action()
    }

    sealed class Effect {

    }

    data class State(
        val quality: Quality? = null,
    )

    init {
        obtainAction(Action.InitQuality)
    }

    override fun handleAction(action: Action) {
        when (action) {
            is Action.OnChangeQualitySettings -> setQualitySettings(action.quality)
            Action.InitQuality -> getQualitySettings()
        }
    }

    private fun setQualitySettings(quality: Quality) = scopeWithContext {
        dataStore.setQualitySetting(quality)
    }

    private fun getQualitySettings() = scopeWithContext {
        val quality = dataStore.getQualitySetting().first()
        logs("quality - $quality; pos - ${quality.pos}")
        obtainState { this.copy(quality = quality) }
    }
}