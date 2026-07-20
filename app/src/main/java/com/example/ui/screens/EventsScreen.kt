package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.api.NewsResponse
import com.example.ui.components.ModernNewsCard
import com.example.ui.components.ActiveClaimItem
import com.example.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    viewModel: DashboardViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Official", "Community", "Development")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Events") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            val news = viewModel.news
            val activeClaims = viewModel.activeClaims

            val officialNews = news.filter { 
                it.Category?.contains("Site", ignoreCase = true) == true || 
                it.Category?.contains("Official", ignoreCase = true) == true ||
                it.Category?.contains("News", ignoreCase = true) == true ||
                it.Category == "1" || // Site News ID
                it.Title?.contains("Official", ignoreCase = true) == true ||
                it.Title?.contains("RA", ignoreCase = true) == true
            }

            val communityNews = news.filter { 
                it.Category?.contains("Community", ignoreCase = true) == true ||
                it.Category?.contains("Competition", ignoreCase = true) == true ||
                it.Category?.contains("Event", ignoreCase = true) == true ||
                it.Category == "3" || // Community News ID
                it.Title?.contains("Community", ignoreCase = true) == true ||
                it.Title?.contains("Challenge", ignoreCase = true) == true ||
                it.Title?.contains("Tournament", ignoreCase = true) == true ||
                it.Title?.contains("User", ignoreCase = true) == true
            }

            val otherNews = news.filter { it !in officialNews && it !in communityNews }

            when (selectedTab) {
                0 -> EventList(officialNews.ifEmpty { news.take(5) }, "No official events found.")
                1 -> EventList(communityNews.ifEmpty { otherNews }, "No community events found.")
                2 -> DevelopmentEventList(activeClaims)
            }
        }
    }
}

@Composable
fun EventList(events: List<NewsResponse>, emptyMessage: String) {
    if (events.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(emptyMessage, color = MaterialTheme.colorScheme.secondary)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(events) { event ->
                ModernNewsCard(event)
            }
        }
    }
}

@Composable
fun DevelopmentEventList(claims: List<com.example.data.api.ActiveClaimResponse>) {
    if (claims.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active development claims.", color = MaterialTheme.colorScheme.secondary)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SectionHeader("Games in Development", Icons.Default.Info)
            }
            items(claims) { claim ->
                ActiveClaimItem(claim)
            }
        }
    }
}
