package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.CompletedGame
import com.example.data.api.GlobalRecentAchievement
import com.example.data.api.RecentAchievement
import com.example.ui.components.SectionHeader

import coil.compose.AsyncImage
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityFeedScreen(
    viewModel: ActivityFeedViewModel,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Feed Aktivitas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadActivity() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (viewModel.error != null) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(viewModel.error!!, color = MaterialTheme.colorScheme.error)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // World Activity (Global Active Players)
                if (viewModel.activePlayers.isNotEmpty()) {
                    item { SectionHeader("World Activity (Live)", Icons.Default.Public) }
                    items(viewModel.activePlayers.take(50)) { player ->
                        ActivityCard(
                            title = player.User ?: "User",
                            subtitle = player.RichPresenceMsg ?: "Playing ${player.GameTitle ?: "Unknown Game"}",
                            trailing = player.ConsoleName ?: "",
                            imageUrl = "https://retroachievements.org/UserPic/${player.User}.png"
                        )
                    }
                }

                // Global World Feed (Recent Achievements)
                if (viewModel.globalRecentUnlocks.isNotEmpty()) {
                    item { SectionHeader("Recent World Unlocks", Icons.Default.Language) }
                    items(viewModel.globalRecentUnlocks.take(30)) { unlock ->
                        ActivityCard(
                            title = unlock.User ?: "User",
                            subtitle = "Earned ${unlock.AchievementTitle ?: "Achievement"} in ${unlock.GameTitle ?: "Unknown Game"}",
                            trailing = "${unlock.Points ?: 0}",
                            imageUrl = "https://retroachievements.org/Badge/${unlock.BadgeName}.png"
                        )
                    }
                }

                // Recent Unlocks
                if (viewModel.userRecentUnlocks.isNotEmpty()) {
                    item { SectionHeader("Recent Unlocks", Icons.Default.EmojiEvents) }
                    items(viewModel.userRecentUnlocks.take(15)) { unlock ->
                        ActivityCard(
                            title = unlock.Title ?: "Achievement",
                            subtitle = unlock.GameTitle ?: "Unknown Game",
                            trailing = "${unlock.Points} pts",
                            imageUrl = "https://retroachievements.org/Badge/${unlock.BadgeName}.png"
                        )
                    }
                }

                // Mastered Games
                if (viewModel.masteredGames.isNotEmpty()) {
                    item { SectionHeader("Mastered Games", Icons.Default.Stars) }
                    items(viewModel.masteredGames.take(5)) { game ->
                        ActivityCard(
                            title = game.Title ?: "Game",
                            subtitle = game.ConsoleName ?: "",
                            trailing = "MASTERED",
                            imageUrl = "https://retroachievements.org${game.ImageIcon}"
                        )
                    }
                }

                // Global Activity
                if (viewModel.globalRecentUnlocks.isNotEmpty()) {
                    item { SectionHeader("Global Activity", Icons.Default.List) }
                    items(viewModel.globalRecentUnlocks.take(20)) { global ->
                        ActivityCard(
                            title = "${global.User} unlocked",
                            subtitle = "${global.AchievementTitle} in ${global.GameTitle}",
                            trailing = if (global.Type == "2") "HC" else "",
                            imageUrl = "https://retroachievements.org/Badge/${global.BadgeName}.png"
                        )
                    }
                }

                // Following
                if (viewModel.followingList.isNotEmpty()) {
                    item { SectionHeader("Followed Users", Icons.Default.People) }
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "Following ${viewModel.followingList.size} users",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityCard(
    title: String,
    subtitle: String,
    trailing: String,
    imageUrl: String? = null,
    icon: ImageVector? = null,
    iconTint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape)
                )
            } else if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = iconTint)
            }
            
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title, 
                    style = MaterialTheme.typography.bodyMedium, 
                    fontWeight = FontWeight.Bold, 
                    maxLines = 1, 
                    modifier = Modifier.basicMarquee()
                )
                Text(
                    subtitle, 
                    style = MaterialTheme.typography.labelSmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant, 
                    maxLines = 1, 
                    modifier = Modifier.basicMarquee()
                )
            }
            if (trailing.isNotEmpty()) {
                Text(trailing, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
