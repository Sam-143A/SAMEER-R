package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusItem
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.KsrtcAmber
import com.example.ui.theme.KsrtcGold
import com.example.ui.theme.KsrtcRedPrimary
import com.example.ui.theme.LiveGreen
import com.example.ui.theme.RadarCyan
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedRadarMap(
    bus: BusItem,
    modifier: Modifier = Modifier,
    onRecenterClick: () -> Unit = {}
) {
    // Radar sweep rotation angle
    val infiniteTransition = rememberInfiniteTransition(label = "radarTransition")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAngle"
    )

    // Pulsing circle wave around bus
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 2.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    // Animated dash offset for road traffic flow
    val dashPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dashPhase"
    )

    var zoomLevel by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(DarkBg)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            // 1. Draw subtle radar grid lines & concentric rings
            val centerX = canvasW * 0.5f
            val centerY = canvasH * 0.5f
            val maxRadius = (canvasW.coerceAtLeast(canvasH)) * 0.65f * zoomLevel

            // Background tactical grid
            val gridStep = 45f * zoomLevel
            var x = 0f
            while (x <= canvasW) {
                drawLine(
                    color = DarkBorder.copy(alpha = 0.25f),
                    start = Offset(x, 0f),
                    end = Offset(x, canvasH),
                    strokeWidth = 1f
                )
                x += gridStep
            }
            var y = 0f
            while (y <= canvasH) {
                drawLine(
                    color = DarkBorder.copy(alpha = 0.25f),
                    start = Offset(0f, y),
                    end = Offset(canvasW, y),
                    strokeWidth = 1f
                )
                y += gridStep
            }

            // Radar range rings centered on middle
            val ringCount = 3
            for (i in 1..ringCount) {
                val r = (maxRadius / ringCount) * i
                drawCircle(
                    color = RadarCyan.copy(alpha = 0.12f),
                    radius = r,
                    center = Offset(centerX, centerY),
                    style = Stroke(
                        width = 1.2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 12f), 0f)
                    )
                )
            }

            // 2. Define Highway Route Curvature (S-Curve connecting waypoints)
            // Route points calculated dynamically relative to canvas size
            val p0 = Offset(canvasW * 0.12f, canvasH * 0.85f) // Origin
            val p1 = Offset(canvasW * 0.32f, canvasH * 0.65f)
            val p2 = Offset(canvasW * 0.28f, canvasH * 0.42f)
            val p3 = Offset(canvasW * 0.62f, canvasH * 0.48f)
            val p4 = Offset(canvasW * 0.72f, canvasH * 0.28f)
            val p5 = Offset(canvasW * 0.88f, canvasH * 0.15f) // Terminus

            val routePoints = listOf(p0, p1, p2, p3, p4, p5)

            // Draw Highway Roadbed (under-glow)
            val roadPath = Path().apply {
                moveTo(p0.x, p0.y)
                cubicTo(p1.x, p1.y, p1.x, p1.y, p2.x, p2.y)
                cubicTo(p2.x, p2.y, p3.x, p3.y, p3.x, p3.y)
                cubicTo(p3.x, p3.y, p4.x, p4.y, p4.x, p4.y)
                cubicTo(p4.x, p4.y, p5.x, p5.y, p5.x, p5.y)
            }

            // Highway glow outline
            drawPath(
                path = roadPath,
                color = KsrtcRedPrimary.copy(alpha = 0.25f),
                style = Stroke(width = 16f, cap = StrokeCap.Round)
            )

            // Road surface dark asphalt
            drawPath(
                path = roadPath,
                color = Color(0xFF1B2230),
                style = Stroke(width = 10f, cap = StrokeCap.Round)
            )

            // Animated highway center-line dash
            drawPath(
                path = roadPath,
                brush = Brush.linearGradient(
                    colors = listOf(LiveGreen, RadarCyan, KsrtcAmber)
                ),
                style = Stroke(
                    width = 4f,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), dashPhase)
                )
            )

            // 3. Compute bus position along the curve based on progress
            val t = bus.progress.coerceIn(0f, 1f)
            // Interpolate position through segments
            val segmentCount = routePoints.size - 1
            val scaledT = t * segmentCount
            val segIdx = scaledT.toInt().coerceIn(0, segmentCount - 1)
            val subT = (scaledT - segIdx).coerceIn(0f, 1f)
            val startPt = routePoints[segIdx]
            val endPt = routePoints[segIdx + 1]

            val busX = startPt.x + (endPt.x - startPt.x) * subT
            val busY = startPt.y + (endPt.y - startPt.y) * subT
            val busPos = Offset(busX, busY)

            // 4. Radar Sweep Sector radiating from Bus position
            rotate(degrees = sweepAngle, pivot = busPos) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            RadarCyan.copy(alpha = 0.03f),
                            RadarCyan.copy(alpha = 0.35f)
                        ),
                        center = busPos
                    ),
                    startAngle = 0f,
                    sweepAngle = 45f,
                    useCenter = true,
                    topLeft = Offset(busPos.x - 140f, busPos.y - 140f),
                    size = androidx.compose.ui.geometry.Size(280f, 280f)
                )

                // Sweep leading edge beam
                val beamLength = 140f
                val rad = 45f * (PI / 180f).toFloat()
                drawLine(
                    color = RadarCyan,
                    start = busPos,
                    end = Offset(busPos.x + beamLength * cos(rad), busPos.y + beamLength * sin(rad)),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )
            }

            // Pulsing GPS ripple around bus
            drawCircle(
                color = LiveGreen.copy(alpha = pulseAlpha),
                radius = 35f * pulseScale,
                center = busPos,
                style = Stroke(width = 2.5f)
            )
            drawCircle(
                color = RadarCyan.copy(alpha = (pulseAlpha * 0.6f)),
                radius = 50f * pulseScale,
                center = busPos,
                style = Stroke(width = 1.5f)
            )

            // 5. Draw Waypoints / Stops
            routePoints.forEachIndexed { index, pt ->
                val isOrigin = index == 0
                val isDestination = index == routePoints.size - 1
                val isPassed = (index.toFloat() / segmentCount) <= bus.progress

                val pinColor = when {
                    isDestination -> KsrtcAmber
                    isOrigin -> LiveGreen
                    isPassed -> LiveGreen.copy(alpha = 0.6f)
                    else -> RadarCyan
                }

                // Stop node glow
                drawCircle(
                    color = pinColor.copy(alpha = 0.3f),
                    radius = if (isOrigin || isDestination) 12f else 8f,
                    center = pt
                )
                // Stop node center
                drawCircle(
                    color = pinColor,
                    radius = if (isOrigin || isDestination) 6f else 4f,
                    center = pt
                )
            }

            // 6. Bus Vehicle Marker
            // Outer glow
            drawCircle(
                color = KsrtcRedPrimary.copy(alpha = 0.5f),
                radius = 22f,
                center = busPos
            )
            // Bus background pill
            drawCircle(
                color = KsrtcRedPrimary,
                radius = 14f,
                center = busPos
            )
            // Inner gold core
            drawCircle(
                color = KsrtcGold,
                radius = 6f,
                center = busPos
            )
        }

        // Overlay 1: Top Status HUD (Bus Number & Live Speed)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceElevated.copy(alpha = 0.9f),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LivePulseDot(color = LiveGreen, size = 7.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = bus.busNumber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "RADAR TRACKING",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RadarCyan
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceElevated.copy(alpha = 0.9f),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Speed",
                        tint = KsrtcAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${bus.speedKmH} km/h",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Overlay 2: Floating Map Controls (Zoom & Recenter)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = DarkSurfaceElevated.copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .size(40.dp)
                    .clickable { zoomLevel = (zoomLevel + 0.2f).coerceAtMost(1.8f) }
                    .testTag("zoom_in_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = "Zoom In",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = DarkSurfaceElevated.copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .size(40.dp)
                    .clickable { zoomLevel = (zoomLevel - 0.2f).coerceAtLeast(0.8f) }
                    .testTag("zoom_out_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ZoomOut,
                        contentDescription = "Zoom Out",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = KsrtcRedPrimary,
                modifier = Modifier
                    .size(44.dp)
                    .clickable {
                        zoomLevel = 1f
                        onRecenterClick()
                    }
                    .testTag("recenter_bus_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Center on Bus",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Overlay 3: Bottom Left Next Stop Pill
        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp),
            shape = RoundedCornerShape(12.dp),
            color = DarkSurfaceElevated.copy(alpha = 0.92f),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = "Bus",
                    tint = KsrtcAmber,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "NEXT: ${bus.nextStopName}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${bus.distanceToNextKm} km • In ${bus.nextStopEtaMinutes} mins",
                        fontSize = 10.sp,
                        color = LiveGreen
                    )
                }
            }
        }
    }
}
