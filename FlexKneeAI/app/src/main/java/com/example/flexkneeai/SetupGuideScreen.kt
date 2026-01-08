package com.example.flexkneeai

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flexkneeai.ui.theme.Amber100
import com.example.flexkneeai.ui.theme.Amber500
import com.example.flexkneeai.ui.theme.Blue50
import com.example.flexkneeai.ui.theme.Blue500
import com.example.flexkneeai.ui.theme.Cream50
import com.example.flexkneeai.ui.theme.Green50
import com.example.flexkneeai.ui.theme.Green500
import com.example.flexkneeai.ui.theme.Slate50
import com.example.flexkneeai.ui.theme.Slate500
import com.example.flexkneeai.ui.theme.Slate900

@Composable
fun SetupGuideScreen(
    onDismissRequest: () -> Unit,
    onReadyClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Slate50
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.setup_guide_title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Slate900
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card 1: Position Your Phone
            SetupGuideCard(
                iconBackground = Blue50,
                title = stringResource(R.string.setup_guide_card_1_title),
                description = stringResource(R.string.setup_guide_card_1_desc),
                iconContent = {
                    Icon(
                        imageVector = Icons.Outlined.Smartphone,
                        contentDescription = null,
                        tint = Blue500,
                        modifier = Modifier.size(32.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card 2: Check Lighting
            SetupGuideCard(
                iconBackground = Cream50,
                title = stringResource(R.string.setup_guide_card_2_title),
                description = stringResource(R.string.setup_guide_card_2_desc),
                iconContent = {
                    Icon(
                        imageVector = Icons.Outlined.WbSunny,
                        contentDescription = null,
                        tint = Amber500,
                        modifier = Modifier.size(32.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card 3: Side Profile
            SetupGuideCard(
                iconBackground = Green50,
                title = stringResource(R.string.setup_guide_card_3_title),
                description = stringResource(R.string.setup_guide_card_3_desc),
                iconContent = {
                    Text(
                        text = "90°",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Green500,
                            fontSize = 24.sp
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Footer Button
            Button(
                onClick = onReadyClick,
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
                    text = stringResource(R.string.setup_guide_button),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            
            // Add extra space at the bottom for scrolling
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SetupGuideCard(
    iconBackground: Color,
    title: String,
    description: String,
    iconContent: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                iconContent()
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    fontSize = 18.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Slate500,
                    lineHeight = 22.sp,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun SetupGuideScreenPreview() {
    SetupGuideScreen(
        onDismissRequest = {},
        onReadyClick = {}
    )
}
