package com.example.data.model

enum class BusServiceType(val displayName: String, val badgeColorHex: Long) {
    SWIFT_DELUXE("KSRTC-SWIFT Deluxe", 0xFFE53935),
    AIRAVAT_DIAMOND("Airavat Club Class", 0xFF8E24AA),
    SUPER_FAST("Super Fast Express", 0xFFFF6F00),
    RAJAHAMSA("Rajahamsa Executive", 0xFF0288D1),
    MINNAL_NIGHT("Minnal Night Express", 0xFF00B0FF),
    ELECTRIC_CITY("KSRTC e-Bus", 0xFF00C853)
}

data class BusStop(
    val name: String,
    val landmark: String,
    val scheduledTime: String,
    val eta: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean,
    val isUpcoming: Boolean,
    val distanceFromStartKm: Float
)

data class LiveTelemetry(
    val speedKmH: Int,
    val engineTempC: Int,
    val gpsAccuracyMeters: Float,
    val headingDegrees: Float,
    val lastPingSecondsAgo: Int
)

data class BusItem(
    val id: String,
    val busNumber: String,
    val serviceName: String,
    val serviceType: BusServiceType,
    val origin: String,
    val destination: String,
    val viaRoute: String,
    val departureTime: String,
    val arrivalTime: String,
    val currentStopName: String,
    val nextStopName: String,
    val distanceToNextKm: Float,
    val nextStopEtaMinutes: Int,
    val speedKmH: Int,
    val delayMinutes: Int, // 0 = on time, >0 = delayed
    val totalSeats: Int,
    val availableSeats: Int,
    val fareRupees: Int,
    val driverName: String,
    val conductorPhone: String,
    val progress: Float, // 0.0 to 1.0
    val crowdDensity: String, // "Light", "Moderate", "High"
    val stops: List<BusStop>,
    val isFavorite: Boolean = false,
    val hasAc: Boolean = true,
    val hasWifi: Boolean = true,
    val hasChargingPoints: Boolean = true
)

data class CommuterProfile(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val passType: String, // "Regular Commuter", "Student Pass (90% Concession)", "State Senior Pass"
    val passNumber: String,
    val passExpiry: String,
    val walletBalanceRupees: Double,
    val avatarInitial: String
)

data class DepotAlert(
    val id: String,
    val title: String,
    val depotName: String,
    val timeAgo: String,
    val message: String,
    val severity: String, // "Info", "Alert", "Notice"
    val routeAffected: String
)
