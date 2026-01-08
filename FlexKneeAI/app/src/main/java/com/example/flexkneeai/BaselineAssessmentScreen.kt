package com.example.flexkneeai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flexkneeai.ui.theme.Blue100
import com.example.flexkneeai.ui.theme.Blue50
import com.example.flexkneeai.ui.theme.Blue500
import com.example.flexkneeai.ui.theme.Green100
import com.example.flexkneeai.ui.theme.Green500
import com.example.flexkneeai.ui.theme.Slate100
import com.example.flexkneeai.ui.theme.Slate500
import com.example.flexkneeai.ui.theme.Slate900

@Composable
fun BaselineAssessmentScreen(
    onBeginAssessmentClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC) // Slate 50 equivalent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Baseline Assessment",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        fontSize = 20.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Title & Subtitle
            Text(
                text = "Let's Establish Your\nBaseline",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "We'll measure your current range of motion and analyze your walking pattern to set a starting point for your recovery.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Slate500,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Assessment Cards
            AssessmentCard(
                icon = Icons.Default.ZoomOutMap, // Placeholder for expand/arrows icon
                iconTint = Blue500,
                iconBg = Blue50,
                title = "1. Knee Bend Test",
                description = "Measure flexion angle"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Using a generic icon for "Steps" as we might not have a dedicated shoe-print icon readily available in default material icons
            // Using a drawable resource would be better, but for now using a placeholder.
            // Assuming we don't have custom assets yet, I'll use a placeholder icon.
            AssessmentCard(
                icon = Icons.Default.ZoomOutMap, // REPLACE with appropriate icon or resource if available. 
                // Actually, let's use a Box with text or a shape if no icon fits.
                // Or better, let's just reuse a similar vector or create a custom simple one if possible.
                // For now, I'll use another icon to distinguish.
                iconTint = Green500,
                iconBg = Green100,
                title = "2. Short Walk Test",
                description = "Analyze gait pattern",
                isWalkTest = true
            )

            Spacer(modifier = Modifier.weight(1f))

            // Begin Button
            Button(
                onClick = onBeginAssessmentClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue500
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 1.dp
                )
            ) {
                Text(
                    text = "Begin Assessment",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AssessmentCard(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    description: String,
    isWalkTest: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        // Add a border or shadow if needed to match design exactly, 
        // Screenshot shows a clean white card, likely with a very subtle drop shadow or border.
        // Let's add a subtle border.
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (isWalkTest) {
                    // Quick hack to show "shoes" if we don't have the icon,
                    // or just use the passed icon.
                    // For the walk test, the screenshot shows footprints. 
                    // Since I don't have the asset, I'll use text "88" turned 90 degrees or similar, 
                    // OR just rely on the color.
                    // Let's stick to the passed icon for safety to avoid compilation errors.
                     Icon(
                        imageVector = icon, 
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Slate500
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun BaselineAssessmentScreenPreview() {
    BaselineAssessmentScreen(onBeginAssessmentClick = {})
}
