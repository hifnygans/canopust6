package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.example.ui.components.RecentAchievementItem
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: DashboardViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToProfile: (String?) -> Unit,
    onNavigateToGame: (Int) -> Unit,
    onNavigateToAotWHistory: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToActivityFeed: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    val user = viewModel.userSummary
    val recent = viewModel.recentAchievements
    val aotw = viewModel.aotw
    val news = viewModel.news
    val activeClaims = viewModel.activeClaims

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    androidx.compose.foundation.layout.Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        androidx.compose.material3.Icon(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp).padding(end = 12.dp),
                            tint = androidx.compose.ui.graphics.Color.Unspecified
                        )
                        Column {
                            Text(
                                "CANOPUS T6",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            )
                            Text(
                                stringResource(id = com.example.R.string.app_tagline),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        if (viewModel.isLoading && user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp)
            ) {
                // Hero Section: User Status
                item {
                    UserHeroHeader(user, onClick = { onNavigateToProfile(null) })
                }

                // Quick Stats
                item {
                    AdvancedStatsRow(user)
                }

                // Featured: Achievement of the Week
                if (aotw != null) {
                    item {
                        FeaturedAotW(aotw, onNavigateToGame)
                    }
                }

                // Recently Played Games
                val recentlyPlayed = user?.RecentlyPlayed
                if (!recentlyPlayed.isNullOrEmpty()) {
                    item {
                        SectionHeader("Recently Played", Icons.Default.Event)
                    }
                    items(recentlyPlayed.take(3)) { game ->
                        ModernGameItem(game, onNavigateToGame)
                    }
                }

                // Active Players
                if (viewModel.activePlayers.isNotEmpty()) {
                    item {
                        SectionHeader("Active Players", Icons.Default.People)
                    }
                    item {
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(viewModel.activePlayers.take(15)) { player ->
                                ActivePlayerPill(player)
                            }
                        }
                    }
                }

                // Recent Activity Feed
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionHeader("Activity", Icons.Default.Notifications)
                        TextButton(onClick = onNavigateToActivityFeed) {
                            Text("View All", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }

                if (recent.isEmpty()) {
                    item {
                        EmptyStateCard("No recent activities.")
                    }
                } else {
                    items(recent.take(4)) { achievement ->
                        ModernActivityItem(achievement)
                    }
                }

                // Site News
                if (news.isNotEmpty()) {
                    item {
                        SectionHeader("Site News", Icons.Default.Info)
                    }
                    items(news.take(3)) { newsItem ->
                        ModernNewsCard(newsItem)
                    }
                }
            }
        }
    }
}

@Composable
fun UserHeroHeader(user: com.example.data.api.UserSummaryResponse?, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                AsyncImage(
                    model = "https://retroachievements.org${user?.UserPic}",
                    contentDescription = null,
                    modifier = Modifier.size(80.dp).clip(CircleShape).border(4.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentScale = ContentScale.Crop
                )
                if (user?.Status == "Online") {
                    Box(
                        modifier = Modifier.size(16.dp).background(Color(0xFF4CAF50), CircleShape).border(2.dp, MaterialTheme.colorScheme.surface, CircleShape).align(Alignment.BottomEnd)
                    )
                }
            }
            Spacer(Modifier.width(20.dp))
            Column {
                Text(user?.User ?: "Gamer", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                Text(user?.Motto ?: "Retro Legend", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = 0.7f, // Mock level progress
                    modifier = Modifier.width(120.dp).height(8.dp).clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}

@Composable
fun AdvancedStatsRow(user: com.example.data.api.UserSummaryResponse?) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatPill(Icons.Default.Stars, user?.TotalPoints?.toString() ?: "0", "Pts", Modifier.weight(1f))
        StatPill(Icons.Default.EmojiEvents, user?.TotalTruePoints?.toString() ?: "0", "True", Modifier.weight(1f))
        StatPill(Icons.Default.BarChart, "#${user?.Rank ?: "0"}", "Rank", Modifier.weight(1f))
    }
}

@Composable
fun StatPill(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String, modifier: Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = CircleShape
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black)
                Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 8.sp)
            }
        }
    }
}

@Composable
fun FeaturedAotW(aotw: com.example.data.api.AchievementOfTheWeekResponse, onNavigateToGame: (Int) -> Unit) {
    Card(
        onClick = { aotw.Game?.ID?.let { onNavigateToGame(it) } },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = androidx.compose.ui.graphics.Color.Unspecified
                )
                Spacer(Modifier.width(8.dp))
                Text("ACHIEVEMENT OF THE WEEK", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = "https://retroachievements.org/Badge/${aotw.Achievement?.BadgeName}.png",
                    contentDescription = null,
                    modifier = Modifier.size(64.dp).clip(MaterialTheme.shapes.medium)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(aotw.Achievement?.Title ?: "Unknown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(aotw.Game?.Title ?: "Game", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun ModernGameItem(game: com.example.data.api.RecentlyPlayedGame, onNavigateToGame: (Int) -> Unit) {
    val progress = if ((game.NumPossibleAchievements ?: 0) > 0) {
        (game.NumAchieved ?: 0).toFloat() / (game.NumPossibleAchievements ?: 0).toFloat()
    } else 0f
    
    Surface(
        onClick = { game.GameID?.let { onNavigateToGame(it) } },
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = "https://retroachievements.org${game.ImageIcon}",
                contentDescription = null,
                modifier = Modifier.size(48.dp).clip(MaterialTheme.shapes.small)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(game.Title ?: "Game", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            Text("${game.NumAchieved}/${game.NumPossibleAchievements}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun ModernActivityItem(achievement: com.example.data.api.RecentAchievement) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = "https://retroachievements.org/Badge/${achievement.BadgeName}.png",
            contentDescription = null,
            modifier = Modifier.size(40.dp).clip(CircleShape)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(achievement.Title ?: "Unlocked", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text("${achievement.GameTitle} • ${achievement.Date?.take(10)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun ActivePlayerPill(player: com.example.data.api.ActivePlayer) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.width(240.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                AsyncImage(
                    model = "https://retroachievements.org/UserPic/${player.User}.png",
                    contentDescription = null,
                    modifier = Modifier.size(44.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    player.User ?: "User",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val statusText = if (!player.RichPresenceMsg.isNullOrBlank()) {
                    player.RichPresenceMsg
                } else if (!player.GameTitle.isNullOrBlank()) {
                    "Playing ${player.GameTitle}"
                } else {
                    "Active on RA"
                }
                Text(
                    statusText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2,
                    lineHeight = 12.sp,
                    overflow = TextOverflow.Ellipsis
                )
                if (!player.ConsoleName.isNullOrBlank()) {
                    Text(
                        player.ConsoleName,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ModernNewsCard(newsItem: com.example.data.api.NewsResponse) {
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    Card(
        onClick = { 
            newsItem.Link?.let { 
                val url = if (it.startsWith("http")) it else "https://retroachievements.org$it"
                uriHandler.openUri(url)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(newsItem.Title ?: "News", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(newsItem.Category ?: "Updates", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun EmptyStateCard(msg: String) {
    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
        Text(msg, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
    }
}
