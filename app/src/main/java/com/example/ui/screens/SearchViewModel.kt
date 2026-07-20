package com.example.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.ConsoleResponse
import com.example.data.api.GameResponse
import com.example.data.api.UserSummaryResponse
import com.example.data.local.SearchHistory
import com.example.data.repository.RetroAchievementsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SearchType { User, Game }
enum class SortOrder { Name, Date, ID }

class SearchViewModel(
    private val repository: RetroAchievementsRepository
) : ViewModel() {

    var searchQuery by mutableStateOf("")
    var searchType by mutableStateOf(SearchType.User)
    
    var searchResultUser by mutableStateOf<UserSummaryResponse?>(null)
        private set
    
    var gamesList by mutableStateOf<List<GameResponse>>(emptyList())
        private set
    
    var consoles by mutableStateOf<List<ConsoleResponse>>(emptyList())
        private set
    
    var selectedConsoleId by mutableStateOf<Int?>(null)
    var sortOrder by mutableStateOf(SortOrder.Name)
    var layoutType by mutableStateOf(LayoutType.LIST)

    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    val searchHistory: StateFlow<List<SearchHistory>> = repository.searchHistory?.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    ) ?: MutableStateFlow(emptyList())

    init {
        loadConsoles()
    }

    private fun loadConsoles() {
        viewModelScope.launch {
            repository.getConsoleIDs().onSuccess {
                consoles = it.sortedBy { c -> c.Name }
            }
        }
    }

    fun onSearch() {
        if (searchQuery.isBlank() && searchType == SearchType.User) return

        isLoading = true
        error = null
        
        viewModelScope.launch {
            repository.saveSearch(searchQuery, searchType.name)
            
            if (searchType == SearchType.User) {
                searchResultUser = null
                repository.searchUser(searchQuery)
                    .onSuccess { searchResultUser = it }
                    .onFailure { error = it.message ?: "User not found" }
                isLoading = false
            } else {
                searchGames()
            }
        }
    }

    fun searchGames() {
        val consoleId = selectedConsoleId
        if (consoleId == null) {
            error = "Please select a system first"
            isLoading = false
            return
        }

        isLoading = true
        viewModelScope.launch {
            repository.getGameList(consoleId)
                .onSuccess {
                    gamesList = it
                }
                .onFailure {
                    error = it.message ?: "Failed to fetch games"
                }
            isLoading = false
        }
    }

    fun getFilteredGames(): List<GameResponse> {
        val filtered = if (searchQuery.isBlank()) {
            gamesList
        } else {
            gamesList.filter { it.Title?.contains(searchQuery, ignoreCase = true) == true }
        }

        return when (sortOrder) {
            SortOrder.Name -> filtered.sortedBy { it.Title }
            SortOrder.Date -> filtered.sortedByDescending { it.Released }
            SortOrder.ID -> filtered.sortedBy { it.ID }
        }
    }

    fun onHistoryClick(history: SearchHistory) {
        searchQuery = history.query
        searchType = SearchType.valueOf(history.type)
        onSearch()
    }

    fun deleteHistory(id: Int) {
        viewModelScope.launch {
            repository.deleteSearch(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
