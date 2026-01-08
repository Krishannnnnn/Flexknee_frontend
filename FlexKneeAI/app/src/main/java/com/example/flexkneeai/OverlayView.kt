package com.example.flexkneeai

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import android.graphics.Paint
import android.graphics.Typeface
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun PoseOverlay(
    results: PoseLandmarkerResult?,
    imageWidth: Int,
    imageHeight: Int,
    modifier: Modifier = Modifier,
    kneeAngleResult: PoseLandmarkerHelper.KneeAngleResult? = null,
    visualizeOnlyKnee: Boolean = false
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        if (results == null || results.landmarks().isEmpty()) return@Canvas

        val canvasWidth = size.width
        val canvasHeight = size.height
        
        val scaleFactor = max(canvasWidth / imageWidth, canvasHeight / imageHeight)
        val scaledWidth = imageWidth * scaleFactor
        // Center crop alignment
        val offsetX = (canvasWidth - scaledWidth) / 2f
        val offsetY = (canvasHeight - (imageHeight * scaleFactor)) / 2f

        for (landmark in results.landmarks()) {
            // Draw lines (connections)
            PoseLandmarker.POSE_LANDMARKS.forEach { connection ->
                // If knee only, skip upper body connections
                // Leg landmarks start from index 23 (hips) to 32
                val isLegConnection = (connection.start() >= 23 && connection.end() >= 23)
                
                if (visualizeOnlyKnee && !isLegConnection) return@forEach

                val start = landmark[connection.start()]
                val end = landmark[connection.end()]
                
                val startX = start.x() * imageWidth * scaleFactor + offsetX
                val startY = start.y() * imageHeight * scaleFactor + offsetY
                val endX = end.x() * imageWidth * scaleFactor + offsetX
                val endY = end.y() * imageHeight * scaleFactor + offsetY
                
                drawLine(
                    color = Color.White,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
            }
            
            // Draw points
            for ((index, normalizedLandmark) in landmark.withIndex()) {
                if (visualizeOnlyKnee && index < 23) continue

                val cx = normalizedLandmark.x() * imageWidth * scaleFactor + offsetX
                val cy = normalizedLandmark.y() * imageHeight * scaleFactor + offsetY

                drawCircle(
                    color = Color(0xFF3B82F6), // Blue 500
                    radius = 8f,
                    center = Offset(cx, cy)
                )
            }

            // Draw Angle Text
            kneeAngleResult?.primaryAngle?.let { angle ->
                // Draw near the knee (Landmark 25 for Left, 26 for Right)
                val isRight = kneeAngleResult.side == "Right"
                val kneeIndex = if (isRight) 26 else 25
                val knee = landmark.getOrNull(kneeIndex)
                
                knee?.let { k ->
                    val kx = k.x() * imageWidth * scaleFactor + offsetX
                    val ky = k.y() * imageHeight * scaleFactor + offsetY
                    
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 54f
                            typeface = Typeface.DEFAULT_BOLD
                            setShadowLayer(5f, 0f, 0f, android.graphics.Color.BLACK)
                        }
                        
                        // Show side info in small text above angle
                        val sidePaint = Paint().apply {
                            color = if (isRight) android.graphics.Color.CYAN else android.graphics.Color.YELLOW
                            textSize = 32f
                            typeface = Typeface.DEFAULT_BOLD
                        }
                        
                        canvas.nativeCanvas.drawText(
                            kneeAngleResult.side,
                            kx + 20f,
                            ky - 40f,
                            sidePaint
                        )
                        
                        canvas.nativeCanvas.drawText(
                            "${angle.roundToInt()}°",
                            kx + 20f,
                            ky + 20f,
                            paint
                        )
                    }
                }
            }
        }
    }
}

object PoseLandmarker {
    // MediaPipe Pose connection pairs indices
    data class Connection(val start: Int, val end: Int) {
        fun start() = start
        fun end() = end
    }
    
    val POSE_LANDMARKS = listOf(
        Connection(0, 1), Connection(1, 2), Connection(2, 3), Connection(3, 7),
        Connection(0, 4), Connection(4, 5), Connection(5, 6), Connection(6, 8),
        Connection(9, 10), Connection(11, 12), Connection(11, 13), Connection(13, 15),
        Connection(15, 17), Connection(15, 19), Connection(15, 21), Connection(17, 19),
        Connection(12, 14), Connection(14, 16), Connection(16, 18), Connection(16, 20),
        Connection(16, 22), Connection(18, 20), Connection(11, 23), Connection(12, 24),
        Connection(23, 24), Connection(23, 25), Connection(24, 26), Connection(25, 27),
        Connection(26, 28), Connection(27, 29), Connection(28, 30), Connection(29, 31),
        Connection(30, 32), Connection(27, 31), Connection(28, 32)
    )
}
