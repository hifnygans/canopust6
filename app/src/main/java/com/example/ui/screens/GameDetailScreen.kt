package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.api.GameAchievement
import com.example.data.api.GameInfoAndUserProgressResponse
import com.example.data.api.LeaderboardResponse
import com.example.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    viewModel: GameDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAchievement: (Int) -> Unit,
    onNavigateToLeaderboard: (Int) -> Unit
) {
    val game = viewModel.gameDetail
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(game?.Title ?: "Game Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            val gameUrl = "https://retroachievements.org/game/${game?.ID}"
                            putExtra(Intent.EXTRA_TEXT, "Check out this game on RetroAchievements: ${game?.Title} $gameUrl")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Game"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    if (game?.ForumTopicID != null) {
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://retroachievements.org/viewtopic.php?t=${game.ForumTopicID}"))
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Default.Forum, contentDescription = "Forum")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (viewModel.isLoading && game == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (game != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Visuals Header
                item {
                    GameVisualsHeader(game)
                }

                // Game Info & Progress
                item {
                    GameInfoSection(game)
                }

                // Achievements Header
                item {
                    SectionHeader("Achievements (${game.NumAchievements})", Icons.Default.EmojiEvents)
                }

                item {
                    AchievementControls(
                        filter = viewModel.filter,
                        onFilterChange = { viewModel.filter = it },
                        sort = viewModel.sort,
                        onSortChange = { viewModel.sort = it }
                    )
                }

                val achievementsList = viewModel.getFilteredAchievements()
                if (achievementsList.isEmpty()) {
                    item {
                        Text("No achievements found.", modifier = Modifier.padding(16.dp))
                    }
                } else {
                    items(achievementsList) { achievement ->
                        AchievementItem(achievement) {
                            achievement.ID?.let { onNavigateToAchievement(it) }
                        }
                    }
                }

                // Leaderboards
                if (viewModel.leaderboards.isNotEmpty()) {
                    item {
                        SectionHeader("Leaderboards", Icons.Default.Leaderboard)
                    }
                    items(viewModel.leaderboards) { lb ->
                        LeaderboardItem(lb) {
                            lb.ID?.let { onNavigateToLeaderboard(it) }
                        }
                    }
                }

                // Rich Presence
                if (!game.RichPresencePatch.isNullOrBlank()) {
                    item {
                        SectionHeader("Rich Presence", Icons.Default.SettingsInputComponent)
                    }
                    item {
                        Card(
                            modifier = Modifier.padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text(
                                text = game.RichPresencePatch!!,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        } else if (viewModel.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(viewModel.error!!, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun GameVisualsHeader(game: GameInfoAndUserProgressResponse) {
    Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
        // In-game Screenshot Background
        AsyncImage(
            model = "https://retroachievements.org${game.ImageIngame}",
            contentDescription = null,
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentScale = ContentScale.Crop,
            alpha = 0.4f
        )
        
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Box Art
            AsyncImage(
                model = "https://retroachievements.org${game.ImageBoxArt}",
                contentDescription = "Box Art",
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(0.7f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = game.Title ?: "",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = game.ConsoleName ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun GameInfoSection(game: GameInfoAndUserProgressResponse) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Progress
        val achievements = game.Achievements?.values ?: emptyList()
        val earnedCount = achievements.count { !it.DateEarned.isNullOrBlank() }
        val totalCount = game.NumAchievements ?: achievements.size
        
        if (totalCount > 0) {
            val progress = earnedCount.toFloat() / totalCount
            Text(
                text = "Progress: $earnedCount / $totalCount (${(progress * 100).toInt()}%)",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid Info
        InfoRow(Icons.Default.Person, "Players", game.NumPlayers?.toString() ?: "-")
        InfoRow(Icons.Default.SettingsInputComponent, "Developer", game.Developer ?: "-")
        InfoRow(Icons.Default.Info, "Publisher", game.Publisher ?: "-")
        InfoRow(Icons.Default.Info, "Genre", game.Genre ?: "-")
        InfoRow(Icons.Default.Info, "Released", game.Released ?: "-")
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$label: ", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AchievementControls(
    filter: AchievementFilter,
    onFilterChange: (AchievementFilter) -> Unit,
    sort: AchievementSort,
    onSortChange: (AchievementSort) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filter == AchievementFilter.All,
                onClick = { onFilterChange(AchievementFilter.All) },
                label = { Text("All") }
            )
            FilterChip(
                selected = filter == AchievementFilter.Unlocked,
                onClick = { onFilterChange(AchievementFilter.Unlocked) },
                label = { Text("Unlocked") }
            )
            FilterChip(
                selected = filter == AchievementFilter.Locked,
                onClick = { onFilterChange(AchievementFilter.Locked) },
                label = { Text("Locked") }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = sort == AchievementSort.Default,
                onClick = { onSortChange(AchievementSort.Default) },
                label = { Text("Default") }
            )
            FilterChip(
                selected = sort == AchievementSort.Points,
                onClick = { onSortChange(AchievementSort.Points) },
                label = { Text("Points") }
            )
            FilterChip(
                selected = sort == AchievementSort.UnlockDate,
                onClick = { onSortChange(AchievementSort.UnlockDate) },
                label = { Text("Date") }
            )
        }
    }
}

@Composable
fun AchievementItem(achievement: GameAchievement, onClick: () -> Unit) {
    val isEarned = !achievement.DateEarned.isNullOrBlank()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEarned) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "https://retroachievements.org/Badge/${achievement.BadgeName}${if (isEarned) "" else "_lock"}.png",
                contentDescription = null,
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = achievement.Title ?: "", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(text = achievement.Description ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                if (isEarned) {
                    Text(
                        text = "Earned on: ${achievement.DateEarned}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(
                text = "${achievement.Points}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun LeaderboardItem(lb: LeaderboardResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = lb.Title ?: "", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(text = lb.Description ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        }
    }
}
