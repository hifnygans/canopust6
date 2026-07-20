package com.example.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GameAchievement
import com.example.data.api.GameInfoAndUserProgressResponse
import com.example.data.api.LeaderboardResponse
import com.example.data.repository.RetroAchievementsRepository
import kotlinx.coroutines.launch

enum class AchievementFilter { All, Unlocked, Locked }
enum class AchievementSort { Default, Points, UnlockDate }

class GameDetailViewModel(
    private val repository: RetroAchievementsRepository,
    val gameId: Int
) : ViewModel() {

    var gameDetail by mutableStateOf<GameInfoAndUserProgressResponse?>(null)
        private set
    var leaderboards by mutableStateOf<List<LeaderboardResponse>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    var filter by mutableStateOf(AchievementFilter.All)
    var sort by mutableStateOf(AchievementSort.Default)

    init {
        loadGameDetail()
    }

    fun getFilteredAchievements(): List<GameAchievement> {
        val list = gameDetail?.Achievements?.values?.toList() ?: emptyList()
        
        val filtered = when (filter) {
            AchievementFilter.All -> list
            AchievementFilter.Unlocked -> list.filter { !it.DateEarned.isNullOrBlank() }
            AchievementFilter.Locked -> list.filter { it.DateEarned.isNullOrBlank() }
        }

        return when (sort) {
            AchievementSort.Default -> filtered.sortedBy { it.DisplayOrder }
            AchievementSort.Points -> filtered.sortedByDescending { it.Points }
            AchievementSort.UnlockDate -> filtered.sortedByDescending { it.DateEarned ?: "" }
        }
    }

    fun loadGameDetail() {
        isLoading = true
        error = null
        viewModelScope.launch {
            try {
                repository.getGameInfoAndUserProgress(gameId).onSuccess { gameDetail = it }
                repository.getGameLeaderboards(gameId).onSuccess { leaderboards = it }
            } catch (e: Exception) {
                error = e.message ?: "Failed to load game details"
            } finally {
                isLoading = false
            }
        }
    }
}
