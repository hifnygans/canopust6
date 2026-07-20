package com.example.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.api.RetroAchievementsService
import com.example.data.api.RetrofitClient
import com.example.data.prefs.SessionManager
import com.example.data.repository.RetroAchievementsRepository
import kotlinx.coroutines.flow.first
import java.util.concurrent.atomic.AtomicInteger

class PollingWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val sharedPrefs = appContext.getSharedPreferences("polling_prefs", Context.MODE_PRIVATE)
    private val notificationHelper = NotificationHelper(appContext)
    private val notificationIdCounter = AtomicInteger(100)

    override suspend fun doWork(): Result {
        val sessionManager = SessionManager(applicationContext)
        val username = sessionManager.username.first() ?: return Result.success()
        val apiKey = sessionManager.apiKey.first() ?: return Result.success()
        
        val apiService = RetrofitClient.instance
        val repository = RetroAchievementsRepository(apiService, sessionManager)

        // 1. Check Recent Achievements
        repository.getRecentAchievements().onSuccess { achievements ->
            val lastKey = sharedPrefs.getString("last_achievement_key", "") ?: ""
            val newest = achievements.firstOrNull()
            val currentKey = newest?.let { "${it.GameTitle}_${it.Title}_${it.Date}" } ?: ""
            if (currentKey.isNotEmpty() && currentKey != lastKey) {
                if (lastKey.isNotEmpty()) {
                    notificationHelper.showNotification(
                        "Achievement Unlocked!",
                        "${newest?.Title} in ${newest?.GameTitle}",
                        notificationIdCounter.incrementAndGet()
                    )
                }
                sharedPrefs.edit().putString("last_achievement_key", currentKey).apply()
            }
        }

        // 2. Check Followers
        repository.getUserFollowerAndFollowing().onSuccess { data ->
            val lastFollowerCount = sharedPrefs.getInt("last_follower_count", -1)
            val currentCount = data.Followers?.size ?: 0
            if (lastFollowerCount != -1 && currentCount > lastFollowerCount) {
                notificationHelper.showNotification(
                    "New Follower!",
                    "Someone just followed you on RetroAchievements!",
                    notificationIdCounter.incrementAndGet()
                )
            }
            sharedPrefs.edit().putInt("last_follower_count", currentCount).apply()
        }

        // 4. Check News
        repository.getNews().onSuccess { news ->
            val lastNewsId = sharedPrefs.getString("last_news_id", "") ?: ""
            val newest = news.firstOrNull()
            if (newest != null && newest.ID != lastNewsId) {
                if (lastNewsId.isNotEmpty()) {
                    notificationHelper.showNotification(
                        "Latest News",
                        newest.Title ?: "New announcement on RetroAchievements",
                        notificationIdCounter.incrementAndGet()
                    )
                }
                sharedPrefs.edit().putString("last_news_id", newest.ID ?: "").apply()
            }
        }

        // 5. Check Events (Active Claims)
        repository.getActiveClaims().onSuccess { claims ->
            val lastClaimCount = sharedPrefs.getInt("last_claim_count", -1)
            val currentCount = claims.size
            if (lastClaimCount != -1 && currentCount > lastClaimCount) {
                val newest = claims.firstOrNull()
                notificationHelper.showNotification(
                    "New Event!",
                    "New active claim: ${newest?.GameTitle ?: "Check out new events"}",
                    notificationIdCounter.incrementAndGet()
                )
            }
            sharedPrefs.edit().putInt("last_claim_count", currentCount).apply()
        }

        return Result.success()
    }
}
