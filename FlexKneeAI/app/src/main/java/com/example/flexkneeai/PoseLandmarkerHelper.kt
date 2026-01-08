package com.example.flexkneeai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

class PoseLandmarkerHelper(
    var minPoseDetectionConfidence: Float = DEFAULT_POSE_DETECTION_CONFIDENCE,
    var minPoseTrackingConfidence: Float = DEFAULT_POSE_TRACKING_CONFIDENCE,
    var minPosePresenceConfidence: Float = DEFAULT_POSE_PRESENCE_CONFIDENCE,
    var currentDelegate: Int = DELEGATE_CPU,
    var runningMode: RunningMode = RunningMode.LIVE_STREAM,
    val context: Context,
    val poseLandmarkerHelperListener: LandmarkerListener? = null
) {

    private var poseLandmarker: PoseLandmarker? = null

    init {
        setupPoseLandmarker()
    }

    fun clearPoseLandmarker() {
        poseLandmarker?.close()
        poseLandmarker = null
    }

    fun setupPoseLandmarker() {
        val baseOptionsBuilder = BaseOptions.builder()

        when (currentDelegate) {
            DELEGATE_CPU -> {
                baseOptionsBuilder.setDelegate(Delegate.CPU)
            }
            DELEGATE_GPU -> {
                baseOptionsBuilder.setDelegate(Delegate.GPU)
            }
        }

        baseOptionsBuilder.setModelAssetPath(MP_POSE_LANDMARKER_TASK)

        try {
            val baseOptions = baseOptionsBuilder.build()
            val optionsBuilder = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinPoseDetectionConfidence(minPoseDetectionConfidence)
                .setMinTrackingConfidence(minPoseTrackingConfidence)
                .setMinPosePresenceConfidence(minPosePresenceConfidence)
                .setRunningMode(runningMode)

            if (runningMode == RunningMode.LIVE_STREAM) {
                optionsBuilder.setResultListener(this::returnLivestreamResult)
                optionsBuilder.setErrorListener(this::returnLivestreamError)
            }

            val options = optionsBuilder.build()
            poseLandmarker = PoseLandmarker.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            poseLandmarkerHelperListener?.onError(
                "Pose Landmarker failed to initialize. See error logs for details"
            )
            Log.e("PoseLandmarkerHelper", "MediaPipe failed to load model with error: " + e.message)
        } catch (e: RuntimeException) {
            poseLandmarkerHelperListener?.onError(
                "Pose Landmarker failed to initialize. See error logs for details",
                GPU_ERROR
            )
            Log.e("PoseLandmarkerHelper", "MediaPipe failed to load model with error: " + e.message)
        }
    }

    fun detectLiveStream(imageProxy: ImageProxy, isFrontCamera: Boolean) {
        if (runningMode != RunningMode.LIVE_STREAM) {
            throw IllegalArgumentException(
                "Attempting to call detectLiveStream while not in LIVE_STREAM mode"
            )
        }
        val frameTime = SystemClock.uptimeMillis()

        // Copy out RGB bits from the frame to a specific buffer.
        val bitmapBuffer = Bitmap.createBitmap(
            imageProxy.width,
            imageProxy.height,
            Bitmap.Config.ARGB_8888
        )
        imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
        imageProxy.close()

        // Rotate the bitmap to upright
        val matrix = Matrix().apply {
            // CameraX images coming from the preview are often rotated 90 degrees.
            // We need to account for the imageProxy.imageInfo.rotationDegrees.
            // However, since we used copyPixelsFromBuffer on raw planes, 
            // the bitmap is in the sensor orientation.
            // Wait, copying raw buffer to bitmap blindly might not work correctly for different formats (YUV vs RGBA).
            // CameraX ImageAnalysis usually outputs YUV_420_888 by default.
            // We should configure ImageAnalysis to OUTPUT_IMAGE_FORMAT_RGBA_8888 to easily convert to Bitmap.
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

            // Flip if front camera
            if (isFrontCamera) {
                postScale(
                    -1f,
                    1f,
                    imageProxy.width.toFloat(),
                    imageProxy.height.toFloat()
                )
            }
        }
        
        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height, matrix, true
        )

        val mpImage = BitmapImageBuilder(rotatedBitmap).build()

        detectAsync(mpImage, frameTime)
    }

    @VisibleForTesting
    fun detectAsync(mpImage: MPImage, frameTime: Long) {
        poseLandmarker?.detectAsync(mpImage, frameTime)
    }

    private fun returnLivestreamResult(
        result: PoseLandmarkerResult,
        input: MPImage
    ) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - result.timestampMs()

        poseLandmarkerHelperListener?.onResults(
            ResultBundle(
                results = listOf(result),
                worldLandmarks = result.worldLandmarks(),
                inferenceTime = inferenceTime,
                inputImageHeight = input.height,
                inputImageWidth = input.width
            )
        )
    }

    private fun returnLivestreamError(error: RuntimeException) {
        poseLandmarkerHelperListener?.onError(error.message ?: "An unknown error has occurred")
    }

    companion object {
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DEFAULT_POSE_DETECTION_CONFIDENCE = 0.6F
        const val DEFAULT_POSE_TRACKING_CONFIDENCE = 0.6F
        const val DEFAULT_POSE_PRESENCE_CONFIDENCE = 0.6F
        const val OTHER_ERROR = 0
        const val GPU_ERROR = 1
        const val MP_POSE_LANDMARKER_TASK = "pose_landmarker_full.task"

        /**
         * Calculates the angle between three points.
         * @param first Point A (e.g. Hip)
         * @param mid Point B (e.g. Knee - the vertex)
         * @param last Point C (e.g. Ankle)
         * @return Angle in degrees
         */
        /**
         * Calculates the angle between three points in 2D or 3D.
         * For 3D: Uses dot product to find angle between vectors mid->first and mid->last.
         */
        fun calculate3DAngle(
            first: com.google.mediapipe.tasks.components.containers.Landmark,
            mid: com.google.mediapipe.tasks.components.containers.Landmark,
            last: com.google.mediapipe.tasks.components.containers.Landmark
        ): Double {
            val v1x = first.x() - mid.x()
            val v1y = first.y() - mid.y()
            val v1z = first.z() - mid.z()
            
            val v2x = last.x() - mid.x()
            val v2y = last.y() - mid.y()
            val v2z = last.z() - mid.z()
            
            val dot = v1x * v2x + v1y * v2y + v1z * v2z
            val mag1 = Math.sqrt((v1x * v1x + v1y * v1y + v1z * v1z).toDouble())
            val mag2 = Math.sqrt((v2x * v2x + v2y * v2y + v2z * v2z).toDouble())
            
            val cosTheta = dot / (mag1 * mag2)
            // Clamp to [-1, 1] to avoid NaN due to floating point errors
            val angle = Math.toDegrees(Math.acos(cosTheta.coerceIn(-1.0, 1.0)))
            return angle
        }

        fun calculateAngle(
            first: com.google.mediapipe.tasks.components.containers.NormalizedLandmark,
            mid: com.google.mediapipe.tasks.components.containers.NormalizedLandmark,
            last: com.google.mediapipe.tasks.components.containers.NormalizedLandmark
        ): Double {
            val result = Math.toDegrees(
                (Math.atan2((last.y() - mid.y()).toDouble(), (last.x() - mid.x()).toDouble())
                        - Math.atan2((first.y() - mid.y()).toDouble(), (first.x() - mid.x()).toDouble()))
            )
            var angle = Math.abs(result)
            if (angle > 180) {
                angle = 360.0 - angle
            }
            return angle
        }

        // State for smoothing (static/singleton pattern or passed in)
        private var lastPrimaryAngle: Double? = null
        private const val SMOOTHING_FACTOR = 0.25 // EMA factor (lower = smoother)

        fun calculateEnhancedKneeAngles(resultBundle: ResultBundle): KneeAngleResult {
            val landmarks = resultBundle.results.firstOrNull()?.landmarks()?.firstOrNull() ?: return KneeAngleResult()
            val worldLandmarks = resultBundle.worldLandmarks.firstOrNull()

            val leftHip = landmarks.getOrNull(23)
            val leftKnee = landmarks.getOrNull(25)
            val leftAnkle = landmarks.getOrNull(27)

            val rightHip = landmarks.getOrNull(24)
            val rightKnee = landmarks.getOrNull(26)
            val rightAnkle = landmarks.getOrNull(28)

            // Prefer 3D World Landmarks for better accuracy if available
            val leftAngle = if (worldLandmarks != null) {
                val wHip = worldLandmarks.getOrNull(23)
                val wKnee = worldLandmarks.getOrNull(25)
                val wAnkle = worldLandmarks.getOrNull(27)
                if (wHip != null && wKnee != null && wAnkle != null) {
                    calculate3DAngle(wHip, wKnee, wAnkle)
                } else null
            } else if (leftHip != null && leftKnee != null && leftAnkle != null) {
                calculateAngle(leftHip, leftKnee, leftAnkle)
            } else null

            val rightAngle = if (worldLandmarks != null) {
                val wHip = worldLandmarks.getOrNull(24)
                val wKnee = worldLandmarks.getOrNull(26)
                val wAnkle = worldLandmarks.getOrNull(28)
                if (wHip != null && wKnee != null && wAnkle != null) {
                    calculate3DAngle(wHip, wKnee, wAnkle)
                } else null
            } else if (rightHip != null && rightKnee != null && rightAnkle != null) {
                calculateAngle(rightHip, rightKnee, rightAnkle)
            } else null

            val leftConfidence = calculateLandmarkConfidence(leftHip, leftKnee, leftAnkle)
            val rightConfidence = calculateLandmarkConfidence(rightHip, rightKnee, rightAnkle)

            var (primaryAngle, side, confidence) = when {
                leftAngle != null && rightAngle != null -> {
                    // Improve side-profile selection: prioritize the leg closer to the camera (Z-axis) 
                    // and with higher tracking confidence
                    val leftZ = Math.abs(leftHip!!.z() + leftKnee!!.z() + leftAnkle!!.z())
                    val rightZ = Math.abs(rightHip!!.z() + rightKnee!!.z() + rightAnkle!!.z())
                    
                    if (leftConfidence > rightConfidence + 0.1 || (Math.abs(leftConfidence - rightConfidence) < 0.1 && leftZ < rightZ)) {
                        Triple(leftAngle, "Left", leftConfidence)
                    } else {
                        Triple(rightAngle, "Right", rightConfidence)
                    }
                }
                leftAngle != null -> Triple(leftAngle, "Left", leftConfidence)
                rightAngle != null -> Triple(rightAngle, "Right", rightConfidence)
                else -> Triple(null, "Unknown", 0.0)
            }

            // Apply EMA Smoothing with Dead-zone
            if (primaryAngle != null) {
                val currentLast = lastPrimaryAngle
                if (currentLast == null) {
                    lastPrimaryAngle = primaryAngle
                } else {
                    // Jitter reduction: only update if change is significant (> 0.5 degrees) OR if it's a trend
                    val diff = Math.abs(primaryAngle - currentLast)
                    if (diff > 0.5) {
                        val smoothed = (primaryAngle * SMOOTHING_FACTOR) + (currentLast * (1.0 - SMOOTHING_FACTOR))
                        lastPrimaryAngle = smoothed
                        primaryAngle = smoothed
                    } else {
                        primaryAngle = currentLast
                    }
                }
            }

            return KneeAngleResult(
                leftAngle = leftAngle,
                rightAngle = rightAngle,
                primaryAngle = primaryAngle,
                confidence = confidence,
                side = side
            )
        }

        fun calculateLandmarkConfidence(
            hip: com.google.mediapipe.tasks.components.containers.NormalizedLandmark?,
            knee: com.google.mediapipe.tasks.components.containers.NormalizedLandmark?,
            ankle: com.google.mediapipe.tasks.components.containers.NormalizedLandmark?
        ): Double {
            if (hip == null || knee == null || ankle == null) return 0.0
            
            // Base confidence on Visibility and Presence from MediaPipe
            val avgVisibility = (hip.visibility().orElse(0f) + knee.visibility().orElse(0f) + ankle.visibility().orElse(0f)) / 3.0
            val avgPresence = (hip.presence().orElse(0f) + knee.presence().orElse(0f) + ankle.presence().orElse(0f)) / 3.0
            
            var confidence = (avgVisibility + avgPresence) / 2.0
            
            // Extreme depth check (Z-axis outliers often cause jitter)
            val avgZ = Math.abs(hip.z() + knee.z() + ankle.z()) / 3.0
            if (avgZ > 1.5) { // Landmarks too far forward/back from the body plane
                confidence *= 0.85
            }
            
            // Geometric validation (2D space normalized)
            val kneeToHipDist = Math.hypot((hip.x() - knee.x()).toDouble(), (hip.y() - knee.y()).toDouble())
            val kneeToAnkleDist = Math.hypot((knee.x() - ankle.x()).toDouble(), (knee.y() - ankle.y()).toDouble())
            val hipToAnkleDist = Math.hypot((hip.x() - ankle.x()).toDouble(), (hip.y() - ankle.y()).toDouble())
            
            // Triangle inequality check (allow some slack for bent knee)
            if (kneeToHipDist + kneeToAnkleDist < hipToAnkleDist * 0.95) {
                confidence *= 0.75
            }
            
            // Anatomical ratio check (thigh vs shin)
            val ratio = if (kneeToAnkleDist > 0) kneeToHipDist / kneeToAnkleDist else 0.0
            if (ratio < 0.4 || ratio > 2.5) { // Wider range for flexed poses but still constrained
                confidence *= 0.7
            }
            
            return confidence.coerceIn(0.0, 1.0)
        }
    }

    data class KneeAngleResult(
        val leftAngle: Double? = null,
        val rightAngle: Double? = null,
        val primaryAngle: Double? = null,
        val confidence: Double = 0.0,
        val side: String = "Unknown"
    )

    data class ResultBundle(
        val results: List<PoseLandmarkerResult>,
        val worldLandmarks: List<List<com.google.mediapipe.tasks.components.containers.Landmark>>,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
    )

    interface LandmarkerListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
        fun onResults(resultBundle: ResultBundle)
    }

    class RepetitionCounter {
        private enum class State { START, BENT }
        private var currentState = State.START
        private var repetitionCount = 0
        private var lastRepetitionTime = 0L
        private val angleWindow = java.util.ArrayDeque<Double>()
        private val SMOOTHING_WINDOW_SIZE = 5
        private val THRESHOLD_START = 155.0
        private val THRESHOLD_BENT = 100.0
        private val MIN_REP_TIME_MS = 500

        fun processAngle(
            angle: Double, 
            timestampMs: Long,
            thresholdStart: Double = THRESHOLD_START,
            thresholdBent: Double = THRESHOLD_BENT
        ): Int {
            // Smoothing
            angleWindow.add(angle)
            if (angleWindow.size > SMOOTHING_WINDOW_SIZE) {
                angleWindow.removeFirst()
            }
            val smoothAngle = angleWindow.average()

            when (currentState) {
                State.START -> {
                    if (smoothAngle <= thresholdBent) {
                        currentState = State.BENT
                    }
                }
                State.BENT -> {
                    if (smoothAngle >= thresholdStart) {
                        if ((timestampMs - lastRepetitionTime) > MIN_REP_TIME_MS) {
                            repetitionCount++
                            lastRepetitionTime = timestampMs
                            currentState = State.START
                        }
                    }
                }
            }
            return repetitionCount
        }

        fun reset() {
            repetitionCount = 0
            currentState = State.START
            angleWindow.clear()
            lastRepetitionTime = 0L
        }
    }
}
