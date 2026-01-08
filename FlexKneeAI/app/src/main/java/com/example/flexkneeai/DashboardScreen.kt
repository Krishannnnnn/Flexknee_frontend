package com.example.flexkneeai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flexkneeai.ui.theme.*

@Composable
fun DashboardScreen(
    userName: String = "John",
    // Data for Home
    romValue: String = "115°",
    painValue: String = "3/10",
    
    // Data for Progress
    currentRom: Int = 115,
    painAvg: Float = 3.0f,
    
    // Data for Log
    currentPain: Int = 3,
    currentSwelling: String = "Low",
    
    // Performance History
    performanceHistory: List<PerformanceRecord> = emptyList(),
    
    // New parameters to fix compilation error
    exerciseCount: String = "0/3",
    recoveryDay: Int = 1,
    
    onSaveLog: (Int, String) -> Unit = { _, _ -> },
    
    // Actions
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onStartExercises: () -> Unit,
    onWeeklyAssessment: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        bottomBar = { 
            DashboardBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            ) 
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> DashboardHomeContent(
                    userName = userName,
                    romValue = romValue,
                    painValue = painValue,
                    exerciseCount = exerciseCount,
                    recoveryDay = recoveryDay,
                    onNotificationsClick = onNotificationsClick,
                    onProfileClick = onProfileClick,
                    onStartExercises = onStartExercises,
                    onWeeklyAssessment = onWeeklyAssessment,
                    onProgressClick = { selectedTab = 1 } 
                )
                1 -> MyProgressScreen(
                    currentRom = currentRom,
                    painAvg = painAvg,
                    history = performanceHistory
                )
                2 -> DailyCheckInScreen(
                    currentPain = currentPain,
                    currentSwelling = currentSwelling,
                    onSaveLog = { pain, swelling ->
                        onSaveLog(pain, swelling)
                        selectedTab = 0 // Go back to home after logging? Or stay? Let's go home for better flow.
                    }
                )
                3 -> ChatScreen(
                    chatPartner = "Dr. Chen", // Default for patient dash
                    isTherapist = false
                )
            }
        }
    }
}

@Composable
fun DashboardHomeContent(
    userName: String,
    romValue: String,
    painValue: String,
    exerciseCount: String,
    recoveryDay: Int,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onStartExercises: () -> Unit,
    onWeeklyAssessment: () -> Unit,
    onProgressClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = Blue500
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("FK", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNotificationsClick) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = Slate500,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
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
        }

        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Wednesday, Dec 31",
            color = Slate500,
            fontSize = 14.sp
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Hello, $userName 👋",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Slate900
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Today's Session Card
        SessionCard(
            exerciseCount = exerciseCount,
            recoveryDay = recoveryDay,
            onStartClick = onStartExercises
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Two-column row for stats
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Range of Motion",
                value = romValue,
                improvement = "5%",
                isPositive = true
            )
            Spacer(modifier = Modifier.width(16.dp))
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Pain Level",
                value = painValue,
                improvement = "10%",
                isPositive = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Weekly Assessment Card
        Card(
            onClick = onWeeklyAssessment,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Slate50)
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF5F3FF) // Soft purple
                ) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        tint = Color(0xFF8B5CF6),
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Weekly Assessment",
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Measure your ROM & Gait",
                        color = Slate500,
                        fontSize = 14.sp
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Slate500
                )
            }
        }
    }
}

@Composable
fun SessionCard(
    exerciseCount: String,
    recoveryDay: Int,
    onStartClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF4A90E2), Color(0xFF357ABD))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Today's Session",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Day $recoveryDay of Recovery",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Progress", color = Color.White, fontSize = 14.sp)
                    Text("$exerciseCount Exercises", color = Color.White, fontSize = 14.sp)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = 0.3f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onStartClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "Start Exercises",
                        color = Blue500,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    improvement: String,
    isPositive: Boolean
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Icon(
                    imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = if (isPositive) Green500 else Color.Red,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$improvement",
                    color = if (isPositive) Green500 else Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (title == "Pain Level") "improvement" else "this week",
                    color = Slate500,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun DashboardBottomNav(
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
            icon = { Icon(Icons.Default.Timeline, contentDescription = "Home") },
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
            icon = { Icon(Icons.Default.Analytics, contentDescription = "Progress") },
            label = { Text("Progress") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Blue500,
                selectedTextColor = Blue500,
                indicatorColor = Blue50
            )
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Log") },
            label = { Text("Log") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Blue500,
                selectedTextColor = Blue500,
                indicatorColor = Blue50
            )
        )
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = { Icon(Icons.Default.Message, contentDescription = "Messages") },
            label = { Text("Messages") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Blue500,
                selectedTextColor = Blue500,
                indicatorColor = Blue50
            )
        )
    }
}
