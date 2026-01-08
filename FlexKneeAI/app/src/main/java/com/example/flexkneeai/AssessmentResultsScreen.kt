package com.example.flexkneeai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flexkneeai.ui.theme.*

@Composable
fun AssessmentResultsScreen(
    romAngle: Int,
    targetAngle: Int = 120,
    gaitStatus: String = "Slight Limp",
    gaitDetail: String = "Detected in left leg stride",
    onContinueClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Slate50
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Success Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Green50, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Green100, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Green500,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Assessment Complete",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Great job! Here are your baseline\nmeasurements.",
                textAlign = TextAlign.Center,
                color = Slate500,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ROM Angle Card
            ResultCard(
                icon = Icons.Filled.OpenInFull,
                iconBg = Blue50,
                iconTint = Blue500,
                title = "ROM Angle",
                badgeText = "Baseline",
                badgeBg = Blue50,
                badgeTint = Blue500,
                valueText = "${romAngle}°",
                subValueText = "Target: ${targetAngle}°"
            )

            Spacer(modifier = Modifier.height(16.dp))

            val gaitColor: Color
            val gaitBg: Color
            val gaitBadgeBg: Color
            val gaitBadgeTint: Color
            val gaitBadgeText: String

            when (gaitStatus) {
                "Normal" -> {
                    gaitColor = Green500
                    gaitBg = Green50
                    gaitBadgeBg = Green100
                    gaitBadgeTint = Green500
                    gaitBadgeText = "Excellent"
                }
                "Skipped" -> {
                    gaitColor = Slate500
                    gaitBg = Slate100
                    gaitBadgeBg = Slate50
                    gaitBadgeTint = Slate500
                    gaitBadgeText = "Not Tested"
                }
                else -> {
                    gaitColor = Color(0xFFF97316)
                    gaitBg = Color(0xFFFFF7ED)
                    gaitBadgeBg = Color(0xFFFEF3C7)
                    gaitBadgeTint = Color(0xFFD97706)
                    gaitBadgeText = "Attention"
                }
            }

            // Gait Status Card
            ResultCard(
                icon = Icons.Filled.DirectionsWalk,
                iconBg = gaitBg,
                iconTint = gaitColor,
                title = "Gait Status",
                badgeText = gaitBadgeText,
                badgeBg = gaitBadgeBg,
                badgeTint = gaitBadgeTint,
                valueText = gaitStatus,
                subValueText = gaitDetail
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onContinueClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue500)
            ) {
                Text(
                    text = "Continue to Dashboard",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ResultCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    badgeText: String,
    badgeBg: Color,
    badgeTint: Color,
    valueText: String,
    subValueText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(iconBg, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Slate900
                    )
                }

                Surface(
                    color = badgeBg,
                    shape = CircleShape
                ) {
                    Text(
                        text = badgeText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = badgeTint,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = valueText,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Slate900
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subValueText,
                color = Slate500,
                fontSize = 14.sp
            )
        }
    }
}
