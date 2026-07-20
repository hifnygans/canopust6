package com.example.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.RetroAchievementsRepository
import kotlinx.coroutines.launch

class FollowListViewModel(
    private val repository: RetroAchievementsRepository,
    private val targetUser: String,
    private val isFollowers: Boolean
) : ViewModel() {

    var users by mutableStateOf<List<String>>(emptyList())
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    init {
        loadUsers()
    }

    fun loadUsers() {
        isLoading = true
        error = null
        viewModelScope.launch {
            repository.getUserFollowerAndFollowing(targetUser).onSuccess {
                users = if (isFollowers) it.Followers ?: emptyList() else it.Following ?: emptyList()
            }.onFailure {
                error = it.message ?: "Failed to load users"
            }
            isLoading = false
        }
    }
}
