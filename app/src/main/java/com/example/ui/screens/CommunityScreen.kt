package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.api.ActiveClaimResponse
import com.example.data.api.ClaimResponse
import com.example.data.api.RecentGameAwardResponse
import com.example.data.api.TopUserResponse
import com.example.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel,
    onNavigateToEvents: () -> Unit,
    onNavigateToActivityFeed: () -> Unit,
    onNavigateToAotWHistory: () -> Unit,
    onNavigateToProfile: (String) -> Unit = {},
    onNavigateToGame: (Int) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Quick Links
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickLinkCard(
                        title = "Activity",
                        icon = Icons.Default.List,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToActivityFeed
                    )
                    QuickLinkCard(
                        title = "Events",
                        icon = Icons.Default.Event,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToEvents
                    )
                    QuickLinkCard(
                        title = "AotW",
                        icon = Icons.Default.EmojiEvents,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToAotWHistory
                    )
                }
            }

            // Top 10 Users
            item {
                TopTenSection(viewModel.topUsers, onNavigateToProfile)
            }

            // Recent Game Awards
            item {
                RecentAwardsSection(viewModel.recentGameAwards, onNavigateToProfile, onNavigateToGame)
            }

            // Active Set Claims
            item {
                ActiveClaimsSection("Active Set Claims", viewModel.activeClaims, onNavigateToProfile, onNavigateToGame)
            }

            // Completed Set Claims
            item {
                CompletedClaimsSection("Recent Completed Claims", viewModel.completedClaims, onNavigateToProfile, onNavigateToGame)
            }
        }
    }
}

@Composable
fun QuickLinkCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TopTenSection(users: List<TopUserResponse>, onNavigateToProfile: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader("Top Ten Players", Icons.Default.Leaderboard)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(users.take(10)) { user ->
                Card(
                    modifier = Modifier.width(140.dp).clickable { user.User?.let { onNavigateToProfile(it) } },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            AsyncImage(
                                model = "https://retroachievements.org/UserPic/${user.User}.png",
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                error = androidx.compose.ui.graphics.painter.ColorPainter(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            user.User ?: "Unknown",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "${user.Points ?: 0} pts",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentAwardsSection(awards: List<RecentGameAwardResponse>, onNavigateToProfile: (String) -> Unit, onNavigateToGame: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader("Recent Game Awards", Icons.Default.WorkspacePremium)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(awards) { award ->
                Card(
                    modifier = Modifier.width(280.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = "https://retroachievements.org/Badge/${award.BadgeName}.png",
                            contentDescription = null,
                            modifier = Modifier.size(64.dp).clip(MaterialTheme.shapes.small),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                award.User ?: "Someone",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { award.User?.let { onNavigateToProfile(it) } }
                            )
                            Text(
                                award.AwardType ?: "Award",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                award.GameTitle ?: "Unknown Game",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.clickable { award.GameID?.let { onNavigateToGame(it) } }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveClaimsSection(title: String, claims: List<ActiveClaimResponse>, onNavigateToProfile: (String) -> Unit, onNavigateToGame: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(title, Icons.Default.NewReleases)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(claims) { claim ->
                Card(
                    modifier = Modifier.width(260.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = "https://retroachievements.org${claim.GameIcon}",
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).clip(MaterialTheme.shapes.small)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    claim.GameTitle ?: "Unknown",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.clickable { claim.GameID?.let { onNavigateToGame(it) } }
                                )
                                Text(claim.ConsoleName ?: "Unknown", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                claim.User ?: "Unknown",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable { claim.User?.let { onNavigateToProfile(it) } }
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Type: ${if (claim.ClaimType == 0) "Primary" else "Secondary"} • ${if (claim.SetType == 0) "Core" else "Subset"}",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompletedClaimsSection(title: String, claims: List<ClaimResponse>, onNavigateToProfile: (String) -> Unit, onNavigateToGame: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(title, Icons.Default.CheckCircle)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(claims) { claim ->
                Card(
                    modifier = Modifier.width(260.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = "https://retroachievements.org${claim.GameIcon}",
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).clip(MaterialTheme.shapes.small)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    claim.GameTitle ?: "Unknown",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.clickable { claim.GameID?.let { onNavigateToGame(it) } }
                                )
                                Text(claim.ConsoleName ?: "Unknown", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                claim.User ?: "Unknown",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable { claim.User?.let { onNavigateToProfile(it) } }
                            )
                        }
                    }
                }
            }
        }
    }
}
