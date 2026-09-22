package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.LivePulseDot
import com.example.ui.screens.BusSearchScreen
import com.example.ui.screens.DepotAlertsScreen
import com.example.ui.screens.LiveTrackerScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.PassAndFavoritesScreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.KsrtcAmber
import com.example.ui.theme.KsrtcGold
import com.example.ui.theme.KsrtcRedPrimary
import com.example.ui.theme.LiveGreen
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.TextSecondary

@Composable
fun MainAppShell(
    viewModel: BusTrackerViewModel,
    modifier: Modifier = Modifier
) {
    val buses by viewModel.buses.collectAsStateWithLifecycle()
    val selectedBus by viewModel.selectedBus.collectAsStateWithLifecycle()
    val savedRoutes by viewModel.savedRoutes.collectAsStateWithLifecycle()
    val userSession by viewModel.userSession.collectAsStateWithLifecycle()
    val alerts by viewModel.depotAlerts.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val stopAlarmEnabled by viewModel.stopAlarmEnabled.collectAsStateWithLifecycle()

    val originQuery by viewModel.searchOrigin.collectAsStateWithLifecycle()
    val destQuery by viewModel.searchDestination.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedTypeFilter.collectAsStateWithLifecycle()
    val filteredBuses by viewModel.filteredBuses.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Screen state: if user logged out or explicitly in login mode
    var showLoginScreen by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    if (showLoginScreen && userSession?.isLoggedIn != true) {
        LoginScreen(
            onLoginSuccess = { fullName, phoneOrEmail, passType, passNumber ->
                viewModel.login(fullName, phoneOrEmail, passType, passNumber)
                showLoginScreen = false
            },
            onContinueAsGuest = {
                showLoginScreen = false
            },
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        )
        return
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        val isWideScreen = maxWidth >= 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = DarkBg,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.navigationBarsPadding()
                )
            },
            topBar = {
                TopKsrtcBar(
                    busCount = buses.size,
                    commuterName = userSession?.fullName ?: "Guest Commuter",
                    onProfileClick = { showLoginScreen = true }
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    BottomKsrtcNavBar(
                        currentTab = currentTab,
                        savedCount = savedRoutes.size,
                        onTabSelected = { viewModel.setTab(it) }
                    )
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = DarkSurfaceElevated,
                        contentColor = Color.White,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        AppNavTab.values().forEach { tab ->
                            NavigationRailItem(
                                selected = currentTab == tab,
                                onClick = { viewModel.setTab(tab) },
                                icon = {
                                    Icon(
                                        imageVector = getTabIcon(tab),
                                        contentDescription = tab.title
                                    )
                                },
                                label = { Text(tab.title, fontSize = 10.sp) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = KsrtcGold,
                                    selectedTextColor = KsrtcGold,
                                    indicatorColor = KsrtcRedPrimary,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                )
                            )
                        }
                    }
                }

                // Dynamic Screen Content with Animated Switch
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "tabContent"
                    ) { tab ->
                        when (tab) {
                            AppNavTab.LIVE_RADAR -> {
                                LiveTrackerScreen(
                                    buses = buses,
                                    selectedBus = selectedBus,
                                    stopAlarmEnabled = stopAlarmEnabled,
                                    onSelectBus = { viewModel.selectBus(it) },
                                    onToggleStopAlarm = { viewModel.toggleStopAlarm() },
                                    onShareTrip = { viewModel.shareLiveTrip() },
                                    onEmergencySos = { viewModel.triggerEmergencySos() }
                                )
                            }
                            AppNavTab.SEARCH_ROUTES -> {
                                BusSearchScreen(
                                    buses = filteredBuses,
                                    selectedBusId = selectedBus?.id,
                                    originQuery = originQuery,
                                    destQuery = destQuery,
                                    selectedFilter = selectedFilter,
                                    onOriginChange = { viewModel.updateSearchFilters(it, destQuery, selectedFilter) },
                                    onDestChange = { viewModel.updateSearchFilters(originQuery, it, selectedFilter) },
                                    onFilterChange = { viewModel.updateSearchFilters(originQuery, destQuery, it) },
                                    onSwap = { viewModel.swapOriginDest() },
                                    onSelectBus = { viewModel.selectBus(it) },
                                    onToggleFavorite = { viewModel.toggleFavorite(it) }
                                )
                            }
                            AppNavTab.MY_PASS_FAVS -> {
                                PassAndFavoritesScreen(
                                    userSession = userSession,
                                    savedRoutes = savedRoutes,
                                    buses = buses,
                                    onSelectBus = { viewModel.selectBus(it) },
                                    onRemoveSavedRoute = { viewModel.toggleFavorite(it) },
                                    onLogout = { viewModel.logout() },
                                    onNavigateToLogin = { showLoginScreen = true }
                                )
                            }
                            AppNavTab.ALERTS -> {
                                DepotAlertsScreen(
                                    alerts = alerts,
                                    onEmergencySos = { viewModel.triggerEmergencySos() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopKsrtcBar(
    busCount: Int,
    commuterName: String,
    onProfileClick: () -> Unit
) {
    Surface(
        color = DarkSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = KsrtcRedPrimary,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = "KSRTC",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "KSRTC",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "RADAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = KsrtcGold
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LivePulseDot(size = 5.dp, color = LiveGreen)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$busCount active in transit",
                            fontSize = 10.sp,
                            color = LiveGreen
                        )
                    }
                }
            }

            // User Profile Avatar Chip
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1B2230),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .clickable { onProfileClick() }
                    .testTag("top_profile_chip")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User",
                        tint = KsrtcGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = commuterName.take(12),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomKsrtcNavBar(
    currentTab: AppNavTab,
    savedCount: Int,
    onTabSelected: (AppNavTab) -> Unit
) {
    NavigationBar(
        containerColor = DarkSurfaceElevated,
        contentColor = Color.White,
        modifier = Modifier
            .navigationBarsPadding()
            .testTag("bottom_nav_bar")
    ) {
        AppNavTab.values().forEach { tab ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    if (tab == AppNavTab.MY_PASS_FAVS && savedCount > 0) {
                        BadgedBox(badge = {
                            Badge(containerColor = KsrtcRedPrimary) {
                                Text(savedCount.toString(), color = Color.White)
                            }
                        }) {
                            Icon(imageVector = getTabIcon(tab), contentDescription = tab.title)
                        }
                    } else {
                        Icon(imageVector = getTabIcon(tab), contentDescription = tab.title)
                    }
                },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = KsrtcGold,
                    selectedTextColor = KsrtcGold,
                    indicatorColor = KsrtcRedPrimary,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}

private fun getTabIcon(tab: AppNavTab): androidx.compose.ui.graphics.vector.ImageVector {
    return when (tab) {
        AppNavTab.LIVE_RADAR -> Icons.Default.Radar
        AppNavTab.SEARCH_ROUTES -> Icons.Default.Search
        AppNavTab.MY_PASS_FAVS -> Icons.Default.Bookmark
        AppNavTab.ALERTS -> Icons.Default.Campaign
    }
}
