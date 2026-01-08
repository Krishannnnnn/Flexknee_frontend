package com.example.flexkneeai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import com.example.flexkneeai.ui.theme.*
import android.view.WindowManager
import androidx.compose.ui.graphics.Color

data class PerformanceRecord(
    val date: String,
    val rom: Int,
    val gait: String,
    val color: Color
)

class MainActivity : ComponentActivity() {
    override fun onResume() {
        super.onResume()
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        enableEdgeToEdge()
        setContent {
            FlexKneeAITheme {
                val currentScreen = remember { mutableStateOf("splash") }
                val previousScreen = remember { mutableStateOf("dashboard") }
                val selectedRole = remember { mutableStateOf("patient") }
                val lastRomAngle = remember { mutableStateOf(0) }
                val lastGaitStatus = remember { mutableStateOf("Normal") }
                val lastGaitDetail = remember { mutableStateOf("Symmetry looks good.") }
                val painLevel = remember { mutableStateOf(0) }
                val swellingLevel = remember { mutableStateOf("Low") }
                val hasCompletedBaseline = remember { mutableStateOf(false) }
                val selectedPatientDetails = remember { mutableStateOf<PatientDetails?>(null) }
                val selectedExerciseDetail = remember { mutableStateOf<ExerciseDetail?>(null) }
                val lastSessionReps = remember { mutableStateOf(0) }
                val lastSessionMaxRom = remember { mutableStateOf(0) }
                val lastSessionExerciseName = remember { mutableStateOf("") }

                val patients = remember {
                    mutableStateListOf(
                        PatientDetails("James Wilson", "ACL Tear", "J", 78, 12, 95, 2.4f, "On Track", 3, "Low", listOf(60, 65, 70, 78, 85, 92, 95)),
                        PatientDetails("Maria Garcia", "Meniscus Repair", "M", 78, 22, 92, 3.0f, "Needs Attention", 8, "High", listOf(85, 88, 90, 92, 92, 91, 92)),
                        PatientDetails("Robert Chen", "Post-Op Rehab", "R", 78, 19, 95, 2.9f, "Ahead", 2, "Low", listOf(70, 75, 82, 88, 92, 98, 105)),
                        PatientDetails("Sarah Miller", "Knee Bio-mechanics", "S", 78, 12, 95, 2.4f, "On Track", 4, "Medium", listOf(80, 82, 85, 85, 88, 90, 95))
                    )
                }

                val performanceHistory = remember {
                    mutableStateListOf(
                        PerformanceRecord("Oct 15", 60, "Severe Limp", Red500),
                        PerformanceRecord("Oct 20", 75, "Mild Limp", Amber500),
                        PerformanceRecord("Oct 25", 85, "Mild Limp", Amber500),
                        PerformanceRecord("Oct 30", 95, "Normal", Green500),
                        PerformanceRecord("Nov 05", 102, "Normal", Green500)
                    )
                }
                
                when (currentScreen.value) {
                    "splash" -> SplashScreen(
                        onAnimationComplete = {
                            currentScreen.value = "onboarding"
                        }
                    )
                    "onboarding" -> OnboardingScreen(
                        onFinish = {
                            currentScreen.value = "role_selection"
                        }
                    )
                    "role_selection" -> RoleSelectionScreen(
                        onRoleSelected = { role ->
                            selectedRole.value = role
                            currentScreen.value = "login"
                        }
                    )
                    "login" -> LoginScreen(
                        initialRole = selectedRole.value,
                        onBackClick = {
                            currentScreen.value = "role_selection"
                        },
                        onLoginClick = { role ->
                            selectedRole.value = role
                            if (role.equals("therapist", ignoreCase = true)) {
                                currentScreen.value = "therapist_dashboard"
                            } else if (hasCompletedBaseline.value) {
                                currentScreen.value = "dashboard"
                            } else {
                                currentScreen.value = "camera_permission"
                            }
                        },
                        onForgotPasswordClick = {
                            currentScreen.value = "forgot_password"
                        },
                        onCreateAccountClick = { role ->
                            selectedRole.value = role
                            currentScreen.value = "create_account"
                        }
                    )
                    "forgot_password" -> ForgotPasswordScreen(
                        onBackClick = {
                            currentScreen.value = "login"
                        }
                    )
                    "create_account" -> CreateAccountScreen(
                        role = selectedRole.value,
                        onBackClick = {
                            currentScreen.value = "login"
                        },
                        onSignInClick = {
                            currentScreen.value = "login"
                        },
                        onCreateAccountClick = {
                            if (selectedRole.value.equals("therapist", ignoreCase = true)) {
                                currentScreen.value = "therapist_dashboard"
                            } else if (hasCompletedBaseline.value) {
                                currentScreen.value = "dashboard"
                            } else {
                                currentScreen.value = "camera_permission"
                            }
                        }
                    )
                    "camera_permission" -> CameraPermissionScreen(
                        onPermissionGranted = {
                            currentScreen.value = "baseline_assessment"
                        },
                        onBackClick = {
                            currentScreen.value = "login"
                        }
                    )
                    "setup_guide" -> SetupGuideScreen(
                        onDismissRequest = {
                            currentScreen.value = "camera_permission" 
                        },
                        onReadyClick = {
                            currentScreen.value = "camera_analysis"
                        }
                    )
                    "camera_analysis" -> CameraAnalysisScreen(
                        onBackClick = {
                            currentScreen.value = "baseline_assessment"
                        },
                        onAssessmentComplete = { angle ->
                            lastRomAngle.value = angle
                            currentScreen.value = "gait_analysis"
                        }
                    )
                    "gait_analysis" -> GaitAnalysisScreen(
                        onBackClick = {
                            currentScreen.value = "camera_analysis"
                        },
                        onAnalysisComplete = { status, detail ->
                            lastGaitStatus.value = status
                            lastGaitDetail.value = detail
                            val color = when {
                                status.contains("Severe") -> Red500
                                status.contains("Mild") -> Amber500
                                else -> Green500
                            }
                            // Add today's record (using mock date for now or "Today")
                            val nextDate = getNextMockDate(performanceHistory.last().date)
                            performanceHistory.add(PerformanceRecord(nextDate, lastRomAngle.value, status, color))
                            currentScreen.value = "assessment_results"
                        }
                    )
                    "assessment_results" -> AssessmentResultsScreen(
                        romAngle = lastRomAngle.value,
                        gaitStatus = lastGaitStatus.value,
                        gaitDetail = lastGaitDetail.value,
                        onContinueClick = {
                            hasCompletedBaseline.value = true
                            currentScreen.value = "dashboard"
                        }
                    )
                    "dashboard" -> {
                        DashboardScreen(
                            userName = "John",
                            romValue = "${lastRomAngle.value}°", 
                            painValue = "${painLevel.value}/10",
                            currentRom = lastRomAngle.value,
                            painAvg = painLevel.value.toFloat(),
                            currentPain = painLevel.value,
                            currentSwelling = swellingLevel.value,
                            performanceHistory = performanceHistory,
                            exerciseCount = "0/3",
                            recoveryDay = 1,
                            onSaveLog = { pain, swelling ->
                                painLevel.value = pain
                                swellingLevel.value = swelling
                            },
                            onNotificationsClick = {
                                currentScreen.value = "notifications"
                            },
                            onProfileClick = {
                                currentScreen.value = "profile"
                            },
                            onStartExercises = {
                                currentScreen.value = "exercise_plan"
                            },
                            onWeeklyAssessment = {
                                currentScreen.value = "baseline_assessment"
                            }
                        )
                    }
                    "therapist_dashboard" -> TherapistDashboardScreen(
                        patients = patients,
                        onProfileClick = {
                            currentScreen.value = "profile"
                        },
                        onPatientClick = { patientName ->
                            selectedPatientDetails.value = patients.find { it.name == patientName }
                            if (selectedPatientDetails.value != null) {
                                currentScreen.value = "patient_details"
                            }
                        },
                        onViewAllClick = {
                            currentScreen.value = "active_patients"
                        },
                        onNeedReviewClick = {
                            currentScreen.value = "need_review"
                        },
                        onSignOutClick = {
                            currentScreen.value = "login"
                        }
                    )
                    "patient_details" -> {
                        selectedPatientDetails.value?.let { patient ->
                            PatientDetailsScreen(
                                patient = patient,
                                onBackClick = {
                                    currentScreen.value = "therapist_dashboard"
                                },
                                onProfileClick = {
                                    currentScreen.value = "profile"
                                },
                                onMessageClick = {
                                    currentScreen.value = "chat_screen"
                                },
                                onViewAiReportClick = {
                                    currentScreen.value = "ai_report"
                                },
                                onModifyPlanClick = {
                                    currentScreen.value = "modify_plan"
                                },
                                onAlertsClick = {
                                    currentScreen.value = "alerts"
                                }
                            )
                        }
                    }
                    "modify_plan" -> {
                        ModifyPlanScreen(
                            onBackClick = {
                                currentScreen.value = "patient_details"
                            },
                            onSaveClick = {
                                currentScreen.value = "patient_details"
                            },
                            onProfileClick = {
                                currentScreen.value = "profile"
                            }
                        )
                    }
                    "active_patients" -> {
                        ActivePatientsScreen(
                            patients = patients,
                            onBackClick = {
                                currentScreen.value = "therapist_dashboard"
                            },
                            onPatientClick = { patientName ->
                                selectedPatientDetails.value = patients.find { it.name == patientName }
                                if (selectedPatientDetails.value != null) {
                                    currentScreen.value = "patient_details"
                                }
                            }
                        )
                    }
                    "need_review" -> {
                        NeedReviewScreen(
                            patients = patients,
                            onBackClick = {
                                currentScreen.value = "therapist_dashboard"
                            },
                            onReviewPatient = { patientName ->
                                selectedPatientDetails.value = patients.find { it.name == patientName }
                                if (selectedPatientDetails.value != null) {
                                    currentScreen.value = "patient_details"
                                }
                            }
                        )
                    }
                    "ai_report" -> {
                        selectedPatientDetails.value?.let { patient ->
                            AiReportScreen(
                                patient = patient,
                                onBackClick = {
                                    currentScreen.value = "patient_details"
                                }
                            )
                        }
                    }
                    "chat_screen" -> ChatScreen(
                        chatPartner = if (selectedRole.value == "therapist") {
                            selectedPatientDetails.value?.name ?: "Patient"
                        } else {
                            "Dr. Chen"
                        },
                        isTherapist = (selectedRole.value == "therapist"),
                        onBackClick = {
                            if (selectedRole.value == "therapist") {
                                currentScreen.value = "patient_details"
                            } else {
                                currentScreen.value = "dashboard"
                            }
                        }
                    )
                    "exercise_plan" -> ExercisePlanScreen(
                        onBackClick = {
                            currentScreen.value = "dashboard"
                        },
                        onProfileClick = {
                            currentScreen.value = "profile"
                        },
                        onStartExercise = { exerciseName ->
                            if (exerciseName == "All") {
                                // Default to first exercise or a sequence
                                selectedExerciseDetail.value = exerciseDetailsList["Heel Slides"]
                            } else {
                                selectedExerciseDetail.value = exerciseDetailsList[exerciseName]
                            }
                            currentScreen.value = "exercise_detail"
                        }
                    )
                    "exercise_detail" -> {
                        selectedExerciseDetail.value?.let { detail ->
                            ExerciseDetailScreen(
                                exercise = detail,
                                onBackClick = {
                                    currentScreen.value = "exercise_plan"
                                },
                                onProfileClick = {
                                    currentScreen.value = "profile"
                                },
                                onStartCamera = {
                                    currentScreen.value = "exercise_analysis"
                                }
                            )
                        }
                    }
                    "exercise_analysis" -> {
                        selectedExerciseDetail.value?.let { exercise ->
                            ExerciseAnalysisScreen(
                                exercise = exercise,
                                onBackClick = {
                                    currentScreen.value = "exercise_detail"
                                },
                                onExerciseComplete = { reps, maxAngle ->
                                    lastSessionReps.value = reps
                                    lastSessionMaxRom.value = maxAngle
                                    lastSessionExerciseName.value = exercise.title
                                    lastRomAngle.value = maxAngle
                                    
                                    // Add to performance history
                                    val nextDate = if (performanceHistory.isNotEmpty()) getNextMockDate(performanceHistory.last().date) else "Today"
                                    performanceHistory.add(PerformanceRecord(nextDate, maxAngle, "Normal", Green500))
                                    
                                    currentScreen.value = "session_results"
                                }
                            )
                        }
                    }
                    "session_results" -> {
                        ExerciseSessionResultsScreen(
                            exerciseName = lastSessionExerciseName.value,
                            reps = lastSessionReps.value,
                            maxRom = lastSessionMaxRom.value,
                            gaitStatus = lastGaitStatus.value,
                            onLogPainClick = {
                                currentScreen.value = "daily_checkin"
                            },
                            onBackHomeClick = {
                                currentScreen.value = "dashboard"
                            }
                        )
                    }
                    "patient_logs" -> PatientLogsScreen(
                        onBackClick = {
                            currentScreen.value = "therapist_dashboard"
                        }
                    )
                    "baseline_assessment" -> BaselineAssessmentScreen(
                        onBeginAssessmentClick = {
                            currentScreen.value = "camera_analysis"
                        }
                    )
                    "notifications" -> NotificationsScreen(
                        onBackClick = {
                            if (selectedRole.value.equals("therapist", ignoreCase = true)) {
                                currentScreen.value = "therapist_dashboard"
                            } else {
                                currentScreen.value = "dashboard"
                            }
                        },
                        onProfileClick = {
                            currentScreen.value = "profile"
                        }
                    )
                    "alerts" -> {
                        AlertsScreen(
                            patients = patients,
                            onBackClick = {
                                currentScreen.value = "therapist_dashboard"
                            },
                            onViewPatient = { patientName ->
                                selectedPatientDetails.value = patients.find { it.name == patientName }
                                if (selectedPatientDetails.value != null) {
                                    currentScreen.value = "patient_details"
                                }
                            },
                            onProfileClick = {
                                currentScreen.value = "profile"
                            }
                        )
                    }
                    "profile" -> {
                        if (selectedRole.value == "therapist") {
                            TherapistProfileScreen(
                                onBackClick = {
                                    currentScreen.value = "therapist_dashboard"
                                },
                                onSignOutClick = {
                                    currentScreen.value = "login"
                                }
                            )
                        } else {
                            ProfileScreen(
                                onBackClick = {
                                    currentScreen.value = "dashboard"
                                },
                                onSignOutClick = {
                                    currentScreen.value = "login"
                                },
                                onChangePasswordClick = {
                                    currentScreen.value = "change_password"
                                },
                                onViewProgressClick = {
                                    previousScreen.value = "profile"
                                    currentScreen.value = "my_progress"
                                },
                                onEditInformationClick = {
                                    currentScreen.value = "edit_information"
                                }
                            )
                        }
                    }
                    "my_progress" -> {
                         MyProgressScreen(
                            currentRom = lastRomAngle.value,
                            painAvg = painLevel.value.toFloat(),
                            history = performanceHistory,
                            onBackClick = {
                                currentScreen.value = previousScreen.value
                            }
                        )
                    }
                    "daily_checkin" -> DailyCheckInScreen(
                        currentPain = painLevel.value,
                        currentSwelling = swellingLevel.value,
                        onSaveLog = { pain, swelling ->
                            painLevel.value = pain
                            swellingLevel.value = swelling
                            currentScreen.value = "dashboard"
                        },
                        onBackClick = {
                            currentScreen.value = "dashboard"
                        }
                    )
                    "change_password" -> ChangePasswordScreen(
                        onBackClick = {
                            currentScreen.value = "profile"
                        }
                    )
                    "edit_information" -> EditInformationScreen(
                        onBackClick = {
                            currentScreen.value = "profile"
                        }
                    )
                }
            }
        }
    }

    private fun getNextMockDate(lastDate: String): String {
        // Simple mock logic to increment date
        // Expected format "Mmm dd" e.g. "Nov 05"
        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        try {
            val parts = lastDate.split(" ")
            if (parts.size != 2) return "Next Day"
            
            val monthStr = parts[0]
            val day = parts[1].toInt()
            
            var monthIndex = months.indexOf(monthStr)
            var nextDay = day + 1
            
            // Simplified max days check
            if (nextDay > 30) {
                nextDay = 1
                monthIndex = (monthIndex + 1) % 12
            }
            
            return "${months[monthIndex]} ${nextDay.toString().padStart(2, '0')}"
        } catch (e: Exception) {
            return "Next Day"
        }
    }
}