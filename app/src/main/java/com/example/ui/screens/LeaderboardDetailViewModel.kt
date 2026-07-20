package com.example.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.LeaderboardEntriesResponse
import com.example.data.repository.RetroAchievementsRepository
import kotlinx.coroutines.launch

class LeaderboardDetailViewModel(
    private val repository: RetroAchievementsRepository,
    val leaderboardId: Int
) : ViewModel() {

    var leaderboardData by mutableStateOf<LeaderboardEntriesResponse?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    init {
        loadLeaderboard()
    }

    fun loadLeaderboard() {
        isLoading = true
        error = null
        viewModelScope.launch {
            try {
                repository.getLeaderboardEntries(leaderboardId).onSuccess {
                    leaderboardData = it
                }.onFailure {
                    error = it.message ?: "Failed to load leaderboard"
                }
            } catch (e: Exception) {
                error = e.message ?: "An unexpected error occurred"
            } finally {
                isLoading = false
            }
        }
    }
}
