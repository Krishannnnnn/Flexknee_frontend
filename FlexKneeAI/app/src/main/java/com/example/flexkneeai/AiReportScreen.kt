package com.example.flexkneeai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiReportScreen(
    patient: PatientDetails,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AI Analysis Report",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8FAFC)
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Report Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Blue500)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = patient.name,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Report Date: Jan 2, 2026",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(12.dp).size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Performance Summary Section
            Text(
                text = "Performance Summary",
                fontWeight = FontWeight.Bold,
                color = Slate900,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ReportMetricItem(Modifier.weight(1f), "ROM Improvement", "+15%", Icons.Default.TrendingUp, Green500)
                ReportMetricItem(Modifier.weight(1f), "Plan Adherence", "${patient.adherence}%", Icons.Default.AssignmentTurnedIn, Blue500)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AI Insights Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "AI Key Insights",
                            fontWeight = FontWeight.Bold,
                            color = Slate900,
                            fontSize = 16.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    InsightBullet("Significant progress in active knee flexion, reaching ${patient.progress + 15}° average.")
                    InsightBullet("Patient exhibits slight compensation pattern in right hip during SLS exercises.")
                    InsightBullet("Pain levels remain stable at ${patient.avgPain}/10, indicating good load tolerance.")
                    InsightBullet("Consistency in morning sessions is 2x better than evening sessions.")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Suggested Actions Section
            Text(
                text = "Suggested Actions",
                fontWeight = FontWeight.Bold,
                color = Slate900,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            ActionCard(
                title = "Increase Volume",
                desc = "Increase Heel Slides to 3 sets of 15 reps based on progress.",
                icon = Icons.Default.AddCircleOutline
            )
            Spacer(modifier = Modifier.height(12.dp))
            ActionCard(
                title = "Review Gait Pattern",
                desc = "Focus on terminal knee extension during next clinical visit.",
                icon = Icons.Default.DirectionsWalk
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Footer Buttons
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue500)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Download Full Report", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Blue500)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = Blue500, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Share with Patient", color = Blue500, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun ReportMetricItem(modifier: Modifier, label: String, value: String, icon: ImageVector, color: Color) {
    Surface(
        modifier = modifier.height(100.dp),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontWeight = FontWeight.ExtraBold, color = Slate900, fontSize = 20.sp)
            Text(text = label, color = Slate500, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun InsightBullet(text: String) {
    Row(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = "•", color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, color = Slate700, fontSize = 14.sp, lineHeight = 20.sp)
    }
}

@Composable
fun ActionCard(title: String, desc: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = Blue50
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Blue500, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, color = Slate900, fontSize = 15.sp)
                Text(text = desc, color = Slate500, fontSize = 13.sp)
            }
        }
    }
}
