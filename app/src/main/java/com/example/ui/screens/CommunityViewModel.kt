package com.example.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.ActiveClaimResponse
import com.example.data.api.ClaimResponse
import com.example.data.api.RecentGameAwardResponse
import com.example.data.api.TopUserResponse
import com.example.data.repository.RetroAchievementsRepository
import kotlinx.coroutines.launch

class CommunityViewModel(private val repository: RetroAchievementsRepository) : ViewModel() {
    var activeClaims by mutableStateOf<List<ActiveClaimResponse>>(emptyList())
    var completedClaims by mutableStateOf<List<ClaimResponse>>(emptyList())
    var topUsers by mutableStateOf<List<TopUserResponse>>(emptyList())
    var recentGameAwards by mutableStateOf<List<RecentGameAwardResponse>>(emptyList())
    
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                // Fetch active claims
                repository.getActiveClaims().onSuccess {
                    activeClaims = it
                }

                // Fetch top 10 users
                repository.getTopTenUsers().onSuccess {
                    topUsers = it
                }

                // Fetch recent game awards
                repository.getRecentGameAwards().onSuccess {
                    recentGameAwards = it
                }
                
                // Fetch some completed claims (kind 1)
                repository.getClaims(claimKind = 1).onSuccess {
                    completedClaims = it
                }

            } catch (e: Exception) {
                error = e.message ?: "An unexpected error occurred"
            } finally {
                isLoading = false
            }
        }
    }
}
