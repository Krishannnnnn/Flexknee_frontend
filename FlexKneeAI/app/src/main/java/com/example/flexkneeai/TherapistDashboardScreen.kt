package com.example.flexkneeai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flexkneeai.ui.theme.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun TherapistDashboardScreen(
    patients: List<PatientDetails> = emptyList(), // Use actual PatientDetails list
    onProfileClick: () -> Unit = {},
    onAlertsClick: () -> Unit = {},
    onLogClick: () -> Unit = {},
    onPatientClick: (String) -> Unit = {},
    onViewAllClick: () -> Unit = {},
    onNeedReviewClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {}
) {
    // Sample Data
    val therapistName = "Chen"
    val activePatients = "12"
    val patientsImpacted = "150"
    val successRate = "94%"
    val isLoading = false
    
    // Sample Patients (Keep for Home tab legacy if needed, or use actual list)
    val displayPatients = remember(patients) {
        patients.map { Patient(it.name, it.name, it.latestPain, it.swellingLevel) }
    }

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            TherapistBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (selectedTab) {
                0 -> TherapistHomeContent(
                    therapistName = therapistName,
                    activePatients = activePatients,
                    patientsImpacted = patientsImpacted,
                    successRate = successRate,
                    patients = displayPatients,
                    onProfileClick = { selectedTab = 3 },
                    onPatientClick = onPatientClick,
                    onViewAllClick = onViewAllClick,
                    onNeedReviewClick = { selectedTab = 2 }
                )
                1 -> PatientLogsScreen(onBackClick = { selectedTab = 0 })
                2 -> AlertsScreen(
                    patients = patients, // Pass actual list
                    onBackClick = { selectedTab = 0 },
                    onViewPatient = onPatientClick
                )
                3 -> TherapistProfileScreen(
                    onBackClick = { selectedTab = 0 },
                    onSignOutClick = onSignOutClick
                )
            }
        }
    }
}

@Composable
fun TherapistHomeContent(
    therapistName: String,
    activePatients: String,
    patientsImpacted: String,
    successRate: String,
    patients: List<Patient>,
    onProfileClick: () -> Unit,
    onPatientClick: (String) -> Unit,
    onViewAllClick: () -> Unit,
    onNeedReviewClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Welcome back,",
                    color = Slate500,
                    fontSize = 16.sp
                )
                Text(
                    text = "Dr. $therapistName",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
            }
            IconButton(onClick = onProfileClick) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = therapistName.take(1),
                            fontWeight = FontWeight.Bold,
                            color = Blue500
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Stats Grid
        Row(modifier = Modifier.fillMaxWidth()) {
            TherapistStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.People,
                title = "Active Patients",
                value = activePatients,
                valueColor = Blue500,
                valueBg = Blue50
            )
            Spacer(modifier = Modifier.width(16.dp))
            TherapistStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Warning,
                title = "Need Review",
                value = "3",
                subValue = "High Priority",
                valueColor = Color(0xFFEF4444),
                valueBg = Color(0xFFFEF2F2),
                isUrgent = true,
                onClick = onNeedReviewClick
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            TherapistStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.TrendingUp,
                title = "Success Rate",
                value = successRate,
                valueColor = Green500,
                valueBg = Green50
            )
            Spacer(modifier = Modifier.width(16.dp))
            TherapistStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.CheckCircle,
                title = "Impact",
                value = patientsImpacted,
                subValue = "Patients",
                valueColor = Purple500,
                valueBg = Purple50
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Search Bar
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search patients...", color = Slate400) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Slate400) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Slate200,
                unfocusedIndicatorColor = Slate200
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Patient List Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Patients",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            TextButton(onClick = onViewAllClick) {
                Text("View all", color = Blue500)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Patient List
        patients.take(3).forEach { patient ->
            val pain = patient.lastPain ?: 0
            val swelling = patient.swelling?.lowercase() ?: "none"
            
            val (computedStatus, statusColor, statusBg) = when {
                pain >= 7 || swelling == "high" -> Triple("Critical", Color(0xFFEF4444), Color(0xFFFEF2F2))
                pain >= 4 || swelling == "medium" -> Triple("Needs Attention", Amber500, Cream50)
                else -> Triple("Improving", Green500, Green50)
            }
            
            PatientCard(
                name = patient.name,
                status = computedStatus,
                statusColor = statusColor,
                statusBg = statusBg,
                onPatientClick = { onPatientClick(patient.id.toString()) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun TherapistStatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    subValue: String? = null,
    valueColor: Color,
    valueBg: Color,
    isUrgent: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(icon, contentDescription = null, tint = valueColor, modifier = Modifier.size(24.dp))
                if (subValue != null) {
                    Surface(
                        color = valueBg,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = subValue,
                            color = valueColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Surface(
                        color = valueColor,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = value,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            Column {
                if (isUrgent) {
                    Text(
                        text = value,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }
                Text(
                    text = title,
                    color = Slate500,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun PatientCard(
    name: String,
    status: String,
    statusColor: Color,
    statusBg: Color,
    onPatientClick: (String) -> Unit
) {
    Card(
        onClick = { onPatientClick(name) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Slate100
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = name.first().toString(),
                        fontWeight = FontWeight.Bold,
                        color = Slate500
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = name,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Surface(
                color = statusBg,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.2f))
            ) {
                Text(
                    text = status,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Slate300
            )
        }
    }
}

data class Patient(
    val id: String,
    val name: String,
    val lastPain: Int?,
    val swelling: String?
)

@Composable
fun TherapistBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Default.Groups, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Blue500,
                selectedTextColor = Blue500,
                indicatorColor = Blue50
            )
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = { Icon(Icons.Default.Assignment, contentDescription = "Patient Logs") },
            label = { Text("Logs") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Blue500,
                selectedTextColor = Blue500,
                indicatorColor = Blue50
            )
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = { Icon(Icons.Default.NotificationImportant, contentDescription = "Alerts") },
            label = { Text("Alerts") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Blue500,
                selectedTextColor = Blue500,
                indicatorColor = Blue50
            )
        )
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Blue500,
                selectedTextColor = Blue500,
                indicatorColor = Blue50
            )
        )
    }
}
