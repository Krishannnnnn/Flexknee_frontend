package com.example.flexkneeai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flexkneeai.ui.theme.*

data class TherapistAlert(
    val id: String,
    val priority: String, // "high", "medium"
    val time: String,
    val message: String,
    val patientName: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    patients: List<PatientDetails>,
    onBackClick: () -> Unit,
    onViewPatient: (String) -> Unit,
    onProfileClick: () -> Unit = {}
) {
    val alerts = remember(patients) {
        patients.filter { it.swellingLevel == "High" && it.latestPain > 7 }
            .map { patient ->
                TherapistAlert(
                    id = patient.name,
                    priority = "high",
                    time = "Just Now",
                    message = "Critical: Swelling High & Pain ${patient.latestPain}/10",
                    patientName = patient.name
                )
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Alerts",
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        fontSize = 20.sp
                    )
                },
                /* navigationIcon removed for tabbed layout */
                actions = {
                    Row(modifier = Modifier.padding(end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { /* TODO */ }) {
                            Box {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Slate500,
                                    modifier = Modifier.size(28.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color.Red, CircleShape)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                        IconButton(onClick = onProfileClick) {
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
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFFBFDFF) // Very light blue/white
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            alerts.forEach { alert ->
                AlertCard(
                    alert = alert,
                    onViewPatient = { onViewPatient(alert.patientName) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AlertCard(
    alert: TherapistAlert,
    onViewPatient: () -> Unit
) {
    val priorityColor = if (alert.priority == "high") Color(0xFFEF4444) else Color(0xFFF59E0B)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header: Priority and Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(24.dp),
                        shape = CircleShape,
                        color = priorityColor.copy(alpha = 0.1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PriorityHigh,
                            contentDescription = null,
                            tint = priorityColor,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = alert.priority,
                        color = priorityColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Priority",
                        color = Slate400,
                        fontSize = 12.sp
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications, // Using notifications as time icon per design mockup look
                        contentDescription = null,
                        tint = Slate300,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = alert.time,
                        color = Slate400,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Alert Title
            Text(
                text = alert.message,
                fontWeight = FontWeight.Bold,
                color = Slate900,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Patient Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Patient:",
                    color = Slate400,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = alert.patientName,
                    color = Slate600,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // View Patient Button
            OutlinedButton(
                onClick = onViewPatient,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate600)
            ) {
                Text(
                    "View Patient",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
