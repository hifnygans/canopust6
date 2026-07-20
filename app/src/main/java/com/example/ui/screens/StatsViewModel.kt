package com.example.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.ActiveUserResponse
import com.example.data.api.CompletedGame
import com.example.data.api.GlobalRecentAchievement
import com.example.data.api.RecentAchievement
import com.example.data.api.TopUserResponse
import com.example.data.api.UserSummaryResponse
import com.example.data.api.AchievementOfTheWeekResponse
import com.example.data.api.ActiveClaimResponse
import com.example.data.api.NewsResponse
import com.example.data.api.GameSuggestionResponse
import com.example.data.repository.RetroAchievementsRepository
import kotlinx.coroutines.launch

class StatsViewModel(private val repository: RetroAchievementsRepository) : ViewModel() {

    var userSummary by mutableStateOf<UserSummaryResponse?>(null)
    var recentAchievements by mutableStateOf<List<RecentAchievement>>(emptyList())
    var completedGames by mutableStateOf<List<CompletedGame>>(emptyList())
    var topUsers by mutableStateOf<List<TopUserResponse>>(emptyList())
    var globalRecentAchievements by mutableStateOf<List<GlobalRecentAchievement>>(emptyList())
    var achievementOfTheWeek by mutableStateOf<AchievementOfTheWeekResponse?>(null)
    var activeClaims by mutableStateOf<List<ActiveClaimResponse>>(emptyList())
    var news by mutableStateOf<List<NewsResponse>>(emptyList())
    
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    init {
        loadStats()
    }

    fun loadStats() {
        isLoading = true
        error = null
        viewModelScope.launch {
            try {
                // Fetch basic user info
                repository.getUserSummary().onSuccess { userSummary = it }
                
                // Fetch recent achievements (last 30 days roughly - 43200 minutes)
                repository.getRecentAchievements(minutes = 43200).onSuccess { 
                    recentAchievements = it 
                }
                
                // Fetch completed games for console stats
                repository.getUserCompletedGames().onSuccess { 
                    completedGames = it 
                }
                
                // Fetch global top 10
                repository.getTopTenUsers().onSuccess { 
                    topUsers = it 
                }

                // Fetch global recent achievements (last 24 hours - 1440 minutes)
                repository.getGlobalRecentAchievements(minutes = 1440).onSuccess {
                    globalRecentAchievements = it
                }

                // Fetch Achievement of the Week
                repository.getAchievementOfTheWeek().onSuccess {
                    achievementOfTheWeek = it
                }

                // Fetch Active Claims
                repository.getActiveClaims().onSuccess {
                    activeClaims = it
                }

                // Fetch News
                repository.getNews(count = 5).onSuccess {
                    news = it
                }.onFailure {
                    // Non-fatal error
                }
                
            } catch (e: Exception) {
                error = e.message ?: "An unexpected error occurred"
            } finally {
                isLoading = false
            }
        }
    }
}
