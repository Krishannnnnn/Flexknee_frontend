package com.example.flexkneeai

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flexkneeai.ui.theme.*

data class PatientDetails(
    val name: String,
    val condition: String,
    val initial: String,
    val progress: Int,
    val sessions: Int,
    val adherence: Int,
    val avgPain: Float,
    val status: String = "On Track",
    val latestPain: Int = 0,
    val swellingLevel: String = "Low",
    val romHistory: List<Int> = emptyList(),
    val targetRom: Int = 120
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailsScreen(
    patient: PatientDetails,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    onViewAiReportClick: () -> Unit = {},
    onModifyPlanClick: () -> Unit = {},
    onAlertsClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Patient Profile",
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
                actions = {
                    Row(modifier = Modifier.padding(end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onAlertsClick) {
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

            // Patient Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    color = Blue100
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = patient.initial,
                            color = Blue500,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(
                        text = patient.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = patient.condition,
                        fontSize = 16.sp,
                        color = Slate500
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onMessageClick,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(Icons.Default.Message, contentDescription = null, tint = Slate500, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Message", color = Slate700, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(Icons.Default.EventNote, contentDescription = null, tint = Slate500, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Schedule", color = Slate700, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recovery Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recovery Status",
                            fontWeight = FontWeight.Bold,
                            color = Slate900,
                            fontSize = 18.sp
                        )
                        Surface(
                            color = Green100,
                            shape = CircleShape
                        ) {
                            Text(
                                text = patient.status,
                                color = Green500,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    val currentRom = patient.romHistory.lastOrNull() ?: 0
                    val currentProgress = (currentRom.toFloat() / patient.targetRom * 100).toInt().coerceAtMost(100)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Overall Progress", color = Slate600, fontSize = 14.sp)
                        Text("$currentProgress %", color = Slate900, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = currentProgress / 100f,
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Blue500,
                        trackColor = Slate100,
                        strokeCap = StrokeCap.Round
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        MetricItem(Modifier.weight(1f), patient.sessions.toString(), "Sessions")
                        MetricItem(Modifier.weight(1f), "${patient.adherence}%", "Adherence")
                        MetricItem(Modifier.weight(1f), patient.avgPain.toString(), "Avg Pain")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ROM Trend Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Range of Motion Trend",
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        fontSize = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Dynamic ROM Line Chart
                    Box(modifier = Modifier.fillMaxWidth().height(180.dp).padding(end = 8.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height
                            val maxAngle = 120f // Y-axis max
                            val labelWidth = 32.dp.toPx()
                            
                            val chartWidth = canvasWidth - labelWidth
                            val chartHeight = canvasHeight - 16.dp.toPx()
                            
                            // Draw Horizontal Grid Lines
                            val yLevels = listOf(0, 30, 60, 90, 120)
                            yLevels.forEach { level ->
                                val y = chartHeight - (level / maxAngle * chartHeight)
                                drawLine(
                                    color = Slate100,
                                    start = androidx.compose.ui.geometry.Offset(labelWidth, y),
                                    end = androidx.compose.ui.geometry.Offset(canvasWidth, y),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                            
                            // Draw Data Path
                            if (patient.romHistory.isNotEmpty()) {
                                val points = patient.romHistory.mapIndexed { index, angle ->
                                    val x = labelWidth + (index.toFloat() / (patient.romHistory.size - 1).coerceAtLeast(1)) * chartWidth
                                    val y = chartHeight - (angle.toFloat() / maxAngle * chartHeight)
                                    androidx.compose.ui.geometry.Offset(x, y)
                                }
                                
                                val path = Path().apply {
                                    points.forEachIndexed { index, offset ->
                                        if (index == 0) moveTo(offset.x, offset.y)
                                        else lineTo(offset.x, offset.y)
                                    }
                                }
                                
                                drawPath(
                                    path = path,
                                    color = Blue500,
                                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                                )
                                
                                // Draw points
                                points.forEach { offset ->
                                    drawCircle(
                                        color = Blue500,
                                        radius = 4.dp.toPx(),
                                        center = offset
                                    )
                                    drawCircle(
                                        color = Color.White,
                                        radius = 2.dp.toPx(),
                                        center = offset
                                    )
                                }
                            }
                        }
                        
                        // Overlay Y-Axis Labels (Degrees)
                        Column(
                            modifier = Modifier.fillMaxHeight().width(32.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.End
                        ) {
                            listOf("120°", "90°", "60°", "30°", "0°").forEach { 
                                Text(it, color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 32.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                            Text(text = day, color = Slate400, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Actions
            ActionItem(
                icon = Icons.Default.ShowChart,
                title = "View AI Analysis Report",
                iconBg = Color(0xFFF5F3FF),
                iconColor = Color(0xFF8B5CF6),
                onClick = onViewAiReportClick
            )
            Spacer(modifier = Modifier.height(12.dp))
            ActionItem(Icons.Default.EventNote, "Modify Rehab Plan", Color(0xFFEFF6FF), Color(0xFF3B82F6), onClick = onModifyPlanClick)
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun MetricItem(modifier: Modifier, value: String, label: String) {
    Surface(
        modifier = modifier.height(64.dp).padding(horizontal = 4.dp),
        color = Slate50,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = value, fontWeight = FontWeight.Bold, color = Slate900, fontSize = 16.sp)
            Text(text = label, color = Slate500, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    iconBg: Color,
    iconColor: Color,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = iconBg
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.SemiBold,
                color = Slate900,
                fontSize = 15.sp
            )
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate300)
        }
    }
}
