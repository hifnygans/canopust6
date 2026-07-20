package com.example.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.Screen

@Composable
fun MainContainer(
    dashboardContent: @Composable () -> Unit,
    searchContent: @Composable () -> Unit,
    statsContent: @Composable () -> Unit,
    communityContent: @Composable () -> Unit,
    currentScreen: Screen,
    onNavigateToTab: (Screen) -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                val tabs = listOf(
                    Triple(Screen.Main, "Home", Icons.Default.Home),
                    Triple(Screen.Search, "Games", Icons.Default.Search),
                    Triple(Screen.Stats, "Stats", Icons.Default.BarChart),
                    Triple(Screen.Community, "Social", Icons.Default.Public)
                )

                tabs.forEach { (screen, label, icon) ->
                    val selected = currentScreen == screen
                    NavigationBarItem(
                        selected = selected,
                        onClick = { onNavigateToTab(screen) },
                        label = { Text(label) },
                        icon = { Icon(icon, contentDescription = label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        // The content will be provided by the NavHost in MainActivity
        // But we need to handle the padding here somehow if we want it persistent.
        // Actually, it's easier to put the NavHost inside the Scaffold here.
    }
}
