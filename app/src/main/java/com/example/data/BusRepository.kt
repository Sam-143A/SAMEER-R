package com.example.data

import com.example.data.local.AppDatabase
import com.example.data.local.SavedRouteEntity
import com.example.data.local.UserSessionEntity
import com.example.data.model.BusItem
import com.example.data.model.BusServiceType
import com.example.data.model.BusStop
import com.example.data.model.CommuterProfile
import com.example.data.model.DepotAlert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.random.Random

class BusRepository(private val db: AppDatabase) {

    private val _buses = MutableStateFlow<List<BusItem>>(initialBuses())
    val buses = _buses.asStateFlow()

    private val _selectedBus = MutableStateFlow<BusItem?>(initialBuses().first())
    val selectedBus = _selectedBus.asStateFlow()

    private val _depotAlerts = MutableStateFlow<List<DepotAlert>>(initialAlerts())
    val depotAlerts = _depotAlerts.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    init {
        // Start continuous live telemetry & location pulse simulation
        coroutineScope.launch {
            while (true) {
                delay(3000) // update telemetry every 3 seconds
                updateSimulationCycle()
            }
        }
    }

    val savedRoutes: Flow<List<SavedRouteEntity>> = db.savedRouteDao().getAllSavedRoutes()
    val userSession: Flow<UserSessionEntity?> = db.userSessionDao().getUserSession()

    fun selectBus(busId: String) {
        val found = _buses.value.find { it.id == busId }
        if (found != null) {
            _selectedBus.value = found
        }
    }

    suspend fun toggleFavorite(bus: BusItem) {
        val currentSaved = _buses.value.find { it.id == bus.id }?.isFavorite ?: false
        if (currentSaved) {
            db.savedRouteDao().deleteByBusId(bus.id)
        } else {
            db.savedRouteDao().insertSavedRoute(
                SavedRouteEntity(
                    busId = bus.id,
                    busNumber = bus.busNumber,
                    serviceName = bus.serviceName,
                    origin = bus.origin,
                    destination = bus.destination
                )
            )
        }

        _buses.value = _buses.value.map {
            if (it.id == bus.id) it.copy(isFavorite = !currentSaved) else it
        }
        if (_selectedBus.value?.id == bus.id) {
            _selectedBus.value = _selectedBus.value?.copy(isFavorite = !currentSaved)
        }
    }

    suspend fun saveLoginSession(fullName: String, phoneOrEmail: String, passType: String, passNumber: String) {
        db.userSessionDao().saveUserSession(
            UserSessionEntity(
                id = 1,
                fullName = fullName,
                phoneOrEmail = phoneOrEmail,
                passType = passType,
                passNumber = passNumber,
                isLoggedIn = true,
                walletBalance = 420.50
            )
        )
    }

    suspend fun logout() {
        db.userSessionDao().clearSession()
    }

    private fun updateSimulationCycle() {
        val currentList = _buses.value
        val updated = currentList.map { bus ->
            // Advance progress gently along route
            val newProgress = (bus.progress + 0.006f).let { if (it > 1.0f) 0.05f else it }
            val jitterSpeed = (bus.speedKmH + Random.nextInt(-3, 4)).coerceIn(35, 78)
            val jitterMinutes = (bus.nextStopEtaMinutes - if (Random.nextBoolean()) 1 else 0).coerceAtLeast(1)
            val jitterDist = (bus.distanceToNextKm - 0.2f).let { if (it <= 0.1f) 4.5f else it }

            bus.copy(
                progress = newProgress,
                speedKmH = jitterSpeed,
                nextStopEtaMinutes = jitterMinutes,
                distanceToNextKm = "%.1f".format(jitterDist).toFloatOrNull() ?: jitterDist
            )
        }
        _buses.value = updated

        val activeId = _selectedBus.value?.id
        if (activeId != null) {
            _selectedBus.value = updated.find { it.id == activeId }
        }
    }

    companion object {
        private fun initialBuses(): List<BusItem> = listOf(
            BusItem(
                id = "b1",
                busNumber = "KL-15-A-1284",
                serviceName = "SWIFT Gajaraj Deluxe Air-Suspension",
                serviceType = BusServiceType.SWIFT_DELUXE,
                origin = "Thiruvananthapuram",
                destination = "Kozhikode",
                viaRoute = "Kollam • Ernakulam • Thrissur",
                departureTime = "06:30 AM",
                arrivalTime = "03:45 PM",
                currentStopName = "Ernakulam Vyttila Mobility Hub",
                nextStopName = "Aluva KSRTC Stand",
                distanceToNextKm = 12.4f,
                nextStopEtaMinutes = 18,
                speedKmH = 62,
                delayMinutes = 0,
                totalSeats = 45,
                availableSeats = 14,
                fareRupees = 490,
                driverName = "M. S. Radhakrishnan",
                conductorPhone = "+91 94470 12345",
                progress = 0.52f,
                crowdDensity = "Moderate",
                stops = listOf(
                    BusStop("Thiruvananthapuram Central", "Platform 4", "06:30 AM", "06:30 AM", isCompleted = true, isCurrent = false, isUpcoming = false, 0f),
                    BusStop("Attingal KSRTC", "NH-66 Bus Bay", "07:15 AM", "07:15 AM", isCompleted = true, isCurrent = false, isUpcoming = false, 32f),
                    BusStop("Kollam KSRTC Depot", "Platform 2", "08:10 AM", "08:12 AM", isCompleted = true, isCurrent = false, isUpcoming = false, 71f),
                    BusStop("Alappuzha KSRTC Stand", "Near Canal", "09:40 AM", "09:42 AM", isCompleted = true, isCurrent = false, isUpcoming = false, 154f),
                    BusStop("Ernakulam Vyttila Hub", "Bay 6 (Pulsing)", "11:15 AM", "11:18 AM", isCompleted = false, isCurrent = true, isUpcoming = false, 218f),
                    BusStop("Aluva Bus Terminal", "Near Metro Pillar 42", "11:45 AM", "11:48 AM", isCompleted = false, isCurrent = false, isUpcoming = true, 235f),
                    BusStop("Thrissur Shakthan Stand", "Bay 1", "12:50 PM", "12:55 PM", isCompleted = false, isCurrent = false, isUpcoming = true, 288f),
                    BusStop("Kozhikode KSRTC Depot", "Mavoor Road Terminal", "03:45 PM", "03:45 PM", isCompleted = false, isCurrent = false, isUpcoming = true, 410f)
                ),
                isFavorite = true
            ),
            BusItem(
                id = "b2",
                busNumber = "KA-01-F-7822",
                serviceName = "Airavat Diamond Class Multi-Axle",
                serviceType = BusServiceType.AIRAVAT_DIAMOND,
                origin = "Bengaluru",
                destination = "Ernakulam",
                viaRoute = "Hosur • Salem • Coimbatore • Palakkad",
                departureTime = "09:00 PM",
                arrivalTime = "06:30 AM",
                currentStopName = "Palakkad KSRTC Complex",
                nextStopName = "Vadakkencherry Bypass",
                distanceToNextKm = 24.8f,
                nextStopEtaMinutes = 26,
                speedKmH = 74,
                delayMinutes = 4,
                totalSeats = 49,
                availableSeats = 6,
                fareRupees = 1120,
                driverName = "G. Venkatesh",
                conductorPhone = "+91 94480 88912",
                progress = 0.78f,
                crowdDensity = "High",
                stops = listOf(
                    BusStop("Bengaluru Shanthinagar", "Terminal 3", "09:00 PM", "09:05 PM", isCompleted = true, isCurrent = false, isUpcoming = false, 0f),
                    BusStop("Hosur Bus Bay", "Flyover Exit", "09:55 PM", "10:00 PM", isCompleted = true, isCurrent = false, isUpcoming = false, 40f),
                    BusStop("Salem New Stand", "Platform 9", "01:10 AM", "01:15 AM", isCompleted = true, isCurrent = false, isUpcoming = false, 200f),
                    BusStop("Coimbatore Gandhipuram", "Bay 3", "03:30 AM", "03:35 AM", isCompleted = true, isCurrent = false, isUpcoming = false, 360f),
                    BusStop("Palakkad KSRTC Complex", "Bay 8", "04:45 AM", "04:50 AM", isCompleted = false, isCurrent = true, isUpcoming = false, 415f),
                    BusStop("Vadakkencherry", "Bypass Stop", "05:20 AM", "05:25 AM", isCompleted = false, isCurrent = false, isUpcoming = true, 448f),
                    BusStop("Ernakulam South", "Near Railway Station", "06:30 AM", "06:35 AM", isCompleted = false, isCurrent = false, isUpcoming = true, 530f)
                ),
                isFavorite = false
            ),
            BusItem(
                id = "b3",
                busNumber = "KL-15-B-3341",
                serviceName = "KSRTC Super Fast Silverline",
                serviceType = BusServiceType.SUPER_FAST,
                origin = "Kottayam",
                destination = "Thiruvananthapuram",
                viaRoute = "Changanassery • Adoor • Kottarakkara",
                departureTime = "02:15 PM",
                arrivalTime = "06:10 PM",
                currentStopName = "Adoor KSRTC Central",
                nextStopName = "Kottarakkara Bus Depot",
                distanceToNextKm = 18.2f,
                nextStopEtaMinutes = 22,
                speedKmH = 55,
                delayMinutes = 0,
                totalSeats = 52,
                availableSeats = 21,
                fareRupees = 195,
                driverName = "K. J. Thomas",
                conductorPhone = "+91 94471 44521",
                progress = 0.44f,
                crowdDensity = "Moderate",
                stops = listOf(
                    BusStop("Kottayam KSRTC Depot", "Platform 1", "02:15 PM", "02:15 PM", isCompleted = true, isCurrent = false, isUpcoming = false, 0f),
                    BusStop("Changanassery Stand", "Bay 2", "02:45 PM", "02:47 PM", isCompleted = true, isCurrent = false, isUpcoming = false, 18f),
                    BusStop("Thiruvalla Deepa Junction", "Stop 4", "03:10 PM", "03:12 PM", isCompleted = true, isCurrent = false, isUpcoming = false, 28f),
                    BusStop("Adoor KSRTC Central", "Platform 3", "03:55 PM", "04:00 PM", isCompleted = false, isCurrent = true, isUpcoming = false, 58f),
                    BusStop("Kottarakkara Bus Depot", "Platform 2", "04:30 PM", "04:32 PM", isCompleted = false, isCurrent = false, isUpcoming = true, 82f),
                    BusStop("Venjaramoodu Junction", "Main Road", "05:25 PM", "05:27 PM", isCompleted = false, isCurrent = false, isUpcoming = true, 122f),
                    BusStop("Thiruvananthapuram Central", "Main Stand", "06:10 PM", "06:10 PM", isCompleted = false, isCurrent = false, isUpcoming = true, 150f)
                ),
                isFavorite = false
            ),
            BusItem(
                id = "b4",
                busNumber = "KL-15-C-9902",
                serviceName = "Minnal Non-Stop Night Express",
                serviceType = BusServiceType.MINNAL_NIGHT,
                origin = "Kozhikode",
                destination = "Thiruvananthapuram",
                viaRoute = "Thrissur • Ernakulam Bypass • Alappuzha",
                departureTime = "10:00 PM",
                arrivalTime = "05:00 AM",
                currentStopName = "Thrissur Bypass Junction",
                nextStopName = "Ernakulam Vyttila Bypass",
                distanceToNextKm = 52.0f,
                nextStopEtaMinutes = 48,
                speedKmH = 75,
                delayMinutes = 0,
                totalSeats = 40,
                availableSeats = 4,
                fareRupees = 560,
                driverName = "S. Nambiar",
                conductorPhone = "+91 94460 77123",
                progress = 0.38f,
                crowdDensity = "High",
                stops = listOf(
                    BusStop("Kozhikode Depot", "Bay 1", "10:00 PM", "10:00 PM", isCompleted = true, isCurrent = false, isUpcoming = false, 0f),
                    BusStop("Malappuram Bypass", "NH Stop", "10:55 PM", "10:55 PM", isCompleted = true, isCurrent = false, isUpcoming = false, 50f),
                    BusStop("Thrissur Bypass", "Flyover Junction", "12:45 AM", "12:48 AM", isCompleted = false, isCurrent = true, isUpcoming = false, 140f),
                    BusStop("Ernakulam Vyttila", "Bypass Hub", "02:10 AM", "02:15 AM", isCompleted = false, isCurrent = false, isUpcoming = true, 215f),
                    BusStop("Alappuzha Bypass", "Flyover Bay", "03:15 AM", "03:18 AM", isCompleted = false, isCurrent = false, isUpcoming = true, 280f),
                    BusStop("Kollam Bypass", "Kadappakada Exit", "04:10 AM", "04:12 AM", isCompleted = false, isCurrent = false, isUpcoming = true, 360f),
                    BusStop("Thiruvananthapuram Thampanoor", "Platform 1", "05:00 AM", "05:00 AM", isCompleted = false, isCurrent = false, isUpcoming = true, 430f)
                ),
                isFavorite = true
            ),
            BusItem(
                id = "b5",
                busNumber = "KL-01-E-2024",
                serviceName = "KSRTC Electric City Circular",
                serviceType = BusServiceType.ELECTRIC_CITY,
                origin = "Thampanoor Central",
                destination = "Technopark Phase III",
                viaRoute = "Statue • PMG • Pattom • Kesavadasapuram • Ulloor",
                departureTime = "08:15 AM",
                arrivalTime = "09:10 AM",
                currentStopName = "Pattom Palace Jn",
                nextStopName = "Kesavadasapuram Depot",
                distanceToNextKm = 1.8f,
                nextStopEtaMinutes = 4,
                speedKmH = 42,
                delayMinutes = 2,
                totalSeats = 36,
                availableSeats = 18,
                fareRupees = 30,
                driverName = "Vineeth Kumar",
                conductorPhone = "+91 94473 99881",
                progress = 0.40f,
                crowdDensity = "Light",
                stops = listOf(
                    BusStop("Thampanoor Central", "City Stand", "08:15 AM", "08:15 AM", isCompleted = true, isCurrent = false, isUpcoming = false, 0f),
                    BusStop("Statue Junction", "Secretariat Gate", "08:23 AM", "08:24 AM", isCompleted = true, isCurrent = false, isUpcoming = false, 2.5f),
                    BusStop("PMG Junction", "Science Museum", "08:31 AM", "08:32 AM", isCompleted = true, isCurrent = false, isUpcoming = false, 5f),
                    BusStop("Pattom Palace", "Near St. Marys", "08:38 AM", "08:40 AM", isCompleted = false, isCurrent = true, isUpcoming = false, 7f),
                    BusStop("Kesavadasapuram", "Bus Shelter", "08:44 AM", "08:46 AM", isCompleted = false, isCurrent = false, isUpcoming = true, 9f),
                    BusStop("Ulloor Bridge", "Medical College Road", "08:50 AM", "08:52 AM", isCompleted = false, isCurrent = false, isUpcoming = true, 11f),
                    BusStop("Kazhakkoottam", "NH Junction", "09:02 AM", "09:04 AM", isCompleted = false, isCurrent = false, isUpcoming = true, 16f),
                    BusStop("Technopark Phase III", "Main Gate", "09:10 AM", "09:10 AM", isCompleted = false, isCurrent = false, isUpcoming = true, 18f)
                ),
                isFavorite = false
            )
        )

        private fun initialAlerts(): List<DepotAlert> = listOf(
            DepotAlert(
                id = "a1",
                title = "Live GPS Telemetry System Active",
                depotName = "State Transit Operations Control",
                timeAgo = "10 mins ago",
                message = "High-precision satellite GPS tracking is now live across all SWIFT and Super Fast interstate routes.",
                severity = "Info",
                routeAffected = "All Kerala & Interstate Corridors"
            ),
            DepotAlert(
                id = "a2",
                title = "NH-66 Road Widening Diversion",
                depotName = "Ernakulam Depot",
                timeAgo = "35 mins ago",
                message = "Aluva-Kalamassery sector has slow movement due to flyover construction. Expected delay of 10-15 minutes.",
                severity = "Alert",
                routeAffected = "Ernakulam - Thrissur Corridor"
            ),
            DepotAlert(
                id = "a3",
                title = "Special Onam/Festival Swift Services Added",
                depotName = "Bengaluru Shanthinagar Control",
                timeAgo = "2 hours ago",
                message = "14 additional Airavat and Swift Deluxe services deployed on Bengaluru-Kozhikode and Bengaluru-Ernakulam routes.",
                severity = "Notice",
                routeAffected = "Karnataka - Kerala Interstate Routes"
            )
        )
    }
}
