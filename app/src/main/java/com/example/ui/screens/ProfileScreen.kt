package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.itemsIndexed

import com.example.ui.components.RecentAchievementItem
import com.example.ui.components.SectionHeader
import com.example.data.api.pctWonDouble

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val user = viewModel.userSummary
    val awards = viewModel.userAwards
    val recent = viewModel.recentAchievements
    val context = LocalContext.current
    val isOwn = viewModel.isOwnProfile

    val surfaceColor = if (isOwn) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f) else MaterialTheme.colorScheme.background

    Scaffold(
        containerColor = surfaceColor,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (isOwn) "My Profile" else user?.User ?: "Profile",
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            val profileUrl = "https://retroachievements.org/user/${user?.User}"
                            putExtra(Intent.EXTRA_TEXT, "Check out ${user?.User}'s RetroAchievements profile: $profileUrl")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Profile"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
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
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Header
                item {
                    if (isOwn) {
                        SelfProfileHeader(user)
                    } else {
                        PublicProfileHeader(user)
                    }
                }

                // Actions (Only for public profile) - REMOVED

                // Stats Grid
                item {
                    ProfileStatsGridEnhanced(user, isOwn)
                }

                // Mastered & Beaten
                item {
                    BetterAwardsSummary(
                        mastered = awards?.MasteredCount ?: 0,
                        beaten = awards?.BeatenCount ?: 0,
                        isOwn = isOwn
                    )
                }

                // Grouping Selector
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            if (isOwn) "Hall of Fame" else "Recent Activity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isOwn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.weight(1f)) {
                            ProfileGrouping.values().forEachIndexed { index, grouping ->
                                SegmentedButton(
                                    selected = viewModel.groupingType == grouping,
                                    onClick = { viewModel.groupingType = grouping },
                                    shape = SegmentedButtonDefaults.itemShape(index = index, count = ProfileGrouping.values().size)
                                ) {
                                    Text(grouping.name.lowercase().capitalize(), fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                // Layout Selector
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        SingleChoiceSegmentedButtonRow {
                            LayoutType.values().forEachIndexed { index, layout ->
                                SegmentedButton(
                                    selected = viewModel.layoutType == layout,
                                    onClick = { viewModel.layoutType = layout },
                                    shape = SegmentedButtonDefaults.itemShape(index = index, count = LayoutType.values().size)
                                ) {
                                    Icon(
                                        when (layout) {
                                            LayoutType.LIST -> Icons.Default.ViewHeadline
                                            LayoutType.GRID -> Icons.Default.ViewModule
                                            LayoutType.COMPACT -> Icons.Default.GridView
                                        },
                                        contentDescription = layout.name,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Games List based on Grouping and Layout
                when (viewModel.groupingType) {
                    ProfileGrouping.ALL -> {
                        renderLayout(viewModel, viewModel.completedGames, viewModel.layoutType, isOwn, viewModel.groupingType)
                    }
                    ProfileGrouping.CONSOLE -> {
                        val grouped = viewModel.completedGames.groupBy { it.ConsoleName }
                        grouped.forEach { (console, games) ->
                            item {
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.SportsEsports,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            console ?: "Unknown Console",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                }
                            }
                            renderLayout(viewModel, games, viewModel.layoutType, isOwn, viewModel.groupingType)
                        }
                    }
                    ProfileGrouping.GAME -> {
                        renderLayout(viewModel, viewModel.completedGames, viewModel.layoutType, isOwn, viewModel.groupingType)
                    }
                }

                // Recent Milestones
                if (recent.isNotEmpty()) {
                    item {
                        SectionHeader("Recent Milestones", Icons.Default.Star)
                    }
                    items(recent.take(5)) { achievement ->
                        RecentAchievementItem(achievement)
                    }
                }
            }
        }
    }
}

@Composable
fun SelfProfileHeader(user: com.example.data.api.UserSummaryResponse?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            shape = CircleShape
        ) {}
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box {
                AsyncImage(
                    model = "https://retroachievements.org${user?.UserPic}",
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(6.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = user?.User ?: "Player",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = user?.Motto ?: "Retro Gaming Legend",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium
            )
            
            Surface(
                color = if (user?.Status == "Online") Color(0xFF4CAF50) else MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.White, CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        user?.Status?.uppercase() ?: "OFFLINE",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun PublicProfileHeader(
    user: com.example.data.api.UserSummaryResponse?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "https://retroachievements.org${user?.UserPic}",
            contentDescription = "Avatar",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(4.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = user?.User ?: "Unknown",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = user?.Motto ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        if (user?.Status != null) {
            Surface(
                color = if (user.Status == "Online") Color(0xFF4CAF50) else MaterialTheme.colorScheme.surfaceVariant,
                shape = CircleShape,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text(
                    text = user.Status.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = if (user.Status == "Online") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BetterAwardsSummary(mastered: Int, beaten: Int, isOwn: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AwardCard(
            label = "Mastered",
            count = mastered,
            color = if (isOwn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
            textColor = if (isOwn) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f)
        )
        AwardCard(
            label = "Beaten",
            count = beaten,
            color = if (isOwn) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondaryContainer,
            textColor = if (isOwn) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun AwardCard(label: String, count: Int, color: Color, textColor: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = textColor.copy(alpha = 0.8f))
            Text(
                count.toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
        }
    }
}

fun LazyListScope.renderLayout(
    viewModel: ProfileViewModel,
    games: List<com.example.data.api.CompletedGame>,
    layoutType: LayoutType,
    isOwn: Boolean,
    groupingType: ProfileGrouping
) {
    if (groupingType == ProfileGrouping.ALL) {
        val achievements = viewModel.recentAchievements
        if (layoutType == LayoutType.GRID || layoutType == LayoutType.COMPACT) {
            val columns = if (layoutType == LayoutType.GRID) 3 else 5
            val chunks = achievements.chunked(columns)
            items(chunks) { rowAchievements ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowAchievements.forEach { achievement ->
                        Box(modifier = Modifier.weight(1f)) {
                            AchievementBadgeItem(achievement)
                        }
                    }
                    repeat(columns - rowAchievements.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        } else {
            items(achievements) { achievement ->
                RecentAchievementItem(achievement)
            }
        }
        return
    }

    // For GAME and CONSOLE groupings
    items(games) { game ->
        var expanded by remember { mutableStateOf(false) }
        
        GameAchievementCard(
            game = game, 
            isOwn = isOwn, 
            expanded = expanded,
            onClick = { 
                expanded = !expanded
                if (expanded) {
                    game.GameID?.let { viewModel.loadGameAchievements(it) }
                }
            }
        )
        
        androidx.compose.animation.AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp, bottom = 8.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), MaterialTheme.shapes.medium)
                    .padding(8.dp)
            ) {
                val achievements = viewModel.gameAchievements[game.GameID]
                val isLoading = viewModel.loadingGames[game.GameID] ?: false
                
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                } else if (achievements != null) {
                    achievements.filter { it.DateEarned != null }.forEach { achievement ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = "https://retroachievements.org/Badge/${achievement.BadgeName}.png",
                                contentDescription = null,
                                modifier = Modifier.size(32.dp).clip(CircleShape)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(achievement.Title ?: "", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                Text(achievement.Description ?: "", style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            }
                            Text("+${achievement.Points}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                } else {
                    Text("No achievements found", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Composable
fun AchievementBadgeItem(achievement: com.example.data.api.RecentAchievement) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        AsyncImage(
            model = "https://retroachievements.org/Badge/${achievement.BadgeName}.png",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}

@Composable
fun GameGridCard(game: com.example.data.api.CompletedGame, isOwn: Boolean) {
    val pct = game.pctWonDouble
    val isMastered = pct >= 100.0

    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(0.8f),
        colors = CardDefaults.cardColors(
            containerColor = if (isMastered) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) 
                             else MaterialTheme.colorScheme.surface
        ),
        border = if (isMastered) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = "https://retroachievements.org${game.ImageIcon}",
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
                if (isMastered) {
                    Icon(
                        Icons.Default.EmojiEvents, 
                        contentDescription = null, 
                        tint = MaterialTheme.colorScheme.primary, 
                        modifier = Modifier.size(24.dp).align(Alignment.BottomEnd).background(MaterialTheme.colorScheme.surface, CircleShape).padding(2.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                game.Title ?: "Unknown",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                "${pct.toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = if (isMastered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun GameCompactCard(game: com.example.data.api.CompletedGame, isOwn: Boolean) {
    val pct = game.pctWonDouble
    val isMastered = pct >= 100.0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small)
            .border(
                if (isMastered) 2.dp else 1.dp, 
                if (isMastered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                MaterialTheme.shapes.small
            )
            .background(if (isMastered) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
    ) {
        AsyncImage(
            model = "https://retroachievements.org${game.ImageIcon}",
            contentDescription = null,
            modifier = Modifier.fillMaxSize().padding(4.dp).clip(MaterialTheme.shapes.extraSmall)
        )
        
        // Small percentage text
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            shape = MaterialTheme.shapes.extraSmall,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(2.dp)
        ) {
            Text(
                "${pct.toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 2.dp),
                color = if (isMastered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }

        if (isMastered) {
            Icon(
                Icons.Default.EmojiEvents, 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.primary, 
                modifier = Modifier.size(12.dp).align(Alignment.TopEnd).background(MaterialTheme.colorScheme.surface, CircleShape).padding(1.dp)
            )
        }
    }
}

@Composable
fun GameAchievementCard(
    game: com.example.data.api.CompletedGame, 
    isOwn: Boolean, 
    expanded: Boolean = false,
    onClick: () -> Unit = {}
) {
    val pct = game.pctWonDouble
    val isMastered = pct >= 100.0
    val isBeaten = pct >= 50.0 && pct < 100.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isMastered) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) 
                             else if (isBeaten) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                             else MaterialTheme.colorScheme.surface
        ),
        border = if (isMastered) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "https://retroachievements.org${game.ImageIcon}",
                contentDescription = null,
                modifier = Modifier
                    .size(if (expanded) 64.dp else 48.dp)
                    .clip(MaterialTheme.shapes.small)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    game.Title ?: "Unknown",
                    style = if (expanded) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    game.ConsoleName ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (pct / 100.0).toFloat() },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(androidx.compose.foundation.shape.CircleShape),
                    color = if (isMastered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${pct.toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = if (isMastered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                if (isMastered) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileStatsGridEnhanced(user: com.example.data.api.UserSummaryResponse?, isOwn: Boolean) {
    val cardColor = if (isOwn) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatItemEnhanced(
                label = "Total Points",
                value = user?.TotalPoints?.toString() ?: "-",
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Star,
                containerColor = cardColor
            )
            StatItemEnhanced(
                label = "True Points",
                value = user?.TotalTruePoints?.toString() ?: "-",
                modifier = Modifier.weight(1f),
                icon = Icons.Default.EmojiEvents,
                containerColor = cardColor
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatItemEnhanced(
                label = "Rank",
                value = "#${user?.Rank ?: "-"}",
                modifier = Modifier.weight(1f),
                icon = Icons.Default.BarChart,
                containerColor = cardColor
            )
            StatItemEnhanced(
                label = "Member Since",
                value = user?.MemberSince?.take(10) ?: "-",
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Info,
                containerColor = cardColor
            )
        }
    }
}

@Composable
fun StatItemEnhanced(label: String, value: String, modifier: Modifier, icon: ImageVector, containerColor: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun FollowStatItem(label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        Text(text = "View", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}
