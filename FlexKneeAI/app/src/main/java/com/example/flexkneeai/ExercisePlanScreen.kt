package com.example.flexkneeai

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flexkneeai.ui.theme.*

// No ViewModel imports needed here

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisePlanScreen(
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    onStartExercise: (String) -> Unit = {}
) {
    // Local data class for mock data
    data class MockExercise(
        val name: String,
        val difficulty: String,
        val durationSeconds: Int,
        val targetReps: Int
    )

    // Sample Data
    val exercises = listOf(
        MockExercise("Heel Slides", "Easy", 120, 10),
        MockExercise("Quad Sets", "Medium", 180, 15),
        MockExercise("Straight Leg Raises", "Hard", 240, 12)
    )
    val rehabPlan = object {
        val description = "Focus on full extension today. Keep your movements slow and controlled. You're doing great!"
    }
    val isLoading = false

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Today's Plan",
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
                        IconButton(onClick = { /* TODO */ }) {
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
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        if (isLoading) {
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Blue500)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))
    
                // Doctor Note Card
                // Use rehab plan notes if available, else static
                val doctorNotes = rehabPlan?.description ?: "Focus on full extension today. Keep your movements slow and controlled. You're doing great!"
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                color = Blue50
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = Blue500,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Dr. Sarah Chen",
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Physiotherapist",
                                    color = Blue500,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Blue100)
                        ) {
                            Text(
                                text = "\"$doctorNotes\"",
                                modifier = Modifier.padding(16.dp),
                                color = Slate700,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
    
                Spacer(modifier = Modifier.height(32.dp))
    
                // Session Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Morning Session",
                            fontWeight = FontWeight.Bold,
                            color = Slate900,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "${exercises.size} exercises • 15 mins",
                            color = Slate500,
                            fontSize = 14.sp
                        )
                    }
                    Button(
                        onClick = { onStartExercise("All") },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue500),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text("Start All", fontWeight = FontWeight.Bold)
                    }
                }
    
                Spacer(modifier = Modifier.height(24.dp))
    
                // Exercise Cards
                if (exercises.isEmpty()) {
                    Text("No exercises assigned yet.", color = Slate500)
                } else {
                    exercises.forEach { exercise ->
                        val diffColor = when(exercise.difficulty) {
                           "Easy" -> Green500
                           "Medium" -> Amber500
                           "Hard" -> Color(0xFFEF4444)
                           else -> Blue500
                        }
                        val diffBg = when(exercise.difficulty) {
                           "Easy" -> Green50
                           "Medium" -> Cream50
                           "Hard" -> Color(0xFFFEF2F2)
                           else -> Blue50
                        }
                        
                        ExerciseItemCard(
                            title = exercise.name,
                            difficulty = exercise.difficulty?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() } ?: "Medium",
                            diffColor = diffColor,
                            diffBg = diffBg,
                            duration = "${exercise.durationSeconds ?: 60} s",
                            reps = "${exercise.targetReps ?: 10} reps",
                            onStart = { onStartExercise(exercise.name) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ExerciseItemCard(
    title: String,
    difficulty: String,
    diffColor: Color,
    diffBg: Color,
    duration: String,
    reps: String,
    onStart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder for exercise image
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Slate100),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Slate300, modifier = Modifier.size(32.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        color = diffBg,
                        shape = CircleShape
                    ) {
                        Text(
                            text = difficulty,
                            color = diffColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Slate400, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = duration, color = Slate500, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Default.Poll, contentDescription = null, tint = Slate400, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = reps, color = Slate500, fontSize = 13.sp)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                TextButton(
                    onClick = onStart,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Blue500, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Start Exercise", color = Blue500, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
