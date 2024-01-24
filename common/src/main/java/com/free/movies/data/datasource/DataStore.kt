package com.free.movies.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.free.movies.domain.models.Quality
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class DataStore @Inject constructor(@ApplicationContext context: Context) {
    private val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES)
    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val PUSH_SETTINGS_QUALITY = stringPreferencesKey(SETTINGS_QUALITY)
    }

    suspend fun setQualitySetting(quality: Quality) {
        dataStore.edit { pref ->
            pref[PreferencesKeys.PUSH_SETTINGS_QUALITY] = quality.name
        }
    }

    suspend fun getQualitySetting() = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map {
            val qualityName = it[PreferencesKeys.PUSH_SETTINGS_QUALITY] ?: DEFAULT_QUALITY
            Quality.valueOf(qualityName)
        }

    private companion object {
        private val DEFAULT_QUALITY = Quality.HD.name
        private const val USER_PREFERENCES = "user_preferences"
        private const val SETTINGS_QUALITY = "quality"
    }
}
