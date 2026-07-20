package com.example.data.repository

import com.example.data.api.RetroAchievementsService
import com.example.data.api.UserSummaryResponse
import com.example.data.prefs.SessionManager
import kotlinx.coroutines.flow.first

class AuthRepository(
    private val apiService: RetroAchievementsService,
    private val sessionManager: SessionManager
) {
    suspend fun login(username: String, apiKey: String): Result<UserSummaryResponse> {
        return try {
            val response = apiService.getUserSummary(username, apiKey, username)
            if (response.isSuccessful && response.body()?.User != null) {
                sessionManager.saveSession(username, apiKey)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Invalid credentials or user not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
