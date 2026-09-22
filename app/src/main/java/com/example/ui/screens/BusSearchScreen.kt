package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusItem
import com.example.data.model.BusServiceType
import com.example.ui.components.AnimatedBusCard
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.KsrtcAmber
import com.example.ui.theme.KsrtcGold
import com.example.ui.theme.KsrtcRedPrimary
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.TextSecondary

@Composable
fun BusSearchScreen(
    buses: List<BusItem>,
    selectedBusId: String?,
    originQuery: String,
    destQuery: String,
    selectedFilter: BusServiceType?,
    onOriginChange: (String) -> Unit,
    onDestChange: (String) -> Unit,
    onFilterChange: (BusServiceType?) -> Unit,
    onSwap: () -> Unit,
    onSelectBus: (String) -> Unit,
    onToggleFavorite: (BusItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickStops = listOf(
        "Thiruvananthapuram", "Ernakulam", "Kozhikode", "Thrissur", "Bengaluru", "Kottayam", "Alappuzha"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("bus_search_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Search Inputs Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "FIND KSRTC SERVICES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RadarCyan
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            // Origin Field
                            OutlinedTextField(
                                value = originQuery,
                                onValueChange = onOriginChange,
                                placeholder = { Text("Boarding from (e.g. Ernakulam)", fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "From",
                                        tint = KsrtcGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (originQuery.isNotEmpty()) {
                                        IconButton(onClick = { onOriginChange("") }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("search_origin_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = KsrtcRedPrimary,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Destination Field
                            OutlinedTextField(
                                value = destQuery,
                                onValueChange = onDestChange,
                                placeholder = { Text("Destination to (e.g. Kozhikode)", fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsBus,
                                        contentDescription = "To",
                                        tint = RadarCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (destQuery.isNotEmpty()) {
                                        IconButton(onClick = { onDestChange("") }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("search_dest_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = KsrtcRedPrimary,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Swap Button
                        Surface(
                            shape = CircleShape,
                            color = KsrtcRedPrimary,
                            modifier = Modifier
                                .size(44.dp)
                                .clickable { onSwap() }
                                .testTag("swap_cities_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SwapVert,
                                    contentDescription = "Swap",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick City Suggestion Chips
                    Text(
                        text = "POPULAR HUBS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(quickStops) { stop ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF191F2B),
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier
                                    .clickable {
                                        if (originQuery.isBlank()) onOriginChange(stop)
                                        else onDestChange(stop)
                                    }
                                    .testTag("quick_hub_$stop")
                            ) {
                                Text(
                                    text = stop,
                                    fontSize = 11.sp,
                                    color = KsrtcAmber,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Service Type Filter Row
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text(
                    text = "SERVICE FLEET FILTER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = RadarCyan
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterPill(
                            title = "All Fleet",
                            isSelected = selectedFilter == null,
                            onClick = { onFilterChange(null) }
                        )
                    }
                    items(BusServiceType.values()) { type ->
                        FilterPill(
                            title = type.displayName.take(15),
                            isSelected = selectedFilter == type,
                            onClick = { onFilterChange(if (selectedFilter == type) null else type) }
                        )
                    }
                }
            }
        }

        // Results Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AVAILABLE SERVICES (${buses.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (originQuery.isNotEmpty() || destQuery.isNotEmpty() || selectedFilter != null) {
                    Text(
                        text = "Filtered",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KsrtcAmber
                    )
                }
            }
        }

        // List of Buses
        if (buses.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No KSRTC services found matching filters",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Try searching for Ernakulam, Kozhikode, or Bengaluru",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(buses, key = { it.id }) { bus ->
                AnimatedBusCard(
                    bus = bus,
                    isSelected = bus.id == selectedBusId,
                    onSelect = { onSelectBus(bus.id) },
                    onToggleFavorite = { onToggleFavorite(bus) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun FilterPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) KsrtcRedPrimary else DarkSurfaceElevated,
        border = BorderStroke(1.dp, if (isSelected) KsrtcGold else DarkBorder),
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("filter_pill_$title")
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else TextSecondary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
