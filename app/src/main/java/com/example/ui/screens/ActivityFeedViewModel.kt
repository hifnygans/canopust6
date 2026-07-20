package com.example.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.CompletedGame
import com.example.data.api.GlobalRecentAchievement
import com.example.data.api.RecentAchievement
import com.example.data.api.pctWonDouble
import com.example.data.repository.RetroAchievementsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

sealed class ActivityItem {
    data class RecentUnlock(val achievement: RecentAchievement) : ActivityItem()
    data class MasteredGame(val game: CompletedGame) : ActivityItem()
    data class BeatenGame(val game: CompletedGame) : ActivityItem()
    data class GlobalUnlock(val achievement: GlobalRecentAchievement) : ActivityItem()
    // Leaderboard activity might be harder to get as a general "activity" without a specific endpoint
    // but we can show recent site-wide unlocks as a proxy for "Activity"
}

class ActivityFeedViewModel(
    private val repository: RetroAchievementsRepository
) : ViewModel() {

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    
    var userRecentUnlocks by mutableStateOf<List<RecentAchievement>>(emptyList())
    var masteredGames by mutableStateOf<List<CompletedGame>>(emptyList())
    var beatenGames by mutableStateOf<List<CompletedGame>>(emptyList())
    var globalRecentUnlocks by mutableStateOf<List<GlobalRecentAchievement>>(emptyList())
    var followingList by mutableStateOf<List<String>>(emptyList())
    var activePlayers by mutableStateOf<List<com.example.data.api.ActivePlayer>>(emptyList())

    init {
        loadActivity()
    }

    fun loadActivity() {
        isLoading = true
        error = null
        viewModelScope.launch {
            try {
                val recentUnlocksDeferred = async { repository.getRecentAchievements() }
                val completedGamesDeferred = async { repository.getUserCompletedGames() }
                val globalUnlocksDeferred = async { repository.getGlobalRecentAchievements(120) } // Last 2 hours
                val followingDeferred = async { repository.getUserFollowerAndFollowing() }
                val activePlayersDeferred = async { repository.getActivePlayers() }

                recentUnlocksDeferred.await().onSuccess { userRecentUnlocks = it }
                
                completedGamesDeferred.await().onSuccess { games ->
                    masteredGames = games.filter { it.pctWonDouble >= 100.0 }
                    beatenGames = games.filter { it.pctWonDouble < 100.0 && it.pctWonDouble >= 50.0 }
                }

                globalUnlocksDeferred.await().onSuccess { globalRecentUnlocks = it }
                followingDeferred.await().onSuccess { followingList = it.Following ?: emptyList() }
                activePlayersDeferred.await().onSuccess { activePlayers = it }

            } catch (e: Exception) {
                error = e.message ?: "Failed to load activity feed"
            } finally {
                isLoading = false
            }
        }
    }
}
