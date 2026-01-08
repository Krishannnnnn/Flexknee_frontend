package com.example.flexkneeai

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flexkneeai.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun ExerciseSessionResultsScreen(
    exerciseName: String,
    reps: Int,
    maxRom: Int,
    gaitStatus: String,
    onLogPainClick: () -> Unit,
    onBackHomeClick: () -> Unit
) {
    val aiSuggestion = remember { generateAiSuggestion(exerciseName, reps, maxRom, gaitStatus) }
    var showConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showConfetti = true
    }

    Box(modifier = Modifier.fillMaxSize().background(Slate50)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Trophy Icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color(0xFFFEF9C3), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Success",
                    tint = Color(0xFFFACC15),
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Session Complete!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "You're crushing your recovery goals.",
                color = Slate500,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Stats Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ResultMetricCard(
                    modifier = Modifier.weight(1f),
                    label = "Reps",
                    value = reps.toString(),
                    valueColor = Blue500
                )
                ResultMetricCard(
                    modifier = Modifier.weight(1f),
                    label = "Max ROM",
                    value = "${maxRom}°",
                    valueColor = Blue500
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gait Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Gait Status", color = Slate500, fontSize = 14.sp)
                        Text(
                            text = gaitStatus,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                    }
                    Surface(
                        color = Green50,
                        shape = CircleShape
                    ) {
                        Text(
                            text = "Good",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Green500,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AI Suggestion Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Blue50.copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Blue100)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(8.dp),
                            shape = CircleShape,
                            color = Blue500
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI SUGGESTION",
                            style = MaterialTheme.typography.labelMedium,
                            color = Blue500,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = aiSuggestion,
                        color = Slate700,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            Button(
                onClick = onLogPainClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue500)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Log Pain & Swelling", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onBackHomeClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = Slate500, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Back Home", color = Slate500, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showConfetti) {
            ConfettiBlast()
        }
    }
}

@Composable
fun ResultMetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    valueColor: Color
) {
    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = label, color = Slate500, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = valueColor
            )
        }
    }
}

@Composable
fun ConfettiBlast() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val colors = listOf(Color(0xFF3B82F6), Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFF8B5CF6))
    
    val particles = remember {
        List(50) {
            ParticleData(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                color = colors.random(),
                size = Random.nextInt(10, 30).toFloat(),
                angle = Random.nextFloat() * 360f,
                speed = Random.nextFloat() * 1000f + 500f
            )
        }
    }

    particles.forEach { particle ->
        val xAnim = animateFloatAsState(
            targetValue = if (particle.speed > 0) particle.x * 2 - 0.5f else particle.x,
            animationSpec = tween(1500, easing = LinearOutSlowInEasing)
        )
        val yAnim = animateFloatAsState(
            targetValue = 1.2f, // Fall down
            animationSpec = tween(2000, easing = FastOutSlowInEasing)
        )
        
        val alphaAnim = animateFloatAsState(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 2000, delayMillis = 500)
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            rotate(particle.angle + (yAnim.value * 360f)) {
                drawRect(
                    color = particle.color.copy(alpha = alphaAnim.value + 0.5f),
                    topLeft = androidx.compose.ui.geometry.Offset(
                        x = xAnim.value * size.width,
                        y = yAnim.value * size.height - (particle.y * 500)
                    ),
                    size = androidx.compose.ui.geometry.Size(particle.size, particle.size / 2)
                )
            }
        }
    }
}

data class ParticleData(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
    val angle: Float,
    val speed: Float
)
