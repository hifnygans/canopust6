package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToGame: (Int) -> Unit
) {
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()
    var showFilters by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (viewModel.searchType == SearchType.Game) {
                        IconButton(onClick = { showFilters = !showFilters }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filters")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs
            TabRow(selectedTabIndex = viewModel.searchType.ordinal) {
                Tab(
                    selected = viewModel.searchType == SearchType.User,
                    onClick = { viewModel.searchType = SearchType.User },
                    text = { Text("Users") }
                )
                Tab(
                    selected = viewModel.searchType == SearchType.Game,
                    onClick = { viewModel.searchType = SearchType.Game },
                    text = { Text("Games") }
                )
            }

            if (viewModel.searchType == SearchType.Game && viewModel.selectedConsoleId == null) {
                ConsoleGrid(viewModel)
            } else {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Search Bar
                    OutlinedTextField(
                        value = viewModel.searchQuery,
                        onValueChange = { viewModel.searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { 
                            Text(if (viewModel.searchType == SearchType.User) "Search for a user..." else "Search for a game...") 
                        },
                        leadingIcon = { 
                            if (viewModel.selectedConsoleId != null) {
                                IconButton(onClick = { viewModel.selectedConsoleId = null }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Consoles")
                                }
                            } else {
                                Icon(Icons.Default.Search, contentDescription = null) 
                            }
                        },
                        trailingIcon = {
                            if (viewModel.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { viewModel.onSearch() }),
                        shape = MaterialTheme.shapes.medium
                    )

                    if (viewModel.selectedConsoleId != null && viewModel.searchType == SearchType.Game) {
                        GameFilters(viewModel)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (viewModel.isLoading) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    viewModel.error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
                    }

                    if (viewModel.searchQuery.isEmpty() && searchHistory.isNotEmpty() && !viewModel.isLoading && viewModel.selectedConsoleId == null) {
                        HistorySection(
                            history = searchHistory,
                            onItemClick = { viewModel.onHistoryClick(it) },
                            onDelete = { viewModel.deleteHistory(it) },
                            onClearAll = { viewModel.clearHistory() }
                        )
                    } else {
                        SearchResults(
                            viewModel = viewModel,
                            onNavigateToProfile = onNavigateToProfile,
                            onNavigateToGame = onNavigateToGame
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConsoleGrid(viewModel: SearchViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Select Console",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        val chunks = viewModel.consoles.chunked(2)
        items(chunks) { rowConsoles ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowConsoles.forEach { console ->
                    ConsoleCard(
                        console = console,
                        onClick = {
                            viewModel.selectedConsoleId = console.ID
                            viewModel.searchGames()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowConsoles.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ConsoleCard(console: com.example.data.api.ConsoleResponse, onClick: () -> Unit, modifier: Modifier) {
    Card(
        modifier = modifier.height(100.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Text(
                text = console.Name ?: "Unknown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun HistorySection(
    history: List<com.example.data.local.SearchHistory>,
    onItemClick: (com.example.data.local.SearchHistory) -> Unit,
    onDelete: (Int) -> Unit,
    onClearAll: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Searches",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            TextButton(onClick = onClearAll) {
                Text("Clear All")
            }
        }
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(history) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) }
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = item.query, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = item.type,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { onDelete(item.id) }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Delete",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameFilters(viewModel: SearchViewModel) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Sort & Layout",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            
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

        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SortOrder.values().forEach { order ->
                FilterChip(
                    selected = viewModel.sortOrder == order,
                    onClick = { viewModel.sortOrder = order },
                    label = { Text(order.name) },
                    leadingIcon = if (viewModel.sortOrder == order) {
                        { Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else null
                )
            }
        }
    }
}

@Composable
fun SearchResults(
    viewModel: SearchViewModel,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToGame: (Int) -> Unit
) {
    if (viewModel.searchType == SearchType.User) {
        viewModel.searchResultUser?.let { user ->
            UserSearchResult(user, onNavigateToProfile)
        }
    } else {
        val games = viewModel.getFilteredGames()
        if (games.isEmpty() && !viewModel.isLoading) {
            Text("No games found.", modifier = Modifier.padding(16.dp))
        } else {
            when (viewModel.layoutType) {
                LayoutType.LIST -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(games) { game ->
                            GameSearchResult(game, onNavigateToGame)
                        }
                    }
                }
                LayoutType.GRID -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        val chunks = games.chunked(2)
                        items(chunks) { rowGames ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                rowGames.forEach { game ->
                                    GameGridSearchCard(game, onNavigateToGame, Modifier.weight(1f))
                                }
                                if (rowGames.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
                LayoutType.COMPACT -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val chunks = games.chunked(4)
                        items(chunks) { rowGames ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowGames.forEach { game ->
                                    GameCompactSearchCard(game, onNavigateToGame, Modifier.weight(1f))
                                }
                                repeat(4 - rowGames.size) { Spacer(Modifier.weight(1f)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameGridSearchCard(game: com.example.data.api.GameResponse, onClick: (Int) -> Unit, modifier: Modifier) {
    Card(
        modifier = modifier.aspectRatio(0.8f).clickable { game.ID?.let { onClick(it) } },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = "https://retroachievements.org${game.ImageIcon}",
                contentDescription = null,
                modifier = Modifier.size(64.dp).clip(MaterialTheme.shapes.small)
            )
            Spacer(Modifier.height(8.dp))
            Text(game.Title ?: "Unknown", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
fun GameCompactSearchCard(game: com.example.data.api.GameResponse, onClick: (Int) -> Unit, modifier: Modifier) {
    Card(
        modifier = modifier.aspectRatio(1f).clickable { game.ID?.let { onClick(it) } },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(4.dp)) {
            AsyncImage(
                model = "https://retroachievements.org${game.ImageIcon}",
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clip(MaterialTheme.shapes.extraSmall)
            )
        }
    }
}

@Composable
fun UserSearchResult(user: com.example.data.api.UserSummaryResponse, onNavigateToProfile: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { user.User?.let { onNavigateToProfile(it) } },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "https://retroachievements.org${user.UserPic}",
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = user.User ?: "Unknown",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Points: ${user.TotalPoints}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Rank: ${user.Rank}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun GameSearchResult(game: com.example.data.api.GameResponse, onClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { game.ID?.let { onClick(it) } },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "https://retroachievements.org${game.ImageIcon}",
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = game.Title ?: "Unknown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )
                Text(
                    text = game.ConsoleName ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (!game.Released.isNullOrBlank()) {
                    Text(
                        text = "Released: ${game.Released}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
