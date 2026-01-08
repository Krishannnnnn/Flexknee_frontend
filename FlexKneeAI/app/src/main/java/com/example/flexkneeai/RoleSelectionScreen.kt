package com.example.flexkneeai

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale as modifierScale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val cardsAlpha = remember { Animatable(0f) }
    val cardsScale = remember { Animatable(0.8f) }

    LaunchedEffect(key1 = true) {
        launch {
            cardsAlpha.animateTo(1f, tween(600))
        }
        launch {
            cardsScale.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC) // Slate 50
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Welcome to FlexKnee",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A), // Slate 900
                    fontSize = 28.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Who are you?",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF64748B), // Slate 500
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // Role Cards Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(cardsAlpha.value)
                    .modifierScale(cardsScale.value),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Patient Card
                RoleSquareCard(
                    title = "Patient",
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF3B82F6), // Blue 500
                    bgColor = Color(0xFFEFF6FF), // Blue 50
                    onClick = {
                        onRoleSelected("patient")
                    }
                ) {
                    AnimatedPatientGraphic()
                }

                // Physio Card
                RoleSquareCard(
                    title = "Therapist",
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF10B981), // Emerald 500
                    bgColor = Color(0xFFECFDF5), // Emerald 50
                    onClick = {
                        onRoleSelected("therapist")
                    }
                ) {
                    AnimatedTherapistGraphic()
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun RoleSquareCard(
    title: String,
    modifier: Modifier = Modifier,
    color: Color,
    bgColor: Color,
    onClick: () -> Unit,
    graphic: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(0.8f) 
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(bgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                graphic()
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 20.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AnimatedPatientGraphic() {
    val legRotation = remember { Animatable(0f) }
    
    LaunchedEffect(true) {
        legRotation.animateTo(
            targetValue = -30f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Canvas(modifier = Modifier.size(60.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        
        // Static Body
        drawLine(
            color = Color(0xFF3B82F6),
            start = Offset(centerX, centerY - 20f),
            end = Offset(centerX, centerY + 10f),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )
        // Head
        drawCircle(
            color = Color(0xFF3B82F6),
            radius = 6.dp.toPx(),
            center = Offset(centerX, centerY - 30f)
        )
        
        // Thigh
        drawLine(
            color = Color(0xFF3B82F6),
            start = Offset(centerX, centerY + 10f),
            end = Offset(centerX + 15f, centerY + 30f),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Lower Leg (Animated)
        rotate(degrees = legRotation.value, pivot = Offset(centerX + 15f, centerY + 30f)) {
            drawLine(
                color = Color(0xFF3B82F6),
                start = Offset(centerX + 15f, centerY + 30f),
                end = Offset(centerX + 15f, centerY + 60f), 
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
            // Foot
            drawLine(
                color = Color(0xFF3B82F6),
                start = Offset(centerX + 15f, centerY + 60f),
                end = Offset(centerX + 25f, centerY + 60f),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun AnimatedTherapistGraphic() {
    val scale = remember { Animatable(1f) }
    
    LaunchedEffect(true) {
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Canvas(modifier = Modifier.size(60.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        
        // Clipboard
        drawRoundRect(
            color = Color(0xFF10B981),
            topLeft = Offset(centerX - 20f, centerY - 25f),
            size = Size(40f, 50f),
            cornerRadius = CornerRadius(4f, 4f),
            style = Stroke(width = 3.dp.toPx())
        )
        
        // Cross (Pulsing)
        // Using translation to keep it centered
        val crossSize = 20f
        val currentScale = scale.value
        
        // Manually translate context
        val prevTransform = drawContext.transform
        drawContext.transform.translate(centerX, centerY)
        
        scale(scale = currentScale, pivot = Offset.Zero) {
            drawLine(
                color = Color(0xFF10B981),
                start = Offset(0f, -10f),
                end = Offset(0f, 10f),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color(0xFF10B981),
                start = Offset(-10f, 0f),
                end = Offset(10f, 0f),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        // Restore transform
        drawContext.transform.translate(-centerX, -centerY)
    }
}
