package com.example.ui.screens

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.CompletedGame
import com.example.data.api.RecentAchievement
import com.example.data.api.UserAwardsResponse
import com.example.data.api.UserSummaryResponse
import com.example.data.api.pctWonDouble
import com.example.data.repository.RetroAchievementsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class ProfileGrouping {
    ALL, CONSOLE, GAME
}

enum class LayoutType {
    LIST, GRID, COMPACT
}

class ProfileViewModel(
    private val repository: RetroAchievementsRepository,
    private val username: String?
) : ViewModel() {

    var userSummary by mutableStateOf<UserSummaryResponse?>(null)
        private set
    var userAwards by mutableStateOf<UserAwardsResponse?>(null)
        private set
    var recentAchievements by mutableStateOf<List<RecentAchievement>>(emptyList())
        private set
    var completedGames by mutableStateOf<List<CompletedGame>>(emptyList())
        private set
    
    var gameAchievements = mutableStateMapOf<Int, List<com.example.data.api.GameAchievement>>()
        private set

    var isLoading by mutableStateOf(false)
        private set
    
    var loadingGames = mutableStateMapOf<Int, Boolean>()
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var isOwnProfile by mutableStateOf(false)
        private set

    var groupingType by mutableStateOf(ProfileGrouping.ALL)
    var layoutType by mutableStateOf(LayoutType.LIST)

    val masteredGames by derivedStateOf {
        completedGames.filter { it.pctWonDouble >= 100.0 }
    }
    
    val beatenGames by derivedStateOf {
        completedGames.filter { it.pctWonDouble < 100.0 && it.pctWonDouble >= 50.0 }
    }

    init {
        loadProfile()
    }

    fun loadProfile() {
        isLoading = true
        error = null
        viewModelScope.launch {
            try {
                val currentLoggedInUser = repository.getCurrentUsername()
                isOwnProfile = username == null || username == currentLoggedInUser

                repository.getUserSummary(username).onSuccess { userSummary = it }
                repository.getUserAwards(username).onSuccess { userAwards = it }
                // Increase minutes to get more achievements for "ALL" view
                repository.getRecentAchievements(username, minutes = 525600).onSuccess { recentAchievements = it } 
                repository.getUserCompletedGames(username).onSuccess { completedGames = it }
            } catch (e: Exception) {
                error = e.message ?: "Failed to load profile"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadGameAchievements(gameId: Int) {
        if (gameAchievements.containsKey(gameId)) return
        
        loadingGames[gameId] = true
        viewModelScope.launch {
            try {
                repository.getGameInfoAndUserProgress(gameId).onSuccess { response ->
                    gameAchievements[gameId] = response.Achievements?.values?.toList() ?: emptyList()
                }
            } catch (e: Exception) {
                // Ignore error for now
            } finally {
                loadingGames[gameId] = false
            }
        }
    }
}
