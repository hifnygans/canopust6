package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.LeaderboardEntry
import com.example.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardDetailScreen(
    viewModel: LeaderboardDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    val data = viewModel.leaderboardData

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(data?.Title ?: "Leaderboard") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (viewModel.isLoading && data == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (data != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    LeaderboardHeader(data)
                }

                item {
                    SectionHeader("RANKINGS", Icons.Default.EmojiEvents)
                }

                val entries = data.Entries ?: emptyList()
                if (entries.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No entries found.", color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                } else {
                    items(entries) { entry ->
                        LeaderboardEntryItem(entry, data.Format ?: "") {
                            entry.User?.let { onNavigateToProfile(it) }
                        }
                    }
                }
            }
        } else if (viewModel.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(viewModel.error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadLeaderboard() }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardHeader(data: com.example.data.api.LeaderboardEntriesResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = data.Title ?: "",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = data.Description ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "${data.GameTitle} (${data.ConsoleName})",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun LeaderboardEntryItem(entry: LeaderboardEntry, format: String, onClick: () -> Unit) {
    val rankColor = when (entry.Rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(rankColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entry.Rank?.toString() ?: "",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (entry.Rank in 1..3) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.User ?: "Unknown",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = entry.DateSubmitted ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            Text(
                text = formatScore(entry.Score ?: 0, format),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

fun formatScore(score: Int, format: String): String {
    return when (format.uppercase()) {
        "TIME" -> {
            val totalSeconds = score / 60
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val hundredths = ((score % 60) * 100) / 60
            String.format("%02d:%02d.%02d", minutes, seconds, hundredths)
        }
        "TIMESECS" -> {
            val minutes = score / 60
            val seconds = score % 60
            String.format("%02d:%02d", minutes, seconds)
        }
        else -> score.toString()
    }
}
