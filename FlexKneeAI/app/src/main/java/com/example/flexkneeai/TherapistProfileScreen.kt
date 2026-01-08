package com.example.flexkneeai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flexkneeai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TherapistProfileScreen(
    onBackClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Profile",
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        fontSize = 20.sp
                    )
                },
                /* navigationIcon removed for tabbed layout */
                actions = {
                    IconButton(
                        onClick = { /* Already on Profile */ },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = Slate100
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "Profile",
                                tint = Slate500,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8FAFC) // Slate 50
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Profile Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = Blue500
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "SC",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(
                        text = "Dr. Sarah Chen",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "Knee Replacement Specialist",
                        fontSize = 14.sp,
                        color = Slate500
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Profile Card
            Text(
                text = "Profile",
                fontWeight = FontWeight.Bold,
                color = Slate900,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    TherapistInfoField(label = "Email", value = "sarah.chen@hospital.com")
                    Spacer(modifier = Modifier.height(20.dp))
                    TherapistInfoField(label = "Phone", value = "+1 (555) 000-0000")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Preferences Card
            Text(
                text = "Preferences",
                fontWeight = FontWeight.Bold,
                color = Slate900,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
            ) {
                val notificationsEnabled = remember { mutableStateOf(true) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = Slate400
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Push Notifications",
                            color = Slate900,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Switch(
                        checked = notificationsEnabled.value,
                        onCheckedChange = { notificationsEnabled.value = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Blue500,
                            uncheckedThumbColor = Slate200,
                            uncheckedTrackColor = Slate100
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Out Button
            Button(
                onClick = onSignOutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = Color(0xFFEF4444)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign Out",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TherapistInfoField(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 13.sp, color = Slate900, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate50, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(text = value, fontSize = 16.sp, color = Slate900)
        }
    }
}
