package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.work.*
import com.example.util.PollingWorker
import java.util.concurrent.TimeUnit
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.navigation.toRoute
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.api.RetroAchievementsService
import com.example.data.local.AppDatabase
import com.example.data.prefs.SessionManager
import com.example.data.repository.AuthRepository
import com.example.data.repository.RetroAchievementsRepository
import com.example.ui.MainViewModel
import com.example.ui.navigation.Screen
import com.example.ui.components.RecentAchievementItem
import com.example.ui.screens.*
import com.example.ui.theme.RetroAchievementsTheme
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun schedulePolling() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val pollingRequest = PeriodicWorkRequestBuilder<PollingWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "RetroPolling",
            ExistingPeriodicWorkPolicy.KEEP,
            pollingRequest
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        checkNotificationPermission()
        schedulePolling()

        // Use singleton API service
        val apiService = com.example.data.api.RetrofitClient.instance
        val sessionManager = SessionManager(applicationContext)
        val authRepository = AuthRepository(apiService, sessionManager)
        
        // Room Database
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "retro_achievements_db"
        ).fallbackToDestructiveMigration().build()
        val searchDao = database.searchDao()

        val raRepository = RetroAchievementsRepository(apiService, sessionManager, searchDao)

        setContent {
            val themeIndex by mainViewModel.themeIndex.collectAsState()

            RetroAchievementsTheme(themeIndex = themeIndex) {
                val navController = rememberNavController()
                val isLoggedIn by mainViewModel.isLoggedIn.collectAsState()
                val username by mainViewModel.username.collectAsState()
                
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val topLevelScreens = listOf(
                    Triple(Screen.Main, "Home", Icons.Default.Home),
                    Triple(Screen.Search, "Games", Icons.Default.Search),
                    Triple(Screen.Stats, "Stats", Icons.Default.BarChart),
                    Triple(Screen.Community, "Social", Icons.Default.Public)
                )

                Scaffold(
                    bottomBar = {
                        val showBottomBar = topLevelScreens.any { (screen, _, _) ->
                            currentDestination?.hierarchy?.any { it.hasRoute(screen::class) } == true
                        }
                        
                        if (showBottomBar) {
                            NavigationBar {
                                topLevelScreens.forEach { (screen, label, icon) ->
                                    NavigationBarItem(
                                        icon = { Icon(icon, contentDescription = label) },
                                        label = { Text(label) },
                                        selected = currentDestination?.hierarchy?.any { it.hasRoute(screen::class) } == true,
                                        onClick = {
                                            navController.navigate(screen) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Splash,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                    composable<Screen.Splash> {
                        SplashScreen(
                            isLoggedIn = isLoggedIn,
                            onNavigateNext = {
                                if (isLoggedIn) {
                                    navController.navigate(Screen.Main) {
                                        popUpTo(Screen.Splash) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate(Screen.Login) {
                                        popUpTo(Screen.Splash) { inclusive = true }
                                    }
                                }
                            }
                        )
                    }

                    composable<Screen.Login> {
                        val loginViewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return LoginViewModel(authRepository) as T
                                }
                            }
                        )
                        LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = {
                                navController.navigate(Screen.Main) {
                                    popUpTo(Screen.Login) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable<Screen.Main> {
                        val dashboardViewModel: DashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return DashboardViewModel(raRepository) as T
                                }
                            }
                        )
                        MainScreen(
                            viewModel = dashboardViewModel,
                            onNavigateToSettings = {
                                navController.navigate(Screen.Settings)
                            },
                            onNavigateToSearch = {
                                navController.navigate(Screen.Search)
                            },
                            onNavigateToProfile = { username ->
                                navController.navigate(Screen.Profile(username))
                            },
                            onNavigateToGame = { gameId ->
                                navController.navigate(Screen.GameDetail(gameId))
                            },
                            onNavigateToAotWHistory = {
                                navController.navigate(Screen.AotWHistory)
                            },
                            onNavigateToEvents = {
                                navController.navigate(Screen.Events)
                            },
                            onNavigateToAbout = {
                                navController.navigate(Screen.About)
                            },
                            onNavigateToActivityFeed = {
                                navController.navigate(Screen.ActivityFeed)
                            },
                            onNavigateToStats = {
                                navController.navigate(Screen.Stats)
                            }
                        )
                    }

                    composable<Screen.Community> {
                        val communityViewModel: CommunityViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return CommunityViewModel(raRepository) as T
                                }
                            }
                        )
                        CommunityScreen(
                            viewModel = communityViewModel,
                            onNavigateToEvents = { navController.navigate(Screen.Events) },
                            onNavigateToActivityFeed = { navController.navigate(Screen.ActivityFeed) },
                            onNavigateToAotWHistory = { navController.navigate(Screen.AotWHistory) },
                            onNavigateToProfile = { username ->
                                navController.navigate(Screen.Profile(username))
                            },
                            onNavigateToGame = { gameId ->
                                navController.navigate(Screen.GameDetail(gameId))
                            }
                        )
                    }

                    composable<Screen.Profile> { backStackEntry ->
                        val profile: Screen.Profile = backStackEntry.toRoute()
                        val profileViewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return ProfileViewModel(raRepository, profile.username) as T
                                }
                            }
                        )
                        ProfileScreen(
                            viewModel = profileViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable<Screen.Search> {
                        val searchViewModel: SearchViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return SearchViewModel(raRepository) as T
                                }
                            }
                        )
                        SearchScreen(
                            viewModel = searchViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToProfile = { username ->
                                navController.navigate(Screen.Profile(username))
                            },
                            onNavigateToGame = { gameId ->
                                navController.navigate(Screen.GameDetail(gameId))
                            }
                        )
                    }

                    composable<Screen.AotWHistory> {
                        val dashboardViewModel: DashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return DashboardViewModel(raRepository) as T
                                }
                            }
                        )
                        AotWHistoryScreen(
                            viewModel = dashboardViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToGame = { gameId ->
                                navController.navigate(Screen.GameDetail(gameId))
                            }
                        )
                    }

                    composable<Screen.Events> {
                        val dashboardViewModel: DashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return DashboardViewModel(raRepository) as T
                                }
                            }
                        )
                        EventsScreen(
                            viewModel = dashboardViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable<Screen.About> {
                        AboutScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable<Screen.ActivityFeed> {
                        val activityViewModel: ActivityFeedViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return ActivityFeedViewModel(raRepository) as T
                                }
                            }
                        )
                        ActivityFeedScreen(
                            viewModel = activityViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable<Screen.Stats> {
                        val statsViewModel: StatsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return StatsViewModel(raRepository) as T
                                }
                            }
                        )
                        StatsScreen(
                            viewModel = statsViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToProfile = { username ->
                                navController.navigate(Screen.Profile(username))
                            }
                        )
                    }

                    composable<Screen.FollowList> { backStackEntry ->
                        val followRoute: Screen.FollowList = backStackEntry.toRoute()
                        val followViewModel: FollowListViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return FollowListViewModel(raRepository, followRoute.targetUser, followRoute.isFollowers) as T
                                }
                            }
                        )
                        FollowListScreen(
                            viewModel = followViewModel,
                            title = if (followRoute.isFollowers) "Followers of ${followRoute.targetUser}" else "Users ${followRoute.targetUser} Follows",
                            onNavigateBack = { navController.popBackStack() },
                            onUserClick = { username ->
                                navController.navigate(Screen.Profile(username))
                            }
                        )
                    }

                    composable<Screen.GameDetail> { backStackEntry ->
                        val gameRoute: Screen.GameDetail = backStackEntry.toRoute()
                        val gameViewModel: GameDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return GameDetailViewModel(raRepository, gameRoute.gameId) as T
                                }
                            }
                        )
                        GameDetailScreen(
                            viewModel = gameViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToAchievement = { achievementId ->
                                navController.navigate(Screen.AchievementDetail(achievementId, gameRoute.gameId))
                            },
                            onNavigateToLeaderboard = { leaderboardId ->
                                navController.navigate(Screen.LeaderboardDetail(leaderboardId))
                            }
                        )
                    }

                    composable<Screen.AchievementDetail> { backStackEntry ->
                        val route: Screen.AchievementDetail = backStackEntry.toRoute()
                        val achievementViewModel: AchievementDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return AchievementDetailViewModel(raRepository, route.achievementId, route.gameId) as T
                                }
                            }
                        )
                        AchievementDetailScreen(
                            viewModel = achievementViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToProfile = { username ->
                                navController.navigate(Screen.Profile(username))
                            }
                        )
                    }

                    composable<Screen.LeaderboardDetail> { backStackEntry ->
                        val route: Screen.LeaderboardDetail = backStackEntry.toRoute()
                        val lbViewModel: LeaderboardDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return LeaderboardDetailViewModel(raRepository, route.leaderboardId) as T
                                }
                            }
                        )
                        LeaderboardDetailScreen(
                            viewModel = lbViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToProfile = { username ->
                                navController.navigate(Screen.Profile(username))
                            }
                        )
                    }

                    composable<Screen.Settings> {
                        SettingsScreen(
                            themeIndex = themeIndex,
                            onThemeSelect = { mainViewModel.setTheme(it) },
                            onLogout = {
                                mainViewModel.logout()
                                navController.navigate(Screen.Login) {
                                    popUpTo(Screen.Main) { inclusive = true }
                                }
                            },
                            onNavigateAbout = {
                                navController.navigate(Screen.About)
                            },
                            onNavigateEmulator = {
                                navController.navigate(Screen.Emulator)
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable<Screen.Emulator> {
                        EmulatorScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
}
