package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusItem
import com.example.data.model.BusStop
import com.example.ui.components.AnimatedRadarMap
import com.example.ui.components.LivePulseDot
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DelayedRed
import com.example.ui.theme.KsrtcAmber
import com.example.ui.theme.KsrtcGold
import com.example.ui.theme.KsrtcRedPrimary
import com.example.ui.theme.LiveGreen
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.TextSecondary

@Composable
fun LiveTrackerScreen(
    buses: List<BusItem>,
    selectedBus: BusItem?,
    stopAlarmEnabled: Boolean,
    onSelectBus: (String) -> Unit,
    onToggleStopAlarm: () -> Unit,
    onShareTrip: () -> Unit,
    onEmergencySos: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bus = selectedBus ?: buses.firstOrNull()

    if (bus == null) {
        Box(
            modifier = modifier.fillMaxSize().background(DarkBg),
            contentAlignment = Alignment.Center
        ) {
            Text("No bus active in tracker", color = Color.White)
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("live_tracker_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Bus Quick-Switch Carousel
        item {
            Column(modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)) {
                Text(
                    text = "ACTIVE FLEET RADAR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = RadarCyan,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(buses, key = { it.id }) { item ->
                        val isCurrent = item.id == bus.id
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelectBus(item.id) }
                                .testTag("fleet_pill_${item.busNumber}"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isCurrent) KsrtcRedPrimary else DarkSurfaceElevated,
                            border = BorderStroke(
                                1.dp,
                                if (isCurrent) KsrtcGold else DarkBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isCurrent) {
                                    LivePulseDot(size = 6.dp, color = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Column {
                                    Text(
                                        text = item.busNumber,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${item.origin.take(5)} → ${item.destination.take(5)}",
                                        fontSize = 10.sp,
                                        color = if (isCurrent) Color.White.copy(alpha = 0.9f) else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Animated Radar Map View (Canvas)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                AnimatedRadarMap(
                    bus = bus,
                    modifier = Modifier.fillMaxSize(),
                    onRecenterClick = {}
                )
            }
        }

        // 3. Live Telemetry & Control Dashboard
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Title and Service Type
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = bus.serviceName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Route: ${bus.origin} to ${bus.destination}",
                                fontSize = 12.sp,
                                color = KsrtcAmber
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (bus.delayMinutes == 0) LiveGreen.copy(alpha = 0.15f) else KsrtcAmber.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (bus.delayMinutes == 0) "ON SCHEDULE" else "+${bus.delayMinutes}m DELAY",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (bus.delayMinutes == 0) LiveGreen else KsrtcAmber
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Gauges Matrix (Speed, Next Stop ETA, Crowd, Available Seats)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TelemetryMetricBox(
                            icon = Icons.Default.Speed,
                            label = "SPEED",
                            value = "${bus.speedKmH} km/h",
                            valueColor = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryMetricBox(
                            icon = Icons.Default.Timer,
                            label = "NEXT STOP",
                            value = "${bus.nextStopEtaMinutes} mins",
                            valueColor = RadarCyan,
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryMetricBox(
                            icon = Icons.Default.AirlineSeatReclineNormal,
                            label = "SEATS LEFT",
                            value = "${bus.availableSeats}/${bus.totalSeats}",
                            valueColor = if (bus.availableSeats > 8) LiveGreen else KsrtcAmber,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Next Stop Corridor Indicator
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceCard,
                        border = BorderStroke(1.dp, DarkBorder.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LivePulseDot(size = 8.dp, color = LiveGreen)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "APPROACHING NEXT STOP",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = bus.nextStopName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Text(
                                text = "${bus.distanceToNextKm} km",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = KsrtcGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Button Controls: Stop Alarm, Share, SOS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Alarm toggle
                        Button(
                            onClick = onToggleStopAlarm,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("toggle_stop_alarm_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (stopAlarmEnabled) LiveGreen else Color(0xFF1F2633)
                            )
                        ) {
                            Icon(
                                imageVector = if (stopAlarmEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                                contentDescription = "Alarm",
                                modifier = Modifier.size(18.dp),
                                tint = if (stopAlarmEnabled) Color.Black else Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (stopAlarmEnabled) "Alert ON" else "Stop Alert",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (stopAlarmEnabled) Color.Black else Color.White
                            )
                        }

                        // Share trip
                        OutlinedButton(
                            onClick = onShareTrip,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("share_trip_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, DarkBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                modifier = Modifier.size(16.dp),
                                tint = RadarCyan
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // Emergency SOS
                        Button(
                            onClick = onEmergencySos,
                            modifier = Modifier
                                .size(46.dp)
                                .testTag("emergency_sos_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DelayedRed),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Emergency,
                                contentDescription = "SOS",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Crew Details (Driver & Conductor)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF10131A), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Driver: ${bus.driverName}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = LiveGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = bus.conductorPhone,
                                fontSize = 11.sp,
                                color = LiveGreen
                            )
                        }
                    }
                }
            }
        }

        // 4. Live Interactive Stop-by-Stop Timeline
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ROUTE TIMELINE & STOPS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RadarCyan
                    )
                    Text(
                        text = "${bus.stops.size} Waypoints",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        bus.stops.forEachIndexed { index, stop ->
                            TimelineStopRow(
                                stop = stop,
                                isFirst = index == 0,
                                isLast = index == bus.stops.size - 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryMetricBox(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkSurfaceCard,
        border = BorderStroke(1.dp, DarkBorder.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = valueColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                maxLines = 1
            )
            Text(
                text = label,
                fontSize = 9.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun TimelineStopRow(
    stop: BusStop,
    isFirst: Boolean,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Vertical indicator dot and line connector
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            if (stop.isCurrent) {
                LivePulseDot(size = 8.dp, color = KsrtcGold)
            } else if (stop.isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Passed",
                    tint = LiveGreen,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E384D))
                        .border(1.dp, RadarCyan.copy(alpha = 0.5f), CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Stop name & Landmark
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stop.name,
                fontSize = 13.sp,
                fontWeight = if (stop.isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                color = if (stop.isCurrent) Color.White else if (stop.isCompleted) TextSecondary else Color.White.copy(alpha = 0.85f)
            )
            Text(
                text = stop.landmark,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        // Time / ETA
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (stop.isCurrent) "NOW" else stop.eta,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (stop.isCurrent) KsrtcGold else if (stop.isCompleted) LiveGreen else RadarCyan
            )
            Text(
                text = "${stop.distanceFromStartKm} km",
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}
