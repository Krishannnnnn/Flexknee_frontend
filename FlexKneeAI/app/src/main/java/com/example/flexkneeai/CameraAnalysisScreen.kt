package com.example.flexkneeai

import android.content.Context
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.*

@Composable
fun CameraAnalysisScreen(
    onBackClick: () -> Unit,
    onAssessmentComplete: (Int) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Camera Selector State
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    val isFrontCamera = cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA

    // Enhanced state management
    var poseResults by remember { mutableStateOf<PoseLandmarkerHelper.ResultBundle?>(null) }
    var inputImageHeight by remember { mutableStateOf(0) }
    var inputImageWidth by remember { mutableStateOf(0) }
    var kneeAngleResult by remember { mutableStateOf<PoseLandmarkerHelper.KneeAngleResult?>(null) }
    
    // Timer for assessment (auto-complete after 10 seconds of good tracking)
    var assessmentProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(kneeAngleResult) {
        if (kneeAngleResult != null && (kneeAngleResult?.confidence ?: 0.0) > 0.6) {
            while (assessmentProgress < 1f) {
                kotlinx.coroutines.delay(100)
                assessmentProgress += 0.01f
            }
            onAssessmentComplete(kneeAngleResult?.primaryAngle?.toInt() ?: 0)
        }
    }

    // Enhanced Pose Landmarker Helper with better listener
    val poseLandmarkerHelper = remember {
        PoseLandmarkerHelper(
            context = context,
            poseLandmarkerHelperListener = object : PoseLandmarkerHelper.LandmarkerListener {
                override fun onError(error: String, errorCode: Int) {
                    Log.e("CameraAnalysis", "Pose Error: $error (Code: $errorCode)")
                }

                override fun onResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
                    poseResults = resultBundle
                    inputImageHeight = resultBundle.inputImageHeight
                    inputImageWidth = resultBundle.inputImageWidth

                    // Enhanced knee angle calculation
                    val enhancedKneeResult = PoseLandmarkerHelper.calculateEnhancedKneeAngles(resultBundle)
                    kneeAngleResult = enhancedKneeResult
                }
            }
        )
    }

    // Background Executor
    val backgroundExecutor = remember {
        java.util.concurrent.Executors.newSingleThreadExecutor { r ->
            java.lang.Thread(r, "CameraAnalysisThread")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            backgroundExecutor.shutdown()
            poseLandmarkerHelper.clearPoseLandmarker()
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        // Unified Camera Preview and Lifecycle Binding
        AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder()
                        .setTargetResolution(android.util.Size(1280, 720))
                        .build()
                    
                    preview.setSurfaceProvider(this.surfaceProvider)
                    
                    val imageAnalysis = androidx.camera.core.ImageAnalysis.Builder()
                        .setTargetResolution(android.util.Size(1280, 720))
                        .setBackpressureStrategy(androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .build()
                        .also { analysis ->
                            analysis.setAnalyzer(backgroundExecutor) { imageProxy ->
                                poseLandmarkerHelper.detectLiveStream(
                                    imageProxy = imageProxy,
                                    isFrontCamera = isFrontCamera
                                )
                            }
                        }
                    
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
                    } catch (e: Exception) {
                        Log.e("CameraAnalysis", "Binding failed", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))
            }
        },
        modifier = Modifier.fillMaxSize()
    )

        // Enhanced Pose Overlay
        PoseOverlay(
            results = poseResults?.results?.firstOrNull(),
            imageWidth = inputImageWidth,
            imageHeight = inputImageHeight,
            modifier = Modifier.fillMaxSize(),
            kneeAngleResult = kneeAngleResult,
            visualizeOnlyKnee = false // Full body for baseline assessment
        )

        // Enhanced UI Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Camera Controls Group
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Close Button
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                        .size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Camera",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))
                
                // Switch Camera Button
                IconButton(
                    onClick = {
                        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }
                    },
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                        .size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cached,
                        contentDescription = "Switch Camera",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))
                
                // Finish Button
                Button(
                    onClick = { onAssessmentComplete(kneeAngleResult?.primaryAngle?.toInt() ?: 0) },
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Finish Assessment",
                        tint = Color.White
                    )
                }
            }

            // Enhanced Knee ROM Display
            kneeAngleResult?.let { result ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 120.dp, start = 24.dp)
                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Knee ROM (${result.side})",
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${result.primaryAngle?.toInt() ?: 0}°",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold
                            )
                        )
                        Text(
                            text = "Confidence: ${(result.confidence * 100).toInt()}%",
                            color = Color(0xFF22C55E),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Tracking Status
            Box(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Text(
                    text = when {
                        kneeAngleResult?.confidence ?: 0.0 > 0.7 -> "Excellent Tracking"
                        kneeAngleResult?.confidence ?: 0.0 > 0.5 -> "Good Tracking"
                        kneeAngleResult != null -> "Fair Tracking"
                        else -> "Acquiring Pose..."
                    },
                    color = when {
                        kneeAngleResult?.confidence ?: 0.0 > 0.7 -> Color(0xFF22C55E)
                        kneeAngleResult?.confidence ?: 0.0 > 0.5 -> Color(0xFFF59E0B)
                        kneeAngleResult != null -> Color(0xFFFF9800)
                        else -> Color.White.copy(alpha = 0.7f)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            // Progress Bar for auto-completion
            if (assessmentProgress > 0f) {
                LinearProgressIndicator(
                    progress = assessmentProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp, start = 80.dp, end = 80.dp),
                    color = Color(0xFF22C55E),
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}
