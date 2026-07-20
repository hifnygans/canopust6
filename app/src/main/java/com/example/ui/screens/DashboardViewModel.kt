package com.example.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.AchievementOfTheWeekResponse
import com.example.data.api.ActiveClaimResponse
import com.example.data.api.NewsResponse
import com.example.data.api.RecentAchievement
import com.example.data.api.UserSummaryResponse
import com.example.data.repository.RetroAchievementsRepository
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: RetroAchievementsRepository
) : ViewModel() {

    var userSummary by mutableStateOf<UserSummaryResponse?>(null)
        private set
    var recentAchievements by mutableStateOf<List<RecentAchievement>>(emptyList())
        private set
    var aotw by mutableStateOf<AchievementOfTheWeekResponse?>(null)
        private set
    var aotwHistory by mutableStateOf<List<AchievementOfTheWeekResponse>>(emptyList())
        private set
    var news by mutableStateOf<List<NewsResponse>>(emptyList())
        private set
    var activeClaims by mutableStateOf<List<ActiveClaimResponse>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    init {
        refreshDashboard()
    }

    fun refreshDashboard() {
        isLoading = true
        error = null
        viewModelScope.launch {
            try {
                repository.getUserSummary().onSuccess { userSummary = it }.onFailure { error = it.message }
                repository.getRecentAchievements().onSuccess { recentAchievements = it }.onFailure { error = it.message }
                repository.getAchievementOfTheWeek().onSuccess { aotw = it }.onFailure { error = it.message }
                repository.getAchievementOfTheWeekHistory().onSuccess { aotwHistory = it }.onFailure { error = it.message }
                repository.getNews(count = 30).onSuccess { news = it }.onFailure { error = it.message }
                repository.getActiveClaims().onSuccess { activeClaims = it }.onFailure { error = it.message }
            } catch (e: Exception) {
                error = e.message ?: "Failed to refresh dashboard"
            } finally {
                isLoading = false
            }
        }
    }
}
