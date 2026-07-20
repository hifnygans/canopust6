package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val query: String,
    val type: String, // "User" or "Game"
    val timestamp: Long = System.currentTimeMillis()
)
