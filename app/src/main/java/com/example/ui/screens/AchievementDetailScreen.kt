package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.api.AchievementUnlock
import com.example.ui.components.StatCard
import com.example.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementDetailScreen(
    viewModel: AchievementDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    val data = viewModel.achievementData
    val achievement = data?.Achievement
    val unlocks = data?.Unlocks
    val game = viewModel.gameData

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Achievement Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (viewModel.isLoading && achievement == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (achievement != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Header
                item {
                    AchievementHeader(achievement)
                }

                // Stats
                item {
                    AchievementStats(achievement, game)
                }

                // Game context
                item {
                    GameContextCard(achievement.GameTitle ?: "", achievement.ConsoleName ?: "")
                }

                // Unlock History
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RECENT UNLOCKS",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }
                }

                if (unlocks.isNullOrEmpty()) {
                    item {
                        Text("No recent unlocks found.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    }
                } else {
                    items(unlocks) { unlock ->
                        UnlockItem(unlock, onNavigateToProfile)
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
fun AchievementHeader(achievement: com.example.data.api.AchievementDetailInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "https://retroachievements.org/Badge/${achievement.BadgeName}.png",
            contentDescription = "Achievement Badge",
            modifier = Modifier
                .size(100.dp)
                .clip(MaterialTheme.shapes.medium)
                .border(2.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = achievement.Title ?: "Unknown",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Text(
            text = achievement.Description ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun AchievementStats(achievement: com.example.data.api.AchievementDetailInfo, game: com.example.data.api.GameInfoAndUserProgressResponse?) {
    val totalPlayers = game?.NumPlayers ?: 0
    val gameAchievement = game?.Achievements?.values?.find { it.ID == achievement.ID }
    val numAwarded = gameAchievement?.NumAwarded ?: 0
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                label = "Points",
                value = achievement.Points?.toString() ?: "-",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.primaryContainer
            )
            StatCard(
                label = "True Points",
                value = achievement.TrueRatio?.toString() ?: "-",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.secondaryContainer
            )
        }
        
        if (totalPlayers > 0) {
            val rate = (numAwarded.toFloat() / totalPlayers * 100)
            StatCard(
                label = "Unlock Rate",
                value = String.format("%.1f%%", rate),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.tertiaryContainer
            )
        }
    }
}

@Composable
fun GameContextCard(gameTitle: String, consoleName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = gameTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(text = consoleName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@Composable
fun UnlockItem(unlock: AchievementUnlock, onNavigateToProfile: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = { unlock.User?.let { onNavigateToProfile(it) } }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = unlock.User ?: "Unknown", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = unlock.DateAwarded ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            }
            if (unlock.HardcoreMode == 1) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = CircleShape
                ) {
                    Text(
                        text = "HC",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
