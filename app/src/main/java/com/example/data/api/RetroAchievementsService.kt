package com.example.data.api

import com.squareup.moshi.Json
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetroAchievementsService {
    @GET("API_GetUserSummary.php")
    suspend fun getUserSummary(
        @Query("z") username: String,
        @Query("y") apiKey: String,
        @Query("u") targetUser: String
    ): Response<UserSummaryResponse>

    @GET("API_GetUserRecentAchievements.php")
    suspend fun getUserRecentAchievements(
        @Query("z") username: String,
        @Query("y") apiKey: String,
        @Query("u") targetUser: String,
        @Query("m") minutes: Int = 10080 // 7 days
    ): Response<List<RecentAchievement>>

    @GET("API_GetAchievementOfTheWeek.php")
    suspend fun getAchievementOfTheWeek(
        @Query("z") username: String,
        @Query("y") apiKey: String
    ): Response<AchievementOfTheWeekResponse>

    @GET("API_GetUserAwards.php")
    suspend fun getUserAwards(
        @Query("z") username: String,
        @Query("y") apiKey: String,
        @Query("u") targetUser: String
    ): Response<UserAwardsResponse>

    @GET("API_GetConsoleIDs.php")
    suspend fun getConsoleIDs(
        @Query("z") username: String,
        @Query("y") apiKey: String
    ): Response<List<ConsoleResponse>>

    @GET("API_GetGameList.php")
    suspend fun getGameList(
        @Query("z") username: String,
        @Query("y") apiKey: String,
        @Query("i") consoleId: Int
    ): Response<List<GameResponse>>

    @GET("API_GetGameInfoAndUserProgress.php")
    suspend fun getGameInfoAndUserProgress(
        @Query("z") username: String,
        @Query("y") apiKey: String,
        @Query("g") gameId: Int,
        @Query("u") targetUser: String
    ): Response<GameInfoAndUserProgressResponse>

    @GET("API_GetGameLeaderboards.php")
    suspend fun getGameLeaderboards(
        @Query("z") username: String,
        @Query("y") apiKey: String,
        @Query("g") gameId: Int
    ): Response<List<LeaderboardResponse>>

    @GET("API_GetAchievementUnlocks.php")
    suspend fun getAchievementUnlocks(
        @Query("z") username: String,
        @Query("y") apiKey: String,
        @Query("a") achievementId: Int,
        @Query("o") offset: Int = 0,
        @Query("c") count: Int = 50
    ): Response<AchievementUnlocksResponse>

    @GET("API_GetLeaderboard.php")
    suspend fun getLeaderboardEntries(
        @Query("z") username: String,
        @Query("y") apiKey: String,
        @Query("i") leaderboardId: Int,
        @Query("o") offset: Int = 0,
        @Query("c") count: Int = 100
    ): Response<LeaderboardEntriesResponse>

    @GET("API_GetNews.php")
    suspend fun getNews(
        @Query("z") username: String,
        @Query("y") apiKey: String,
        @Query("c") count: Int = 10
    ): Response<List<NewsResponse>>

    @GET("API_GetActiveClaims.php")
    suspend fun getActiveClaims(
        @Query("z") username: String,
        @Query("y") apiKey: String
    ): Response<List<ActiveClaimResponse>>

    @GET("API_GetAchievementOfTheWeekHistory.php")
    suspend fun getAchievementOfTheWeekHistory(
        @Query("z") username: String,
        @Query("y") apiKey: String,
        @Query("c") count: Int = 10
    ): Response<List<AchievementOfTheWeekResponse>>

    @GET("API_GetUserFollowerAndFollowing.php")
    suspend fun getUserFollowerAndFollowing(
        @Query("z") username: String,
        @Query("y") apiKey: String,
        @Query("u") targetUser: String
    ): Response<FollowerFollowingResponse>

    @GET("API_GetRecentAchievements.php")
    suspend fun getGlobalRecentAchievements(
        @Query("z") username: String,
        @Query("y") apiKey: String,
        @Query("m") minutes: Int = 60
    ): Response<List<GlobalRecentAchievement>>

    @GET("API_GetUserCompletedGames.php")
    suspend fun getUserCompletedGames(
        @Query("z") username: String,
        @Query("y") apiKey: String,
        @Query("u") targetUser: String
    ): Response<List<CompletedGame>>

    @GET("API_GetTopTenUsers.php")
    suspend fun getTopTenUsers(
        @Query("z") username: String,
        @Query("y") apiKey: String
    ): Response<List<TopUserResponse>>

    @GET("API_GetActiveUsers.php")
    suspend fun getActiveUsers(
        @Query("z") username: String,
        @Query("y") apiKey: String
    ): Response<List<ActiveUserResponse>>

    @GET("API_GetClaims.php")
    suspend fun getClaims(
        @Query("z") username: String,
        @Query("y") apiKey: String,
        @Query("k") claimKind: Int = 1
    ): Response<List<ClaimResponse>>

    @GET("API_GetRecentGameAwards.php")
    suspend fun getRecentGameAwards(
        @Query("z") username: String,
        @Query("y") apiKey: String
    ): Response<List<RecentGameAwardResponse>>

    @GET("API_GetActivePlayers.php")
    suspend fun getActivePlayers(
        @Query("z") username: String,
        @Query("y") apiKey: String
    ): Response<List<ActivePlayer>>
}

data class ActivePlayer(
    @Json(name = "User") val User: String? = null,
    @Json(name = "RichPresenceMsg") val RichPresenceMsg: String? = null,
    @Json(name = "GameID") val GameID: Int? = null,
    @Json(name = "GameTitle") val GameTitle: String? = null,
    @Json(name = "ConsoleName") val ConsoleName: String? = null,
    @Json(name = "LastActivity") val LastActivity: String? = null
)

data class GameSuggestionResponse(
    @Json(name = "ID") val ID: Any? = null,
    @Json(name = "GameID") val GameID: Any? = null,
    @Json(name = "Title") val Title: String? = null,
    @Json(name = "ConsoleName") val ConsoleName: String? = null,
    @Json(name = "ImageIcon") val ImageIcon: String? = null,
    @Json(name = "IconName") val IconName: String? = null,
    @Json(name = "NumAchievements") val NumAchievements: Any? = null,
    @Json(name = "Points") val Points: Any? = null,
    @Json(name = "NumDistinctPlayers") val NumDistinctPlayers: Any? = null,
    @Json(name = "Reasoning") val Reasoning: String? = null,
    @Json(name = "Progress") val Progress: Any? = null
)

data class ActiveUserResponse(
    @Json(name = "User") val User: String? = null,
    @Json(name = "LastActivity") val LastActivity: String? = null,
    @Json(name = "LastGameID") val LastGameID: Int? = null,
    @Json(name = "LastGameTitle") val LastGameTitle: String? = null
)

data class TopUserResponse(
    @Json(name = "1") val User: String? = null,
    @Json(name = "2") val Points: Int? = null,
    @Json(name = "3") val TruePoints: Int? = null
)

data class CompletedGame(
    @Json(name = "GameID") val GameID: Int? = null,
    @Json(name = "Title") val Title: String? = null,
    @Json(name = "ImageIcon") val ImageIcon: String? = null,
    @Json(name = "ConsoleID") val ConsoleID: Int? = null,
    @Json(name = "ConsoleName") val ConsoleName: String? = null,
    @Json(name = "MaxPoints") val MaxPoints: Int? = null,
    @Json(name = "NumAchievements") val NumAchievements: Int? = null,
    @Json(name = "PctWon") val PctWon: String? = null,
    @Json(name = "HardcoreMode") val HardcoreMode: Int? = null
)

val CompletedGame.pctWonDouble: Double
    get() {
        val raw = PctWon?.replace("%", "")?.toDoubleOrNull() ?: 0.0
        // Handle both 0.0-1.0 and 0-100 formats. If it's <= 1.0 but doesn't have a % sign, it's likely a decimal.
        return if (raw <= 1.0 && raw > 0.0 && !(PctWon?.contains("%") ?: false)) raw * 100.0 else raw
    }


data class GlobalRecentAchievement(
    @Json(name = "Date") val Date: String? = null,
    @Json(name = "User") val User: String? = null,
    @Json(name = "GameID") val GameID: Int? = null,
    @Json(name = "GameTitle") val GameTitle: String? = null,
    @Json(name = "AchievementID") val AchievementID: Int? = null,
    @Json(name = "AchievementTitle") val AchievementTitle: String? = null,
    @Json(name = "AchievementDescription") val AchievementDescription: String? = null,
    @Json(name = "BadgeName") val BadgeName: String? = null,
    @Json(name = "Points") val Points: Int? = null,
    @Json(name = "Type") val Type: String? = null // "1" for normal, "2" for hardcore
)

data class FollowerFollowingResponse(
    @Json(name = "Followers") val Followers: List<String>? = null,
    @Json(name = "Following") val Following: List<String>? = null
)

data class NewsResponse(
    @Json(name = "ID") val ID: String? = null,
    @Json(name = "Title") val Title: String? = null,
    @Json(name = "Image") val Image: String? = null,
    @Json(name = "Link") val Link: String? = null,
    @Json(name = "PostedAt") val PostedAt: String? = null,
    @Json(name = "Author") val Author: String? = null,
    @Json(name = "Category") val Category: String? = null
)

data class ActiveClaimResponse(
    @Json(name = "ID") val ID: Int? = null,
    @Json(name = "User") val User: String? = null,
    @Json(name = "GameID") val GameID: Int? = null,
    @Json(name = "GameTitle") val GameTitle: String? = null,
    @Json(name = "GameIcon") val GameIcon: String? = null,
    @Json(name = "ConsoleName") val ConsoleName: String? = null,
    @Json(name = "ClaimType") val ClaimType: Int? = null,
    @Json(name = "SetType") val SetType: Int? = null,
    @Json(name = "Expiration") val Expiration: String? = null,
    @Json(name = "Created") val Created: String? = null,
    @Json(name = "Done") val Done: Int? = null
)

data class ClaimResponse(
    @Json(name = "ID") val ID: Int? = null,
    @Json(name = "User") val User: String? = null,
    @Json(name = "GameID") val GameID: Int? = null,
    @Json(name = "GameTitle") val GameTitle: String? = null,
    @Json(name = "GameIcon") val GameIcon: String? = null,
    @Json(name = "ConsoleName") val ConsoleName: String? = null,
    @Json(name = "ClaimType") val ClaimType: Int? = null,
    @Json(name = "SetType") val SetType: Int? = null,
    @Json(name = "Expiration") val Expiration: String? = null,
    @Json(name = "Created") val Created: String? = null,
    @Json(name = "Done") val Done: Int? = null
)

data class RecentGameAwardResponse(
    @Json(name = "User") val User: String? = null,
    @Json(name = "AwardType") val AwardType: String? = null,
    @Json(name = "AwardDate") val AwardDate: String? = null,
    @Json(name = "GameID") val GameID: Int? = null,
    @Json(name = "GameTitle") val GameTitle: String? = null,
    @Json(name = "ConsoleName") val ConsoleName: String? = null,
    @Json(name = "BadgeName") val BadgeName: String? = null
)

data class LeaderboardEntriesResponse(
    val ID: Int? = null,
    val Title: String? = null,
    val Description: String? = null,
    val Format: String? = null,
    val LowerIsBetter: Int? = null,
    val GameID: Int? = null,
    val GameTitle: String? = null,
    val ConsoleID: Int? = null,
    val ConsoleName: String? = null,
    val Entries: List<LeaderboardEntry>? = null
)

data class LeaderboardEntry(
    val Rank: Int? = null,
    val User: String? = null,
    val Score: Int? = null,
    val DateSubmitted: String? = null
)

data class AchievementUnlocksResponse(
    val Achievement: AchievementDetailInfo? = null,
    val Unlocks: List<AchievementUnlock>? = null,
    val TotalUnlocks: Int? = null
)

data class AchievementDetailInfo(
    val ID: Int? = null,
    val Title: String? = null,
    val Description: String? = null,
    val Points: Int? = null,
    val TrueRatio: Int? = null,
    val Author: String? = null,
    val DateCreated: String? = null,
    val DateModified: String? = null,
    val BadgeName: String? = null,
    val GameID: Int? = null,
    val GameTitle: String? = null,
    val ConsoleID: Int? = null,
    val ConsoleName: String? = null
)

data class AchievementUnlock(
    val User: String? = null,
    val DateAwarded: String? = null,
    val HardcoreMode: Int? = null
)

data class LeaderboardResponse(
    val ID: Int? = null,
    val Title: String? = null,
    val Description: String? = null,
    val Format: String? = null,
    val LowerIsBetter: Int? = null
)

data class GameInfoAndUserProgressResponse(
    val ID: Int? = null,
    val Title: String? = null,
    val ConsoleID: Int? = null,
    val ForumTopicID: Int? = null,
    val ImageIcon: String? = null,
    val ImageTitle: String? = null,
    val ImageIngame: String? = null,
    val ImageBoxArt: String? = null,
    val Publisher: String? = null,
    val Developer: String? = null,
    val Genre: String? = null,
    val Released: String? = null,
    val ConsoleName: String? = null,
    val RichPresencePatch: String? = null,
    val NumPlayers: Int? = null,
    val NumAchievements: Int? = null,
    val Achievements: Map<String, GameAchievement>? = null
)

data class GameAchievement(
    val ID: Int? = null,
    val NumAwarded: Int? = null,
    val NumAwardedHardcore: Int? = null,
    val Title: String? = null,
    val Description: String? = null,
    val Points: Int? = null,
    val TrueRatio: Int? = null,
    val Author: String? = null,
    val DateCreated: String? = null,
    val DateModified: String? = null,
    val BadgeName: String? = null,
    val DisplayOrder: Int? = null,
    val MemAddr: String? = null,
    val DateEarned: String? = null,
    val DateEarnedHardcore: String? = null
)

data class ConsoleResponse(
    val ID: Int? = null,
    val Name: String? = null
)

data class GameResponse(
    val Title: String? = null,
    val ID: Int? = null,
    val ConsoleID: Int? = null,
    val ConsoleName: String? = null,
    val ImageIcon: String? = null,
    val ImageTitle: String? = null,
    val ImageIngame: String? = null,
    val ImageBoxArt: String? = null,
    val Publisher: String? = null,
    val Developer: String? = null,
    val Genre: String? = null,
    val Released: String? = null
)

data class UserAwardsResponse(
    val VisibleUserAwards: List<Award>? = null,
    val HiddenUserAwards: List<Award>? = null,
    val MasteredCount: Int? = null,
    val BeatenCount: Int? = null
)

data class Award(
    val AwardType: String? = null,
    val AwardDate: String? = null,
    val AwardData: String? = null,
    val AwardData2: String? = null,
    val GameID: Int? = null,
    val GameTitle: String? = null,
    val BadgeName: String? = null
)

data class UserSummaryResponse(
    @Json(name = "User") val User: String? = null,
    @Json(name = "UserPic") val UserPic: String? = null,
    @Json(name = "TotalPoints") val TotalPoints: Int? = null,
    @Json(name = "TotalTruePoints") val TotalTruePoints: Int? = null,
    @Json(name = "Rank") val Rank: String? = null,
    @Json(name = "Motto") val Motto: String? = null,
    @Json(name = "MemberSince") val MemberSince: String? = null,
    @Json(name = "LastActivity") val LastActivity: LastActivity? = null,
    @Json(name = "RecentlyPlayed") val RecentlyPlayed: List<RecentlyPlayedGame>? = null,
    @Json(name = "LastGameID") val LastGameID: Int? = null,
    @Json(name = "Status") val Status: String? = null
)

data class LastActivity(
    val ID: Int? = null,
    val User: String? = null,
    val LastActivity: String? = null
)

data class RecentlyPlayedGame(
    @Json(name = "GameID") val GameID: Int? = null,
    @Json(name = "ConsoleID") val ConsoleID: Int? = null,
    @Json(name = "ConsoleName") val ConsoleName: String? = null,
    @Json(name = "Title") val Title: String? = null,
    @Json(name = "ImageIcon") val ImageIcon: String? = null,
    @Json(name = "LastPlayed") val LastPlayed: String? = null,
    @Json(name = "NumPossibleAchievements") val NumPossibleAchievements: Int? = null,
    @Json(name = "NumAchieved") val NumAchieved: Int? = null,
    @Json(name = "ScoreAchieved") val ScoreAchieved: Int? = null
)

data class RecentAchievement(
    val Date: String? = null,
    val GameTitle: String? = null,
    val Title: String? = null,
    val Description: String? = null,
    val BadgeName: String? = null,
    val Points: Int? = null
)

data class AchievementOfTheWeekResponse(
    val Achievement: AchievementDetail? = null,
    val Game: GameDetail? = null,
    val StartAt: String? = null,
    val TotalPlayers: Int? = null
)

data class AchievementDetail(
    val ID: Int? = null,
    val Title: String? = null,
    val Description: String? = null,
    val Points: Int? = null,
    val BadgeName: String? = null
)

data class GameDetail(
    val ID: Int? = null,
    val Title: String? = null,
    val IconName: String? = null
)
