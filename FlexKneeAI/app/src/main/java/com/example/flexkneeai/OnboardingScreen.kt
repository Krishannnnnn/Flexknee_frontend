package com.example.flexkneeai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isLastPage: Boolean = false
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) }
    
    val pages = listOf(
        OnboardingPage(
            title = "AI-Powered Tracking",
            description = "Use your phone's camera to precisely measure your knee range of motion without any wearables.",
            icon = Icons.Outlined.PhotoCamera
        ),
        // Assuming a middle step might exist based on dots, but user provided this as "next". 
        // We will just show 2 pages for now but style the dots to look like there are 3 if needed, 
        // or just adapt to 2. Let's adapt to 2 for a logical flow, or add a filler if requested.
        // For now, I'll stick to the requested screens.
        OnboardingPage(
            title = "Clinical Accuracy",
            description = "Get hospital-grade assessments and share progress directly with your doctor.",
            icon = Icons.Outlined.VerifiedUser,
            isLastPage = true
        )
    )

    val currentPageData = pages[currentPage]

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
            Spacer(modifier = Modifier.weight(1f))

            // Icon Container
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color(0xFFE0F2FE), CircleShape), // Sky 100
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = currentPageData.icon,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFF3B82F6) // Blue 500
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Title
            Text(
                text = currentPageData.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A), // Slate 900
                    fontSize = 24.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Body
            Text(
                text = currentPageData.description,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF64748B), // Slate 500
                    lineHeight = 24.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Pagination Dots
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // We'll show 3 dots to match the design aesthetic, but only map 0 and 1 active for now.
                // Or simply map pages.size. Let's map pages.size to be safe.
                 repeat(pages.size) { index ->
                     val isSelected = currentPage == index
                     val width = if (isSelected) 32.dp else 8.dp
                     val color = if (isSelected) Color(0xFF3B82F6) else Color(0xFFE2E8F0)
                     
                     Box(
                         modifier = Modifier
                             .padding(end = 8.dp)
                             .width(width)
                             .height(8.dp)
                             .background(color, RoundedCornerShape(4.dp))
                     )
                 }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Button
            Button(
                onClick = {
                    if (currentPageData.isLastPage) {
                        onFinish()
                    } else {
                        currentPage++
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6) // Blue 500
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Text(
                    text = if (currentPageData.isLastPage) "Get Started" else "Next",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                )
                if (!currentPageData.isLastPage) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen {}
}
