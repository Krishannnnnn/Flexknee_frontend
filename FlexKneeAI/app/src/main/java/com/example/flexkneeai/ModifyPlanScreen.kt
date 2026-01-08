package com.example.flexkneeai

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flexkneeai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyPlanScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onProfileClick: () -> Unit = {}
) {
    var showAddExercise by remember { mutableStateOf(false) }
    
    // Initial plan based on the screenshot
    val initialPlan = remember {
        mutableStateListOf(
            PlanItem("Heel Slides", "10 reps", "3"),
            PlanItem("Quad Sets", "15 reps", "3"),
            PlanItem("Straight Leg Raises", "12 reps", "3")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Modify Plan",
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Slate900
                        )
                    }
                },
                actions = {
                    Row(modifier = Modifier.padding(end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = Slate500,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        IconButton(onClick = onProfileClick) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                color = Slate100
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = "Profile",
                                    tint = Slate500,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFFBFDFF),
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                color = Color.Transparent
            ) {
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue500)
                ) {
                    Text(
                        "Save Changes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // AI Recommendation Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                color = Color(0xFFEDF5FF),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "AI Recommendation",
                        color = Color(0xFF1E40AF), // Darker blue
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Patient is finding \"Straight Leg Raises\" difficult. Consider reducing reps or swapping for \"Quad Sets\".",
                        color = Color(0xFF1E40AF),
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current Exercises",
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        fontSize = 18.sp
                    )
                    TextButton(onClick = { showAddExercise = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Blue500, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Exercise", color = Blue500, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                initialPlan.forEachIndexed { index, item ->
                    PlanItemCard(
                        item = item,
                        onRemove = { initialPlan.removeAt(index) },
                        onUpdateReps = { newValue -> initialPlan[index] = item.copy(reps = newValue) },
                        onUpdateSets = { newValue -> initialPlan[index] = item.copy(sets = newValue) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showAddExercise) {
        AlertDialog(
            onDismissRequest = { showAddExercise = false },
            title = { Text("Add Exercise", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    exerciseDetailsList.values.forEach { exercise ->
                        val isAlreadyAdded = initialPlan.any { it.title == exercise.title }
                        
                        Surface(
                            onClick = {
                                if (!isAlreadyAdded) {
                                    initialPlan.add(
                                        PlanItem(
                                            exercise.title,
                                            exercise.targetReps,
                                            exercise.sets.filter { it.isDigit() }
                                        )
                                    )
                                    showAddExercise = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            color = if (isAlreadyAdded) Slate50 else Color.White,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Blue50
                                ) {
                                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Blue500, modifier = Modifier.padding(8.dp))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(text = exercise.title, fontWeight = FontWeight.Bold, color = if (isAlreadyAdded) Slate400 else Slate900)
                                    Text(text = "${exercise.difficulty} • ${exercise.duration}", color = Slate400, fontSize = 12.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddExercise = false }) {
                    Text("Close")
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}

data class PlanItem(
    val title: String,
    val reps: String,
    val sets: String
)

@Composable
fun PlanItemCard(
    item: PlanItem,
    onRemove: () -> Unit,
    onUpdateReps: (String) -> Unit,
    onUpdateSets: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate100)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DragIndicator,
                contentDescription = null,
                tint = Slate300,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))

            // Placeholder for exercise image (circular/rounded)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Slate50)
            ) {
                // In a real app, use Image with painterResource
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        fontSize = 16.sp
                    )
                    IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red.copy(alpha = 0.6f))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    EditableField(
                        label = "Reps",
                        value = item.reps,
                        onValueChange = onUpdateReps,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    EditableField(
                        label = "Sets",
                        value = item.sets,
                        onValueChange = onUpdateSets,
                        modifier = Modifier.weight(0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun EditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = label, color = Slate400, fontSize = 12.sp)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = Slate900,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Slate100)
        )
    }
}
