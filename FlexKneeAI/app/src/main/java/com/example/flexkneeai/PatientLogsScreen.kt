package com.example.flexkneeai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flexkneeai.ui.theme.*

@Composable
fun PatientLogsScreen(
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC) // Slate 50
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                /* Back button removed for tabbed layout */
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Patient Logs",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Patient Logs List
            PatientLogCard(
                name = "James Wilson",
                avgPain = 5.5f,
                lastLogDate = "27 Dec",
                swelling = "medium",
                status = "Improving"
            )
            Spacer(modifier = Modifier.height(16.dp))
            PatientLogCard(
                name = "Maria Garcia",
                avgPain = 5.4f,
                lastLogDate = "26 Dec",
                swelling = "medium",
                status = "Monitor"
            )
            Spacer(modifier = Modifier.height(16.dp))
            PatientLogCard(
                name = "Sarah Miller",
                avgPain = 4.3f,
                lastLogDate = "25 Dec",
                swelling = "low",
                status = "Stable"
            )
            Spacer(modifier = Modifier.height(16.dp))
            PatientLogCard(
                name = "Robert Chen",
                avgPain = 3.2f,
                lastLogDate = "27 Dec",
                swelling = "low",
                status = "Improving"
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PatientLogCard(
    name: String,
    avgPain: Float,
    lastLogDate: String,
    swelling: String,
    status: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Slate100.copy(alpha = 0.5f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = when(name) {
                            "James Wilson" -> Green500
                            "Maria Garcia" -> Color(0xFFF59E0B)
                            "Sarah Miller" -> Blue500
                            "Robert Chen" -> Green500
                            else -> Slate400
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = name.first().toString(),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = name,
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        fontSize = 18.sp
                    )
                }
                
                StatusBadge(status)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem("Avg Pain", avgPain.toString())
                MetricItem("Last Log", lastLogDate)
                MetricItem("Swelling", swelling)
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (color, icon, bgColor) = when (status) {
        "Improving" -> Triple(Green500, Icons.AutoMirrored.Filled.TrendingUp, Green50)
        "Monitor" -> Triple(Color(0xFFF59E0B), Icons.Default.Warning, Cream50)
        "Stable" -> Triple(Blue500, Icons.Default.HorizontalRule, Blue50)
        "Emergency" -> Triple(Color(0xFFEF4444), Icons.Default.Error, Color(0xFFFEF2F2))
        else -> Triple(Slate500, Icons.Default.Help, Slate50)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = status,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MetricItem(label: String, value: String) {
    Column {
        Text(text = label, color = Slate400, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = Slate900, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
