package com.example.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.AchievementUnlocksResponse
import com.example.data.api.GameInfoAndUserProgressResponse
import com.example.data.repository.RetroAchievementsRepository
import kotlinx.coroutines.launch

class AchievementDetailViewModel(
    private val repository: RetroAchievementsRepository,
    val achievementId: Int,
    val gameId: Int
) : ViewModel() {

    var achievementData by mutableStateOf<AchievementUnlocksResponse?>(null)
        private set
    var gameData by mutableStateOf<GameInfoAndUserProgressResponse?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    init {
        loadData()
    }

    fun loadData() {
        isLoading = true
        error = null
        viewModelScope.launch {
            try {
                repository.getAchievementUnlocks(achievementId).onSuccess { achievementData = it }
                repository.getGameInfoAndUserProgress(gameId).onSuccess { gameData = it }
            } catch (e: Exception) {
                error = e.message ?: "Failed to load achievement details"
            } finally {
                isLoading = false
            }
        }
    }
}
