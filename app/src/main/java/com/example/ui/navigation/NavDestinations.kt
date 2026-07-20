package com.example.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Splash : Screen
    @Serializable
    data object Login : Screen
    @Serializable
    data object Main : Screen
    @Serializable
    data object Settings : Screen
    @Serializable
    data object Search : Screen
    @Serializable
    data class Profile(val username: String? = null) : Screen
    @Serializable
    data class GameDetail(val gameId: Int) : Screen
    @Serializable
    data class AchievementDetail(val achievementId: Int, val gameId: Int) : Screen
    @Serializable
    data class LeaderboardDetail(val leaderboardId: Int) : Screen
    @Serializable
    object AotWHistory : Screen
    @Serializable
    object Events : Screen
    @Serializable
    object About : Screen
    @Serializable
    object ActivityFeed : Screen
    @Serializable
    data class FollowList(val targetUser: String, val isFollowers: Boolean) : Screen
    @Serializable
    object Stats : Screen
    @Serializable
    object Community : Screen
    @Serializable
    object Emulator : Screen
}
