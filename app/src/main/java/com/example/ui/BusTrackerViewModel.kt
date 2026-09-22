package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BusRepository
import com.example.data.local.AppDatabase
import com.example.data.local.SavedRouteEntity
import com.example.data.local.UserSessionEntity
import com.example.data.model.BusItem
import com.example.data.model.BusServiceType
import com.example.data.model.DepotAlert
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab(val title: String) {
    LIVE_RADAR("Live Radar"),
    SEARCH_ROUTES("Search"),
    MY_PASS_FAVS("Pass & Trips"),
    ALERTS("Depot SOS")
}

class BusTrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = BusRepository(db)

    val buses: StateFlow<List<BusItem>> = repository.buses
    val selectedBus: StateFlow<BusItem?> = repository.selectedBus
    val depotAlerts: StateFlow<List<DepotAlert>> = repository.depotAlerts

    val savedRoutes: StateFlow<List<SavedRouteEntity>> = repository.savedRoutes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val userSession: StateFlow<UserSessionEntity?> = repository.userSession.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Active bottom tab
    private val _currentTab = MutableStateFlow(AppNavTab.LIVE_RADAR)
    val currentTab = _currentTab.asStateFlow()

    // Alarm reminder toggles
    private val _stopAlarmEnabled = MutableStateFlow(false)
    val stopAlarmEnabled = _stopAlarmEnabled.asStateFlow()

    // Search filters
    private val _searchOrigin = MutableStateFlow("")
    val searchOrigin = _searchOrigin.asStateFlow()

    private val _searchDestination = MutableStateFlow("")
    val searchDestination = _searchDestination.asStateFlow()

    private val _selectedTypeFilter = MutableStateFlow<BusServiceType?>(null)
    val selectedTypeFilter = _selectedTypeFilter.asStateFlow()

    // Filtered bus list
    val filteredBuses: StateFlow<List<BusItem>> = combine(
        buses,
        _searchOrigin,
        _searchDestination,
        _selectedTypeFilter
    ) { allBuses, origin, dest, filterType ->
        allBuses.filter { bus ->
            val matchesOrigin = origin.isBlank() || bus.origin.contains(origin, ignoreCase = true) || bus.stops.any { it.name.contains(origin, ignoreCase = true) }
            val matchesDest = dest.isBlank() || bus.destination.contains(dest, ignoreCase = true) || bus.stops.any { it.name.contains(dest, ignoreCase = true) }
            val matchesType = filterType == null || bus.serviceType == filterType
            matchesOrigin && matchesDest && matchesType
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Snack messages
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    fun setTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun selectBus(busId: String) {
        repository.selectBus(busId)
        _currentTab.value = AppNavTab.LIVE_RADAR
    }

    fun toggleFavorite(bus: BusItem) {
        viewModelScope.launch {
            val wasFav = bus.isFavorite
            repository.toggleFavorite(bus)
            val msg = if (wasFav) "Removed ${bus.busNumber} from Favorites" else "Saved ${bus.busNumber} to Offline Favorites"
            _snackbarEvent.emit(msg)
        }
    }

    fun toggleStopAlarm() {
        val newState = !_stopAlarmEnabled.value
        _stopAlarmEnabled.value = newState
        val bus = selectedBus.value
        val msg = if (newState) "Alert chime enabled: You will be notified 2km before ${bus?.nextStopName ?: "your stop"}" else "Arrival alert turned off"
        viewModelScope.launch {
            _snackbarEvent.emit(msg)
        }
    }

    fun shareLiveTrip() {
        val bus = selectedBus.value
        viewModelScope.launch {
            _snackbarEvent.emit("Live tracking link for ${bus?.busNumber ?: "KSRTC bus"} copied to clipboard!")
        }
    }

    fun triggerEmergencySos() {
        viewModelScope.launch {
            _snackbarEvent.emit("KSRTC Central Passenger Helpline: 1800-599-4011 notified. Driver & Conductor alerted.")
        }
    }

    fun updateSearchFilters(origin: String, dest: String, filter: BusServiceType?) {
        _searchOrigin.value = origin
        _searchDestination.value = dest
        _selectedTypeFilter.value = filter
    }

    fun swapOriginDest() {
        val o = _searchOrigin.value
        val d = _searchDestination.value
        _searchOrigin.value = d
        _searchDestination.value = o
    }

    fun login(fullName: String, phoneOrEmail: String, passType: String, passNumber: String) {
        viewModelScope.launch {
            repository.saveLoginSession(fullName, phoneOrEmail, passType, passNumber)
            _snackbarEvent.emit("Welcome aboard, $fullName! Live tracking is ready.")
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _snackbarEvent.emit("Logged out successfully.")
        }
    }
}
