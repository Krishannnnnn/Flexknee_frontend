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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flexkneeai.ui.theme.*

@Composable
fun MyProgressScreen(
    currentRom: Int,
    painAvg: Float,
    history: List<PerformanceRecord> = emptyList(),
    onBackClick: (() -> Unit)? = null
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
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Slate900
                        )
                    }
                }
                Text(
                    text = "My Progress",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ROM Graph Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Take last 7 dates or as many as available to align with labels
                    val recentHistory = history.takeLast(7)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Range of Motion",
                            fontWeight = FontWeight.Bold,
                            color = Slate900,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "+${if (history.isNotEmpty()) history.last().rom - (if(history.size > 1) history[history.size-2].rom else 0) else 0}° Total",
                            color = Green500,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Graph with Y-Axis Labels
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        // Y-Axis Labels
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(30.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            val labels = listOf("150°", "120°", "90°", "60°", "30°", "0°")
                            labels.forEach { label ->
                                Text(
                                    text = label,
                                    color = Slate400,
                                    fontSize = 10.sp,
                                    modifier = Modifier.height(20.dp), // Height to align with grid lines loosely
                                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Chart
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            LineChart(dataPoints = recentHistory.map { it.rom })
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        history.takeLast(7).forEach { record ->
                            Text(text = record.date, color = Slate400, fontSize = 10.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Two-column row for stats
            Row(modifier = Modifier.fillMaxWidth()) {
                ProgressStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Current ROM",
                    value = "${currentRom}°",
                    change = "5%",
                    isPositive = true
                )
                Spacer(modifier = Modifier.width(16.dp))
                ProgressStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Pain Avg",
                    value = String.format("%.1f", painAvg),
                    change = "15%",
                    isPositive = true,
                    isPain = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Gait Analysis History
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Gait Analysis History",
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        fontSize = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    history.takeLast(5).reversed().forEach { record ->
                        GaitHistoryRow(record.date, record.gait, record.color)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun LineChart(dataPoints: List<Int>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val maxVal = 150f 
        
        // Draw grid lines
        val gridLines = 5
        for (i in 0..gridLines) {
            val y = height - (i * height / gridLines)
            drawLine(
                color = Slate100,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx()
            )
        }
        
        if (dataPoints.isEmpty()) return@Canvas

        val path = Path()
        
        dataPoints.forEachIndexed { index, value ->
            // Distribute points evenly across width
            val x = if (dataPoints.size > 1) index * (width / (dataPoints.size - 1)) else width / 2
            // Normalize value to 0-1 based on maxVal (150)
            val y = height - ((value / maxVal) * height)
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        
        drawPath(
            path = path,
            color = Blue500,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        
        // Draw dots
        dataPoints.forEachIndexed { index, value ->
            val x = if (dataPoints.size > 1) index * (width / (dataPoints.size - 1)) else width / 2
            val y = height - ((value / maxVal) * height)
            
            drawCircle(
                color = Blue500,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun ProgressStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    change: String,
    isPositive: Boolean,
    isPain: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = Slate500, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                @Suppress("DEPRECATION")
                Icon(
                    imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = if (isPain) Color(0xFFEF4444) else Green500,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isPain) "↓ $change" else "↑ $change",
                    color = if (isPain) Color(0xFFEF4444) else Green500,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isPain) "decrease" else "this week",
                    color = Slate500,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun GaitHistoryRow(date: String, status: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = date, color = Slate500, fontSize = 16.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = status,
                color = Slate900,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                modifier = Modifier.width(100.dp)
            )
        }
    }
}
