package com.example.flexkneeai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flexkneeai.ui.theme.*

data class ExerciseDetail(
    val title: String,
    val difficulty: String,
    val duration: String,
    val targetReps: String,
    val sets: String,
    val instructions: List<String>,
    val diffColor: Color,
    val diffBg: Color,
    val id: String = "0"
)

val exerciseDetailsList = mapOf(
    "Heel Slides" to ExerciseDetail(
        title = "Heel Slides",
        difficulty = "Easy",
        duration = "2 mins",
        targetReps = "10 reps",
        sets = "3 Sets",
        instructions = listOf(
            "Lie on your back with legs straight",
            "Slowly slide your heel towards your buttocks",
            "Hold for 5 seconds",
            "Slide back down slowly"
        ),
        diffColor = Green500,
        diffBg = Green50
    ),
    "Quad Sets" to ExerciseDetail(
        title = "Quad Sets",
        difficulty = "Medium",
        duration = "3 mins",
        targetReps = "15 reps",
        sets = "3 Sets",
        instructions = listOf(
            "Sit or lie on your back with legs straight",
            "Tighten your thigh muscle (quadriceps) by pushing the back of your knee down",
            "Hold for 5-10 seconds",
            "Relax and repeat"
        ),
        diffColor = Amber500,
        diffBg = Cream50
    ),
    "Straight Leg Raises" to ExerciseDetail(
        title = "Straight Leg Raises",
        difficulty = "Hard",
        duration = "4 mins",
        targetReps = "12 reps",
        sets = "3 Sets",
        instructions = listOf(
            "Lie on your back with one knee bent and the other straight",
            "Tighten the thigh muscle of the straight leg and lift it about 12 inches",
            "Hold for 3 seconds",
            "Lower the leg slowly to the floor"
        ),
        diffColor = Color(0xFFEF4444),
        diffBg = Color(0xFFFEF2F2)
    ),
    "Seated Knee Extension" to ExerciseDetail(
        title = "Seated Knee Extension",
        difficulty = "Easy",
        duration = "2 mins",
        targetReps = "15 reps",
        sets = "2 Sets",
        instructions = listOf(
            "Sit comfortably on a chair",
            "Slowly straighten your knee",
            "Hold for 3 seconds",
            "Lower slowly"
        ),
        diffColor = Green500,
        diffBg = Green50
    ),
    "Standing Hamstring Curls" to ExerciseDetail(
        title = "Standing Hamstring Curls",
        difficulty = "Easy",
        duration = "2 mins",
        targetReps = "12 reps",
        sets = "2 Sets",
        instructions = listOf(
            "Stand holding a support",
            "Bend your knee",
            "Bring heel toward buttocks",
            "Lower slowly"
        ),
        diffColor = Green500,
        diffBg = Green50
    ),
    "Sit to Stand" to ExerciseDetail(
        title = "Sit to Stand",
        difficulty = "Medium",
        duration = "2 mins",
        targetReps = "10 reps",
        sets = "3 Sets",
        instructions = listOf(
            "Sit on a chair",
            "Push through your legs to stand",
            "Keep knees aligned",
            "Sit back down slowly"
        ),
        diffColor = Amber500,
        diffBg = Cream50
    ),
    "Mini Squats" to ExerciseDetail(
        title = "Mini Squats",
        difficulty = "Medium",
        duration = "2 mins",
        targetReps = "12 reps",
        sets = "2 Sets",
        instructions = listOf(
            "Stand with feet shoulder-width apart",
            "Bend knees slightly",
            "Keep your back straight",
            "Return to standing"
        ),
        diffColor = Amber500,
        diffBg = Cream50
    ),
    "Step-Ups" to ExerciseDetail(
        title = "Step-Ups",
        difficulty = "Medium",
        duration = "2 mins",
        targetReps = "10 reps",
        sets = "2 Sets",
        instructions = listOf(
            "Step onto a low platform",
            "Push through your leg",
            "Straighten your knee",
            "Step down slowly"
        ),
        diffColor = Amber500,
        diffBg = Cream50
    ),
    "Heel-to-Toe Walking" to ExerciseDetail(
        title = "Heel-to-Toe Walking",
        difficulty = "Medium",
        duration = "2 mins",
        targetReps = "15 steps",
        sets = "2 Rounds",
        instructions = listOf(
            "Walk in a straight line",
            "Place heel first",
            "Roll onto your toes",
            "Maintain balance"
        ),
        diffColor = Amber500,
        diffBg = Cream50
    ),
    "Single Leg Stand" to ExerciseDetail(
        title = "Single Leg Stand",
        difficulty = "Medium",
        duration = "2 mins",
        targetReps = "30 seconds",
        sets = "3 Sets",
        instructions = listOf(
            "Stand on one leg",
            "Keep knee slightly bent",
            "Maintain balance",
            "Switch legs"
        ),
        diffColor = Amber500,
        diffBg = Cream50
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exercise: ExerciseDetail,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    onStartCamera: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        exercise.title,
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
        containerColor = Color.White,
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                color = Color.Transparent
            ) {
                Button(
                    onClick = onStartCamera,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue500)
                ) {
                    Text(
                        "Start with AI Camera",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Video Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                // Video Placeholder with Play Button
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp).padding(16.dp)
                    )
                }

                // Badges Overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = exercise.difficulty,
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications, // Using as placeholder for clock
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = exercise.duration,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Target", color = Slate500, fontSize = 13.sp)
                        Text(exercise.targetReps, fontWeight = FontWeight.Bold, color = Slate900, fontSize = 18.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Sets", color = Slate500, fontSize = 13.sp)
                        Text(exercise.sets, fontWeight = FontWeight.Bold, color = Slate900, fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Instructions
                Text(
                    text = "Instructions",
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                exercise.instructions.forEachIndexed { index, instruction ->
                    InstructionRow(number = index + 1, text = instruction)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun InstructionRow(number: Int, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Surface(
            modifier = Modifier.size(28.dp),
            shape = CircleShape,
            color = Blue50
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number.toString(),
                    color = Blue500,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = Slate700,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )
    }
}
