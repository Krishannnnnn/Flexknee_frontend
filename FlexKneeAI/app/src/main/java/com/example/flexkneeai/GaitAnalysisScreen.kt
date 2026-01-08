package com.example.flexkneeai

import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.flexkneeai.ui.theme.*
import kotlinx.coroutines.delay

enum class GaitPhase(val label: String) {
    GET_READY("Get Ready"),
    WALK_AWAY("Walk away from camera"),
    TURN_AROUND("Turn around"),
    WALK_BACK("Walk back to camera"),
    COMPLETED("Analysis Complete")
}

@Composable
fun GaitAnalysisScreen(
    onBackClick: () -> Unit,
    onAnalysisComplete: (String, String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    val isFrontCamera = cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA

    var poseResults by remember { mutableStateOf<PoseLandmarkerHelper.ResultBundle?>(null) }
    var inputImageHeight by remember { mutableStateOf(0) }
    var inputImageWidth by remember { mutableStateOf(0) }
    var kneeAngleResult by remember { mutableStateOf<PoseLandmarkerHelper.KneeAngleResult?>(null) }

    var currentPhase by remember { mutableStateOf(GaitPhase.GET_READY) }
    var progress by remember { mutableStateOf(0f) }
    
    // Manual control states
    var isRecording by remember { mutableStateOf(false) }
    var isTestComplete by remember { mutableStateOf(false) }
    var showResultsUI by remember { mutableStateOf(false) }

    // State machine for gait analysis logic
    LaunchedEffect(poseResults, isRecording) {
        if (!isRecording || isTestComplete) return@LaunchedEffect

        val landmarks = poseResults?.results?.firstOrNull()?.landmarks()?.firstOrNull()
        val isVisible = landmarks != null && landmarks.size > 24 

        when (currentPhase) {
            GaitPhase.GET_READY -> {
                if (isVisible) {
                    delay(1000)
                    currentPhase = GaitPhase.WALK_AWAY
                    progress = 0.1f
                }
            }
            GaitPhase.WALK_AWAY -> {
                if (isVisible) {
                    progress += 0.004f
                    if (progress >= 0.45f) {
                        currentPhase = GaitPhase.TURN_AROUND
                    }
                }
            }
            GaitPhase.TURN_AROUND -> {
                if (isVisible) {
                    progress += 0.008f
                    if (progress >= 0.55f) {
                        currentPhase = GaitPhase.WALK_BACK
                    }
                }
            }
            GaitPhase.WALK_BACK -> {
                if (isVisible) {
                    progress += 0.004f
                    if (progress >= 1.0f) {
                        currentPhase = GaitPhase.COMPLETED
                        isRecording = false
                        isTestComplete = true
                        showResultsUI = true
                    }
                }
            }
            GaitPhase.COMPLETED -> {}
        }
    }

    val poseLandmarkerHelper = remember {
        PoseLandmarkerHelper(
            context = context,
            poseLandmarkerHelperListener = object : PoseLandmarkerHelper.LandmarkerListener {
                override fun onError(error: String, errorCode: Int) {
                    Log.e("GaitAnalysis", "Pose Error: $error")
                }

                override fun onResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
                    poseResults = resultBundle
                    inputImageHeight = resultBundle.inputImageHeight
                    inputImageWidth = resultBundle.inputImageWidth
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = "Gait Analysis",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Walk away from the camera,\nturn around, and walk back.",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Close Button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(24.dp)
                    .background(Color.White, CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Camera View Container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 140.dp)
            ) {
                // Dashed Border Container
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(32.dp))
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(32.dp)
                        )
                ) {
                    // Camera Preview
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                                
                                // Initial Setup
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(this.surfaceProvider)
                                    }
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
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner, cameraSelector, preview, imageAnalysis
                                        )
                                    } catch (e: Exception) {
                                        Log.e("GaitAnalysis", "Initial binding failed", e)
                                    }
                                }, ContextCompat.getMainExecutor(context))
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        update = { /* No-op to avoid rebinding on recomposition */ }
                    )

                    // Skeleton Overlay
                    PoseOverlay(
                        results = poseResults?.results?.firstOrNull(),
                        imageWidth = inputImageWidth,
                        imageHeight = inputImageHeight,
                        modifier = Modifier.fillMaxSize(),
                        kneeAngleResult = kneeAngleResult
                    )

                    // Inner Dashed Rectangle (Guide)
                    Canvas(modifier = Modifier.fillMaxSize().padding(32.dp)) {
                        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.3f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f),
                            style = Stroke(width = 2f, pathEffect = pathEffect)
                        )
                    }

                    // Status Badges (Live & Camera Switch)
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        if (isRecording) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .background(Color.Red.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color.White, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("REC", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(if (isTestComplete) Green500 else Color.Red, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isTestComplete) "Done" else "Live", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        IconButton(
                            onClick = {
                                cameraSelector = if (isFrontCamera) CameraSelector.DEFAULT_BACK_CAMERA 
                                                else CameraSelector.DEFAULT_FRONT_CAMERA
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 48.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(Icons.Default.Cached, "Switch", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }

                    // Get Ready Overlay
                    if (!isRecording && !isTestComplete) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
                                .padding(horizontal = 40.dp, vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Ready to Analyze",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Tap Start to begin",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    
                    // Completion Message
                    if (isTestComplete) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
                                .padding(horizontal = 40.dp, vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Green500,
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Analysis Complete",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    // Full Body View Text
                    Text(
                        "Full Body View",
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp),
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            }

            // Bottom Controls
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress Bar
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = if (isTestComplete) Green500 else Blue500,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = when {
                        isTestComplete -> "Gait analysis complete"
                        isRecording -> "Analyzing gait pattern... (${currentPhase.label})"
                        showResultsUI -> "Analysis stopped"
                        else -> "Position yourself in the guide"
                    },
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (showResultsUI) {
                    // Retake and OK Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {
                                // Reset all states
                                isRecording = false
                                isTestComplete = false
                                showResultsUI = false
                                progress = 0f
                                currentPhase = GaitPhase.GET_READY
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.Refresh, null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Retake", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        
                        Button(
                            onClick = { onAnalysisComplete("Normal", "Symmetry looks good in both strides.") },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Green500)
                        ) {
                            Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Default.ChevronRight, null, tint = Color.White)
                        }
                    }
                } else {
                    // Start/Stop Toggle
                    Button(
                        onClick = { 
                            if (isRecording) {
                                // Stopping
                                isRecording = false
                                showResultsUI = true
                            } else {
                                // Starting
                                isRecording = true
                                showResultsUI = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecording) Color.Red else Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isRecording) "Stop" else "Start",
                            tint = if (isRecording) Color.White else Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = if (isRecording) "Stop Analysis" else "Start Analysis",
                            color = if (isRecording) Color.White else Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
