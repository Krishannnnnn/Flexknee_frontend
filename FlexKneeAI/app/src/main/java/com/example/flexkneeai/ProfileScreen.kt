package com.example.flexkneeai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flexkneeai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onViewProgressClick: () -> Unit,
    onEditInformationClick: () -> Unit
) {
    // Sample Data
    val userProfileName = "John Doe"
    val userProfileEmail = "john.doe@example.com"
    val userRole = "patient"
    val isLoading = false
    val userProfile = object {
        val user = object {
            val name = userProfileName
            val email = userProfileEmail
            val role = userRole
            val phone: String? = "+1 234 567 8900"
        }
    }

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
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Slate900
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8FAFC) // Slate 50
    ) { paddingValues ->
        if (isLoading && userProfile == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Blue500)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Profile Header
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Blue500, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userProfile?.user?.name?.take(2)?.uppercase() ?: "U",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = userProfile?.user?.name ?: "User",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Text(
                    text = "Role: ${userProfile?.user?.role?.capitalize() ?: "Patient"}",
                    fontSize = 14.sp,
                    color = Slate500
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Personal Information Section
                SectionHeader("Personal Information")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        InfoRow(icon = Icons.Outlined.Person, label = "Full Name", value = userProfile?.user?.name ?: "--")
                        Spacer(modifier = Modifier.height(16.dp))
                        InfoRow(icon = Icons.Outlined.Email, label = "Email", value = userProfile?.user?.email ?: "--")
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Show phone if available in user details
                        val phone = userProfile?.user?.phone
                        if (phone != null) {
                            InfoRow(icon = Icons.Outlined.Phone, label = "Phone", value = phone)
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        OutlinedButton(
                            onClick = onEditInformationClick,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate500),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                        ) {
                            Text("Edit Information")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Settings Section
                SectionHeader("Settings")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        val notificationsEnabled = remember { mutableStateOf(true) }
                        SettingsToggleRow(
                            icon = Icons.Default.NotificationsNone,
                            label = "Notifications",
                            checked = notificationsEnabled.value,
                            onCheckedChange = { notificationsEnabled.value = it }
                        )
                        SettingsActionRow(
                            icon = Icons.Outlined.Lock, 
                            label = "Change Password",
                            onClick = onChangePasswordClick
                        )
                        SettingsActionRow(
                            icon = Icons.Outlined.Timeline, 
                            label = "View Progress Report",
                            onClick = onViewProgressClick
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign Out Button
                Button(
                    onClick = { 
                        onSignOutClick()
                    },
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
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        fontWeight = FontWeight.Bold,
        color = Slate900,
        fontSize = 16.sp
    )
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Slate50, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Slate400)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = Slate400)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
        }
    }
}

@Composable
fun SettingsToggleRow(icon: ImageVector, label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = Slate400)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = label, color = Slate900, fontWeight = FontWeight.Medium)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Blue500,
                uncheckedThumbColor = Slate200,
                uncheckedTrackColor = Slate100
            )
        )
    }
}

@Composable
fun SettingsActionRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = Slate400)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = label, color = Slate900, fontWeight = FontWeight.Medium)
        }
        Icon(imageVector = Icons.Outlined.ChevronRight, contentDescription = null, tint = Slate300)
    }
}
