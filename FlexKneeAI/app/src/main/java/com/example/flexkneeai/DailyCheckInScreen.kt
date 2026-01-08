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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flexkneeai.ui.theme.*

@Composable
fun DailyCheckInScreen(
    currentPain: Int,
    currentSwelling: String,
    onSaveLog: (Int, String) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    var painLevel by remember { mutableFloatStateOf(currentPain.toFloat()) }
    var selectedSwelling by remember { mutableStateOf(currentSwelling) }

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
                    text = "Daily Check-in",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Pain Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "How is your pain today?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = painLevel.toInt().toString(),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.width(48.dp))
                        val (icon, tint) = when {
                            painLevel < 3f -> Icons.Default.SentimentVerySatisfied to Green500
                            painLevel < 5f -> Icons.Default.SentimentSatisfied to Color(0xFFFBBF24) // Amber 400
                            painLevel < 7f -> Icons.Default.SentimentNeutral to Color(0xFFF59E0B) // Amber 500
                            painLevel < 9f -> Icons.Default.SentimentDissatisfied to Color(0xFFEF4444) // Red 500
                            else -> Icons.Default.SentimentVeryDissatisfied to Color(0xFFB91C1C) // Red 700
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = tint,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                    
                    Text(text = "Pain Level", color = Slate500, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(32.dp))

                    Slider(
                        value = painLevel,
                        onValueChange = { painLevel = it },
                        valueRange = 0f..10f,
                        steps = 9,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Slate200,
                            inactiveTrackColor = Slate100
                        )
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("No Pain", color = Slate400, fontSize = 12.sp)
                        Text("Moderate", color = Slate400, fontSize = 12.sp)
                        Text("Severe", color = Slate400, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Swelling Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Swelling Level",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        SwellingButton(
                            label = "Low",
                            selected = selectedSwelling == "Low",
                            modifier = Modifier.weight(1f),
                            onClick = { selectedSwelling = "Low" }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        SwellingButton(
                            label = "Medium",
                            selected = selectedSwelling == "Medium",
                            modifier = Modifier.weight(1f),
                            onClick = { selectedSwelling = "Medium" }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        SwellingButton(
                            label = "High",
                            selected = selectedSwelling == "High",
                            modifier = Modifier.weight(1f),
                            onClick = { selectedSwelling = "High" }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFEFF6FF), // Blue 50
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Consistent tracking helps your physiotherapist adjust your plan for better results.",
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFF1E40AF), // Blue 800
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSaveLog(painLevel.toInt(), selectedSwelling) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue500)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Save Log",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SwellingButton(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFFECFDF5) else Slate50, // Green 50 if selected
            contentColor = if (selected) Green500 else Slate600
        ),
        border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, Green500) else null,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(text = label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}
