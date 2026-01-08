package com.example.flexkneeai

import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.flexkneeai.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.*

@Composable
fun ExerciseAnalysisScreen(
    exercise: ExerciseDetail,
    onBackClick: () -> Unit,
    onExerciseComplete: (Int, Int) -> Unit // reps, maxAngle
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    val isFrontCamera = cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA

    var poseResults by remember { mutableStateOf<PoseLandmarkerHelper.ResultBundle?>(null) }
    var kneeAngleResult by remember { mutableStateOf<PoseLandmarkerHelper.KneeAngleResult?>(null) }
    
    var repCount by remember { mutableStateOf(0) }
    var currentSet by remember { mutableStateOf(1) }
    var isExercising by remember { mutableStateOf(false) }
    var feedbackText by remember { mutableStateOf("Press Start to Begin") }
    var maxFlexionSession by remember { mutableStateOf(180.0) }
    var timerSeconds by remember { mutableStateOf(0) }
    
    val repetitionCounter = remember { PoseLandmarkerHelper.RepetitionCounter() }

    // Timer Logic
    LaunchedEffect(isExercising) {
        while (isExercising) {
            delay(1000)
            timerSeconds++
        }
    }

    fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
    }

    // Rep Counting Logic
    LaunchedEffect(kneeAngleResult) {
        kneeAngleResult?.primaryAngle?.let { angle ->
            if (isExercising) {
                // Update max flexion for session
                if (angle < maxFlexionSession) {
                    maxFlexionSession = angle
                }

                // Determine feedback based on angle
                feedbackText = if (angle < 100) "Good Depth!" else "Bend Knee"

                // Process Repetition using dynamic thresholds based on exercise type
                val count = when (exercise.title) {
                    "Heel Slides" -> repetitionCounter.processAngle(angle, System.currentTimeMillis(), thresholdStart = 155.0, thresholdBent = 105.0)
                    "Quad Sets" -> repetitionCounter.processAngle(angle, System.currentTimeMillis(), thresholdStart = 175.0, thresholdBent = 165.0)
                    "Straight Leg Raises" -> repetitionCounter.processAngle(angle, System.currentTimeMillis(), thresholdStart = 175.0, thresholdBent = 160.0)
                    else -> repetitionCounter.processAngle(angle, System.currentTimeMillis()) // default 155/100
                }
                
                if (count > repCount) {
                     repCount = count
                     feedbackText = "Good Rep!"
                }
            } else if (repCount > 0) {
                feedbackText = "Paused. Press Start to continue."
            }
        }
    }

    val poseLandmarkerHelper = remember {
        PoseLandmarkerHelper(
            context = context,
            poseLandmarkerHelperListener = object : PoseLandmarkerHelper.LandmarkerListener {
                override fun onError(error: String, errorCode: Int) {
                    Log.e("ExerciseAnalysis", "Pose Error: $error")
                }

                override fun onResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
                    poseResults = resultBundle
                    kneeAngleResult = PoseLandmarkerHelper.calculateEnhancedKneeAngles(resultBundle)
                }
            }
        )
    }

    val backgroundExecutor = remember {
        java.util.concurrent.Executors.newSingleThreadExecutor()
    }

    DisposableEffect(Unit) {
        onDispose {
            backgroundExecutor.shutdown()
            poseLandmarkerHelper.clearPoseLandmarker()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Unified Camera Preview and Lifecycle Binding
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { previewView ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    
                    val imageAnalysis = androidx.camera.core.ImageAnalysis.Builder()
                        .setBackpressureStrategy(androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .build()
                        .also { analysis ->
                            analysis.setAnalyzer(backgroundExecutor) { imageProxy ->
                                poseLandmarkerHelper.detectLiveStream(imageProxy, isFrontCamera)
                            }
                        }
                    
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
                    } catch (e: Exception) {
                        Log.e("ExerciseAnalysis", "Binding failed", e)
                    }
                }, ContextCompat.getMainExecutor(context))
            }
        )

        // Pose Overlay (Knee Only)
        PoseOverlay(
            results = poseResults?.results?.firstOrNull(),
            imageWidth = (poseResults?.inputImageWidth ?: 0),
            imageHeight = (poseResults?.inputImageHeight ?: 0),
            modifier = Modifier.fillMaxSize(),
            kneeAngleResult = kneeAngleResult,
            visualizeOnlyKnee = true
        )

        // UI Overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = exercise.title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Set $currentSet of ${exercise.sets}",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.size(4.dp).background(Color.White.copy(alpha=0.5f), CircleShape))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formatTime(timerSeconds),
                            color = Blue500,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Camera Toggle
                    IconButton(
                        onClick = {
                            cameraSelector = if (isFrontCamera) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA
                        },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isFrontCamera) Icons.Default.CameraRear else Icons.Default.CameraFront,
                            contentDescription = "Switch Camera",
                            tint = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Rep Counter
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = Blue500
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = repCount.toString(),
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom UI
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Angle Card
                Surface(
                    modifier = Modifier.size(140.dp, 160.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.size(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = (180 - (kneeAngleResult?.primaryAngle ?: 180.0)).toFloat() / 110f,
                                modifier = Modifier.fillMaxSize(),
                                color = Blue500,
                                strokeWidth = 8.dp,
                                trackColor = Slate100
                            )
                            Text(
                                text = "${(180 - (kneeAngleResult?.primaryAngle ?: 180.0)).toInt()}°",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Slate900
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Current Angle", color = Slate500, fontSize = 12.sp)
                    }
                }

                // AI Feedback Badge
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Live Feed", color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Controls Row (Start/Stop/Retake/Finish)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isExercising && repCount == 0) {
                    // Start Button
                    FloatingActionButton(
                        onClick = { 
                            isExercising = true 
                            repetitionCounter.reset()
                            repCount = 0
                            timerSeconds = 0
                            feedbackText = "Exercise Started!"
                        },
                        containerColor = Blue500,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, "Start", modifier = Modifier.size(32.dp))
                    }
                } else if (isExercising) {
                    // Stop Button
                    FloatingActionButton(
                        onClick = { isExercising = false },
                        containerColor = Color(0xFFEF4444),
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(Icons.Default.Stop, "Stop", modifier = Modifier.size(32.dp))
                    }
                } else {
                    // Retake
                    Button(
                        onClick = { 
                            repCount = 0
                            timerSeconds = 0
                            repetitionCounter.reset()
                            isExercising = true
                            feedbackText = "Exercise Restarted!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha=0.2f)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(Icons.Default.Refresh, "Retake", tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Retake", color = Color.White)
                    }

                    // Finish
                    Button(
                        onClick = { onExerciseComplete(repCount, (180 - maxFlexionSession).toInt()) },
                        colors = ButtonDefaults.buttonColors(containerColor = Blue500),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, "Finish", tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Finish", color = Color.White)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // AI Recommendation Text
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Blue500.copy(alpha = 0.9f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = feedbackText,
                    modifier = Modifier.padding(16.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
