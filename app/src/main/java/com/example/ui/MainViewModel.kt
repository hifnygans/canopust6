package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.prefs.SessionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    val isLoggedIn: StateFlow<Boolean> = sessionManager.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val themeIndex: StateFlow<Int> = sessionManager.themeIndex
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    
    val username: StateFlow<String?> = sessionManager.username
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun setTheme(index: Int) {
        viewModelScope.launch {
            sessionManager.setThemeIndex(index)
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
        }
    }
}
