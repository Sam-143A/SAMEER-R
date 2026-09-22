package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.LivePulseDot
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.KsrtcAmber
import com.example.ui.theme.KsrtcGold
import com.example.ui.theme.KsrtcRedDark
import com.example.ui.theme.KsrtcRedPrimary
import com.example.ui.theme.LiveGreen
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.TextSecondary

@Composable
fun LoginScreen(
    onLoginSuccess: (fullName: String, phoneOrEmail: String, passType: String, passNumber: String) -> Unit,
    onContinueAsGuest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUp by remember { mutableStateOf(false) }
    var emailOrPhone by remember { mutableStateOf("praveen.ksrtc@commuter.in") }
    var password by remember { mutableStateOf("KSRTC2026") }
    var fullName by remember { mutableStateOf("Praveen K. Nair") }
    var passType by remember { mutableStateOf("Regular Commuter") }
    var passNumber by remember { mutableStateOf("KSRTC-KL-88219") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Visual Card with Bus Image
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_ksrtc_hero),
                        contentDescription = "KSRTC Swift Luxury Express",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        DarkBg.copy(alpha = 0.85f),
                                        DarkBg
                                    )
                                )
                            )
                    )
                    // Header Floating Badge
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = KsrtcRedPrimary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBus,
                                    contentDescription = "Bus",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "KSRTC",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE TRACKER",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KsrtcGold
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LivePulseDot(size = 6.dp, color = LiveGreen)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GPS Fleet Radar • Swift • Airavat",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Auth Toggle Tabs: Sign In / Create Account
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { isSignUp = false }
                            .testTag("tab_signin"),
                        color = if (!isSignUp) KsrtcRedPrimary else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = "Sign In",
                                fontWeight = FontWeight.Bold,
                                color = if (!isSignUp) Color.White else TextSecondary
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { isSignUp = true }
                            .testTag("tab_signup"),
                        color = if (isSignUp) KsrtcRedPrimary else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = "New Commuter",
                                fontWeight = FontWeight.Bold,
                                color = if (isSignUp) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Demo Profiles Bar
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "QUICK DEMO PROFILES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = RadarCyan
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DemoProfileChip(
                        label = "Praveen (Daily)",
                        isSelected = fullName.startsWith("Praveen"),
                        onClick = {
                            fullName = "Praveen K. Nair"
                            emailOrPhone = "praveen.ksrtc@commuter.in"
                            password = "KSRTC2026"
                            passType = "Regular Commuter"
                            passNumber = "KSRTC-KL-88219"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    DemoProfileChip(
                        label = "Anjali (Student)",
                        isSelected = fullName.startsWith("Anjali"),
                        onClick = {
                            fullName = "Anjali S. Menon"
                            emailOrPhone = "anjali.menon@cet.ac.in"
                            password = "KSRTC2026"
                            passType = "Student Pass (Concession)"
                            passNumber = "KSRTC-STU-4501"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input Fields Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    if (isSignUp) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = KsrtcAmber)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_fullname"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = KsrtcRedPrimary,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = emailOrPhone,
                        onValueChange = { emailOrPhone = it },
                        label = { Text(if (isSignUp) "Email or Phone Number" else "Commuter ID / Phone / Email") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = KsrtcAmber)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_email_phone"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KsrtcRedPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = KsrtcAmber)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password",
                                    tint = TextSecondary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_password"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KsrtcRedPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (isSignUp) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = passNumber,
                            onValueChange = { passNumber = it },
                            label = { Text("Pass / Smart Card # (Optional)") },
                            leadingIcon = {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = KsrtcAmber)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_pass_num"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = KsrtcRedPrimary,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    if (errorMsg != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMsg ?: "",
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Sign In / Sign Up Button
                    Button(
                        onClick = {
                            if (emailOrPhone.isBlank() || password.isBlank()) {
                                errorMsg = "Please enter valid credentials"
                            } else {
                                errorMsg = null
                                onLoginSuccess(
                                    fullName.ifBlank { "KSRTC Commuter" },
                                    emailOrPhone,
                                    passType,
                                    passNumber.ifBlank { "KL-PASS-2026" }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_login_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = KsrtcRedPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = "Radar",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSignUp) "Register & Start Tracking" else "Sign In & Track Buses",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Continue as Guest Button
                    OutlinedButton(
                        onClick = onContinueAsGuest,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("guest_login_button"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Continue as Guest / Skip Login",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Kerala & Karnataka State Road Transport Corporations",
                fontSize = 11.sp,
                color = TextSecondary
            )
            Text(
                text = "Government of Kerala • Transit Safety & Telemetry Division",
                fontSize = 10.sp,
                color = TextSecondary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun DemoProfileChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("demo_profile_${label.take(6)}"),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) KsrtcRedPrimary.copy(alpha = 0.2f) else DarkSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) KsrtcRedPrimary else DarkBorder
        )
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) KsrtcGold else TextSecondary,
                maxLines = 1
            )
        }
    }
}
