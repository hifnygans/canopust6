package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.api.CompletedGame
import com.example.data.api.GlobalRecentAchievement
import com.example.data.api.RecentAchievement
import com.example.data.api.TopUserResponse
import com.example.ui.components.SectionHeader
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.entryModelOf
import androidx.compose.ui.text.style.TextOverflow
import com.example.data.api.AchievementOfTheWeekResponse
import com.example.data.api.ActiveClaimResponse
import com.example.data.api.GameSuggestionResponse
import com.example.data.api.NewsResponse
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community & Insights", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(16.dp))
                    Text(viewModel.error!!, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.loadStats() }, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Retry")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Achievement of the Week
                item {
                    AchievementOfTheWeekSection(viewModel.achievementOfTheWeek)
                }

                // Personal Stats Summary
                item {
                    StatsSummarySection(viewModel)
                }

                // Personal Points Chart
                item {
                    PersonalPointsChart(viewModel.recentAchievements)
                }

                // Console Distribution
                item {
                    ConsoleStatsSection(viewModel.completedGames)
                }

                // Top Users
                item {
                    GlobalRankingsSection(viewModel.topUsers, onNavigateToProfile)
                }

                // News Feed
                item {
                    NewsFeedSection(viewModel.news)
                }

                // Active Claims (Developers) - Moved to bottom
                item {
                    ActiveClaimsSection(viewModel.activeClaims)
                }
                
                item {
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun AchievementOfTheWeekSection(aotw: AchievementOfTheWeekResponse?) {
    if (aotw == null || aotw.Achievement == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                Spacer(Modifier.width(8.dp))
                Text("ACHIEVEMENT OF THE WEEK", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.tertiary)
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = "https://retroachievements.org/Badge/${aotw.Achievement.BadgeName}.png",
                    contentDescription = null,
                    modifier = Modifier.size(64.dp).clip(MaterialTheme.shapes.medium)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(aotw.Achievement.Title ?: "Unknown", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text(aotw.Achievement.Description ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f))
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(aotw.Game?.Title ?: "Unknown Game", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.width(16.dp))
                Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("${aotw.TotalPlayers} Players", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun GlobalActivityChart(achievements: List<GlobalRecentAchievement>) {
    val chartData = remember(achievements) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val now = LocalDateTime.now()
        
        // Group by hours ago (0 to 23)
        val data = (0..23).map { hoursAgo ->
            achievements.count { 
                try {
                    val date = LocalDateTime.parse(it.Date, formatter)
                    val hours = Duration.between(date, now).toHours()
                    hours == hoursAgo.toLong()
                } catch (e: Exception) {
                    false
                }
            }.toFloat()
        }.reversed() // Show oldest to newest
        
        data
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(20.dp)) {
            SectionHeader("Global Pulse (24h)", Icons.Default.FlashOn)
            Text("Activity across the entire community", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.height(24.dp))
            
            if (chartData.any { it > 0f }) {
                VicoChart(chartData, MaterialTheme.colorScheme.primary)
            } else {
                EmptyChartState()
            }
        }
    }
}

@Composable
fun PersonalPointsChart(achievements: List<RecentAchievement>) {
    val chartData = remember(achievements) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dailyPoints = achievements.reversed()
            .groupBy { 
                try {
                    LocalDateTime.parse(it.Date, formatter).toLocalDate()
                } catch (e: Exception) {
                    null
                }
            }
            .filterKeys { it != null }
            .mapValues { it.value.sumOf { a -> a.Points ?: 0 } }
            .toList()
            .sortedBy { it.first }
            .map { it.second.toFloat() }
        
        if (dailyPoints.isEmpty()) emptyList() else dailyPoints
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(20.dp)) {
            SectionHeader("Points Growth", Icons.Default.Timeline)
            Text("Total daily points progress", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.height(24.dp))
            
            if (chartData.isNotEmpty()) {
                VicoChart(chartData, MaterialTheme.colorScheme.tertiary)
            } else {
                EmptyChartState()
            }
        }
    }
}

@Composable
fun VicoChart(data: List<Float>, color: Color) {
    val model = entryModelOf(*data.toTypedArray())
    Chart(
        chart = lineChart(
            lines = listOf(
                LineChart.LineSpec(
                    lineColor = color.toArgb(),
                    lineBackgroundShader = DynamicShaders.fromBrush(
                        Brush.verticalGradient(
                            listOf(color.copy(alpha = 0.4f), Color.Transparent)
                        )
                    )
                )
            )
        ),
        model = model,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(),
        modifier = Modifier.fillMaxWidth().height(200.dp)
    )
}

@Composable
fun EmptyChartState() {
    Box(Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Inbox, contentDescription = null, tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), modifier = Modifier.size(40.dp))
            Spacer(Modifier.height(8.dp))
            Text("No activity detected in this period", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun ActiveClaimsSection(claims: List<ActiveClaimResponse>) {
    if (claims.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Developer Activity", Icons.Default.Construction)
        Text("Latest achievement sets in progress", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            claims.take(5).forEach { claim ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = "https://retroachievements.org${claim.GameIcon}",
                            contentDescription = null,
                            modifier = Modifier.size(40.dp).clip(CircleShape)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(claim.GameTitle ?: "Unknown", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${claim.User} • ${claim.ConsoleName}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                        }
                        StatusBadge(if (claim.Done == 1) "Completed" else "In Progress", if (claim.Done == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = CircleShape,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NewsFeedSection(news: List<NewsResponse>) {
    if (news.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("RA News", Icons.Default.Newspaper)
        
        news.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(item.Title ?: "", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("${item.Author} • ${item.PostedAt?.take(10)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
fun StatsSummarySection(viewModel: StatsViewModel) {
    val user = viewModel.userSummary
    val totalPoints = user?.TotalPoints ?: 0
    val totalGames = viewModel.completedGames.size
    val rank = user?.Rank ?: "N/A"

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Your Dashboard", Icons.Default.Dashboard)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryMiniCard(
                label = "Global Rank",
                value = "#$rank",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.primaryContainer,
                icon = Icons.Default.EmojiEvents
            )
            SummaryMiniCard(
                label = "Total Score",
                value = totalPoints.toString(),
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.secondaryContainer,
                icon = Icons.Default.Stars
            )
            SummaryMiniCard(
                label = "Completed",
                value = totalGames.toString(),
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.tertiaryContainer,
                icon = Icons.Default.SportsEsports
            )
        }
    }
}

@Composable
fun SummaryMiniCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ConsoleStatsSection(completedGames: List<CompletedGame>) {
    val totalGames = completedGames.size
    val consoleCounts = remember(completedGames) {
        completedGames.groupBy { it.ConsoleName ?: "Unknown" }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .take(5)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(20.dp)) {
            SectionHeader("Console Distribution", Icons.Default.PieChart)
            Spacer(Modifier.height(20.dp))
            if (consoleCounts.isNotEmpty()) {
                consoleCounts.forEach { (console, count) ->
                    val percentage = if (totalGames > 0) (count.toFloat() / totalGames.toFloat() * 100).toInt() else 0
                    val progress = if (totalGames > 0) count.toFloat() / totalGames.toFloat() else 0f
                    
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(console, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("$count ($percentage%)", style = MaterialTheme.typography.labelMedium)
                        }
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(12.dp),
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            } else {
                Text("No data available", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun GlobalRankingsSection(topUsers: List<TopUserResponse>, onNavigateToProfile: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(20.dp)) {
            SectionHeader("Hall of Fame", Icons.Default.Public)
            Text("Top 10 players globally by points", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.height(24.dp))
            if (topUsers.isNotEmpty()) {
                topUsers.forEachIndexed { index, user ->
                    val username = user.User ?: "Unknown"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { onNavigateToProfile(username) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = when(index) {
                                0 -> Color(0xFFFFD700) // Gold
                                1 -> Color(0xFFC0C0C0) // Silver
                                2 -> Color(0xFFCD7F32) // Bronze
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Black,
                                    color = if (index < 3) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        
                        AsyncImage(
                            model = "https://retroachievements.org/UserPic/$username.png",
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentScale = ContentScale.Crop
                        )
                        
                        Spacer(Modifier.width(16.dp))
                        
                        Column(Modifier.weight(1f)) {
                            Text(username, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Stars, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(4.dp))
                                Text("${user.Points} pts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${user.TruePoints} TP",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text("True Points", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f))
                        }
                    }
                    if (index < topUsers.size - 1) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp).alpha(0.1f))
                    }
                }
            } else {
                Text("No data available", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
