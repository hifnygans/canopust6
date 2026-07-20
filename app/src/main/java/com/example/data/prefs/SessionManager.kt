package com.example.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SessionManager(private val context: Context) {

    companion object {
        val USERNAME = stringPreferencesKey("username")
        val API_KEY = stringPreferencesKey("api_key")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val THEME_INDEX = intPreferencesKey("theme_index")
    }

    val username: Flow<String?> = context.dataStore.data.map { it[USERNAME] }
    val apiKey: Flow<String?> = context.dataStore.data.map { it[API_KEY] }
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    val themeIndex: Flow<Int> = context.dataStore.data.map { it[THEME_INDEX] ?: 0 }

    suspend fun saveSession(username: String, apiKey: String) {
        context.dataStore.edit { prefs ->
            prefs[USERNAME] = username
            prefs[API_KEY] = apiKey
            prefs[IS_LOGGED_IN] = true
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(USERNAME)
            prefs.remove(API_KEY)
            prefs[IS_LOGGED_IN] = false
        }
    }

    suspend fun setThemeIndex(index: Int) {
        context.dataStore.edit { prefs ->
            prefs[THEME_INDEX] = index
        }
    }
}
