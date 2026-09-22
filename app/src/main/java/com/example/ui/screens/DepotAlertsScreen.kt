package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DepotAlert
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DelayedRed
import com.example.ui.theme.KsrtcAmber
import com.example.ui.theme.KsrtcGold
import com.example.ui.theme.KsrtcRedPrimary
import com.example.ui.theme.LiveGreen
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.TextSecondary

@Composable
fun DepotAlertsScreen(
    alerts: List<DepotAlert>,
    onEmergencySos: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("depot_alerts_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Emergency SOS Broadcast Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A0D10)),
                border = BorderStroke(1.dp, DelayedRed)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = DelayedRed,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Emergency,
                                    contentDescription = "SOS",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "KSRTC TRANSIT SOS & HELPLINE",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Instant 24x7 Safety & Breakdown Support",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onEmergencySos,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("sos_broadcast_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = DelayedRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Trigger Emergency Assistance", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // 24x7 Helpline Directory Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DEPOT & PASSENGER DIRECTORY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RadarCyan
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    HelplineRow(title = "Central Control Room (Tvm)", number = "0471-2463799", tag = "24x7")
                    HelplineRow(title = "KSRTC Toll-Free Helpline", number = "1800-599-4011", tag = "Free")
                    HelplineRow(title = "Women Passenger Safety Cell", number = "94470 71021", tag = "SOS")
                    HelplineRow(title = "Bengaluru Shanthinagar Depot", number = "080-22221321", tag = "Interstate")
                }
            }
        }

        // Live Transit Alerts & Road Conditions
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = "LIVE ROUTE ADVISORIES & BULLETINS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = KsrtcAmber
                )
            }
        }

        items(alerts, key = { it.id }) { alert ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (alert.severity) {
                                "Alert" -> DelayedRed.copy(alpha = 0.2f)
                                "Notice" -> KsrtcAmber.copy(alpha = 0.2f)
                                else -> LiveGreen.copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = alert.severity.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (alert.severity) {
                                    "Alert" -> DelayedRed
                                    "Notice" -> KsrtcAmber
                                    else -> LiveGreen
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = alert.timeAgo,
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = alert.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = alert.message,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Sector: ${alert.routeAffected}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KsrtcGold
                    )
                }
            }
        }
    }
}

@Composable
private fun HelplineRow(
    title: String,
    number: String,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 12.sp, color = Color.White)
            Text(text = number, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LiveGreen)
        }

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFF1E2430)
        ) {
            Text(
                text = tag,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = KsrtcAmber,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
