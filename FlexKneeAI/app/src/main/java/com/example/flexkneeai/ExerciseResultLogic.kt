package com.example.flexkneeai

fun generateAiSuggestion(exerciseName: String, reps: Int, rom: Int, gaitStatus: String): String {
    val basicSuggestions = listOf(
        "Excellent form! Consistency is the key to recovery.",
        "Great effort today. Make sure to stay hydrated.",
        "Your movement looks steady. Keep up the good work!"
    )

    return when {
        rom < 90 && exerciseName == "Heel Slides" -> 
            "Focus on sliding your heel a bit further each time to improve flexion."
        rom > 110 -> 
            "Incredible range of motion! You're ahead of the curve."
        reps < 5 -> 
            "Starting slow is perfectly fine. Aim for 2 more reps in the next session."
        gaitStatus.contains("Limp", ignoreCase = true) ->
            "Your gait shows a slight limp. Try to focus on even weight distribution during walks."
        else -> basicSuggestions.random()
    }
}
