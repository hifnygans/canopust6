package com.example.data.repository

import com.example.data.api.AchievementOfTheWeekResponse
import com.example.data.api.AchievementUnlocksResponse
import com.example.data.api.ActiveClaimResponse
import com.example.data.api.ClaimResponse
import com.example.data.api.CompletedGame
import com.example.data.api.ConsoleResponse
import com.example.data.api.FollowerFollowingResponse
import com.example.data.api.GameAchievement
import com.example.data.api.GameInfoAndUserProgressResponse
import com.example.data.api.GameResponse
import com.example.data.api.RecentGameAwardResponse
import com.example.data.api.GlobalRecentAchievement
import com.example.data.api.LeaderboardEntriesResponse
import com.example.data.api.LeaderboardResponse
import com.example.data.api.NewsResponse
import com.example.data.api.RecentAchievement
import com.example.data.api.RetroAchievementsService
import com.example.data.api.TopUserResponse
import com.example.data.api.UserAwardsResponse
import com.example.data.api.UserSummaryResponse
import com.example.data.local.SearchDao
import com.example.data.local.SearchHistory
import com.example.data.prefs.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.concurrent.ConcurrentHashMap

class RetroAchievementsRepository(
    private val apiService: RetroAchievementsService,
    private val sessionManager: SessionManager,
    private val searchDao: SearchDao? = null
) {
    private val cache = ConcurrentHashMap<String, CachedData<*>>()
    private val CACHE_EXPIRATION_MS = 15 * 60 * 1000 // 15 minutes
    private val SUGGESTIONS_CACHE_EXPIRATION_MS = 60 * 60 * 1000 // 1 hour for suggestions

    data class CachedData<T>(
        val data: T,
        val timestamp: Long = System.currentTimeMillis()
    )

    private fun <T> getCached(key: String): T? {
        val cached = cache[key] as? CachedData<T>
        return if (cached != null && System.currentTimeMillis() - cached.timestamp < CACHE_EXPIRATION_MS) {
            cached.data
        } else {
            cache.remove(key)
            null
        }
    }

    private fun saveCache(key: String, data: Any) {
        cache[key] = CachedData(data)
    }

    val searchHistory: Flow<List<SearchHistory>>? = searchDao?.getRecentSearchHistory()

    suspend fun saveSearch(query: String, type: String) {
        searchDao?.insertSearch(SearchHistory(query = query, type = type))
    }

    suspend fun deleteSearch(id: Int) {
        searchDao?.deleteSearchById(id)
    }

    suspend fun clearHistory() {
        searchDao?.clearHistory()
    }

    suspend fun getCurrentUsername(): String? {
        return sessionManager.username.first()
    }

    suspend fun getConsoleIDs(): Result<List<ConsoleResponse>> {
        val cacheKey = "console_ids"
        getCached<List<ConsoleResponse>>(cacheKey)?.let { return Result.success(it) }

        return try {
            val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val response = apiService.getConsoleIDs(username, apiKey)
            if (response.isSuccessful && response.body() != null) {
                saveCache(cacheKey, response.body()!!)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch console list"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGameList(consoleId: Int): Result<List<GameResponse>> {
        return try {
            val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val response = apiService.getGameList(username, apiKey, consoleId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch game list"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGameInfoAndUserProgress(gameId: Int): Result<GameInfoAndUserProgressResponse> {
        return try {
            val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val response = apiService.getGameInfoAndUserProgress(username, apiKey, gameId, username)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch game info"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGameLeaderboards(gameId: Int): Result<List<LeaderboardResponse>> {
        return try {
            val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val response = apiService.getGameLeaderboards(username, apiKey, gameId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch leaderboards"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAchievementUnlocks(achievementId: Int): Result<AchievementUnlocksResponse> {
        return try {
            val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val response = apiService.getAchievementUnlocks(username, apiKey, achievementId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch achievement unlocks"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLeaderboardEntries(leaderboardId: Int): Result<LeaderboardEntriesResponse> {
        return try {
            val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val response = apiService.getLeaderboardEntries(username, apiKey, leaderboardId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch leaderboard entries"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserSummary(target: String? = null): Result<UserSummaryResponse> {
        val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
        val targetUser = target ?: username
        val cacheKey = "user_summary_$targetUser"
        getCached<UserSummaryResponse>(cacheKey)?.let { return Result.success(it) }

        return try {
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val response = apiService.getUserSummary(username, apiKey, targetUser)
            if (response.isSuccessful && response.body() != null) {
                saveCache(cacheKey, response.body()!!)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch user summary"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserAwards(target: String? = null): Result<UserAwardsResponse> {
        return try {
            val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val targetUser = target ?: username
            val response = apiService.getUserAwards(username, apiKey, targetUser)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch user awards"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecentAchievements(target: String? = null, minutes: Int = 10080): Result<List<RecentAchievement>> {
        val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
        val targetUser = target ?: username
        val cacheKey = "recent_achievements_${targetUser}_$minutes"
        getCached<List<RecentAchievement>>(cacheKey)?.let { return Result.success(it) }

        return try {
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val response = apiService.getUserRecentAchievements(username, apiKey, targetUser, minutes)
            if (response.isSuccessful && response.body() != null) {
                saveCache(cacheKey, response.body()!!)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch recent achievements"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAchievementOfTheWeek(): Result<AchievementOfTheWeekResponse> {
        val cacheKey = "aotw"
        getCached<AchievementOfTheWeekResponse>(cacheKey)?.let { return Result.success(it) }

        return try {
            val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val response = apiService.getAchievementOfTheWeek(username, apiKey)
            if (response.isSuccessful && response.body() != null) {
                saveCache(cacheKey, response.body()!!)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch AOTW"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAchievementOfTheWeekHistory(count: Int = 10): Result<List<AchievementOfTheWeekResponse>> {
        return try {
            val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val response = apiService.getAchievementOfTheWeekHistory(username, apiKey, count)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch AOTW history"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNews(count: Int = 10): Result<List<NewsResponse>> {
        val cacheKey = "news_$count"
        getCached<List<NewsResponse>>(cacheKey)?.let { return Result.success(it) }

        return try {
            val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val response = apiService.getNews(username, apiKey, count)
            if (response.isSuccessful && response.body() != null) {
                saveCache(cacheKey, response.body()!!)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch news"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActiveClaims(): Result<List<ActiveClaimResponse>> {
        val cacheKey = "active_claims"
        getCached<List<ActiveClaimResponse>>(cacheKey)?.let { return Result.success(it) }

        return try {
            val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val response = apiService.getActiveClaims(username, apiKey)
            if (response.isSuccessful && response.body() != null) {
                saveCache(cacheKey, response.body()!!)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch active claims"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClaims(claimKind: Int = 1): Result<List<ClaimResponse>> {
        val cacheKey = "claims_$claimKind"
        getCached<List<ClaimResponse>>(cacheKey)?.let { return Result.success(it) }

        return try {
            val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val response = apiService.getClaims(username, apiKey, claimKind)
            if (response.isSuccessful && response.body() != null) {
                saveCache(cacheKey, response.body()!!)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch claims"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecentGameAwards(): Result<List<RecentGameAwardResponse>> {
        val cacheKey = "recent_game_awards"
        getCached<List<RecentGameAwardResponse>>(cacheKey)?.let { return Result.success(it) }

        return try {
            val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val response = apiService.getRecentGameAwards(username, apiKey)
            if (response.isSuccessful && response.body() != null) {
                saveCache(cacheKey, response.body()!!)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch recent game awards"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTopTenUsers(): Result<List<TopUserResponse>> {
        val cacheKey = "top_ten_users"
        getCached<List<TopUserResponse>>(cacheKey)?.let { return Result.success(it) }

        return try {
            val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val response = apiService.getTopTenUsers(username, apiKey)
            if (response.isSuccessful && response.body() != null) {
                saveCache(cacheKey, response.body()!!)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch top ten users"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserFollowerAndFollowing(target: String? = null): Result<FollowerFollowingResponse> {
        return try {
            val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val targetUser = target ?: username
            val response = apiService.getUserFollowerAndFollowing(username, apiKey, targetUser)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch followers/following"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGlobalRecentAchievements(minutes: Int = 60): Result<List<GlobalRecentAchievement>> {
        val cacheKey = "global_recent_achievements_$minutes"
        getCached<List<GlobalRecentAchievement>>(cacheKey)?.let { return Result.success(it) }

        return try {
            val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val response = apiService.getGlobalRecentAchievements(username, apiKey, minutes)
            if (response.isSuccessful && response.body() != null) {
                saveCache(cacheKey, response.body()!!)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch global recent achievements"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserCompletedGames(target: String? = null): Result<List<CompletedGame>> {
        val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
        val targetUser = target ?: username
        val cacheKey = "completed_games_$targetUser"
        getCached<List<CompletedGame>>(cacheKey)?.let { return Result.success(it) }

        return try {
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val response = apiService.getUserCompletedGames(username, apiKey, targetUser)
            if (response.isSuccessful && response.body() != null) {
                saveCache(cacheKey, response.body()!!)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch completed games"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchUser(query: String): Result<UserSummaryResponse> {
        return try {
            val username = sessionManager.username.first() ?: return Result.failure(Exception("Not logged in"))
            val apiKey = sessionManager.apiKey.first() ?: return Result.failure(Exception("Not logged in"))
            val response = apiService.getUserSummary(username, apiKey, query)
            if (response.isSuccessful && response.body()?.User != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
