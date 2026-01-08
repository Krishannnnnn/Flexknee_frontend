package com.example.flexkneeai

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onAnimationComplete: () -> Unit) {
    val logoScale = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffset = remember { Animatable(50f) }

    LaunchedEffect(key1 = true) {
        // Parallel animations
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        
        delay(100)
        launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
        }
        launch {
            textOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
        
        // Wait for animations to settle then trigger callback
        delay(1500)
        onAnimationComplete()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC) // Very light blue-ish white background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            
            Box(contentAlignment = Alignment.Center) {
                // Logo Container
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(logoScale.value)
                        .shadow(
                            elevation = 15.dp,
                            shape = RoundedCornerShape(28.dp),
                            spotColor = Color(0x20000000),
                            ambientColor = Color(0x10000000)
                        ),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White
                ) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                        val width = size.width
                        val height = size.height
                        
                        // Horizon Line (Curved path)
                        val horizonPath = Path().apply {
                            moveTo(0f, height * 0.8f)
                            quadraticBezierTo(
                                width / 2, height * 0.5f, // Control point
                                width, height * 0.8f // End point
                            )
                        }
                        drawPath(
                            path = horizonPath,
                            color = Color(0xFF475569), // Slate 600
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Pulse Line (Blue ECG style)
                        val pulsePath = Path().apply {
                            moveTo(width * 0.1f, height * 0.5f)
                            lineTo(width * 0.35f, height * 0.5f)
                            lineTo(width * 0.45f, height * 0.2f) // Peak up
                            lineTo(width * 0.55f, height * 0.7f) // Peak down
                            lineTo(width * 0.65f, height * 0.45f) // Small correction
                            lineTo(width * 0.9f, height * 0.45f)
                        }
                        
                        drawPath(
                            path = pulsePath,
                            color = Color(0xFF3B82F6), // Blue 500
                            style = Stroke(
                                width = 4.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
                

            }

            Spacer(modifier = Modifier.height(32.dp))

            // Text Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(textAlpha.value)
                    .offset(y = textOffset.value.dp)
            ) {
                Text(
                    text = "FlexKnee AI",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A), // Slate 900
                        fontSize = 28.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Smart Rehabilitation",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF60A5FA), // Blue 400
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}
