package com.example.flexkneeai

import android.app.DatePickerDialog
import android.net.Uri
import java.util.Calendar
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun CreateAccountScreen(
    role: String = "patient",
    onBackClick: () -> Unit,
    onSignInClick: () -> Unit,
    onCreateAccountClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC) // Slate 50
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Back Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF0F172A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title section
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    fontSize = 28.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (role == "patient") "Start your personalized recovery plan." else "Join our network of physiotherapy experts.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF64748B),
                    fontSize = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (role == "patient") {
                PatientRegistrationForm(onCreateAccountClick)
            } else {
                TherapistRegistrationForm(onCreateAccountClick)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign In Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "Sign in",
                    color = Color(0xFF3B82F6),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onSignInClick() }
                )
            }
        }
    }
}

@Composable
fun PatientRegistrationForm(onCreateClick: () -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var surgeryDate by remember { mutableStateOf("") }
    var selectedKnee by remember { mutableStateOf<String?>(null) } // "Left" or "Right"
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            surgeryDate = "${selectedMonth + 1}/$selectedDay/$selectedYear"
        },
        year, month, day
    )

    Column {
        InputField(label = "Full Name", value = fullName, onValueChange = { fullName = it }, placeholder = "John Doe")
        Spacer(modifier = Modifier.height(16.dp))
        InputField(label = "Email Address", value = email, onValueChange = { email = it }, placeholder = "you@example.com", keyboardType = KeyboardType.Email)
        Spacer(modifier = Modifier.height(16.dp))
        InputField(label = "Password", value = password, onValueChange = { password = it }, placeholder = "Create a password", isPassword = true)
        Spacer(modifier = Modifier.height(16.dp))

        // Surgery Date
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Surgery Date",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155)
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = surgeryDate,
                onValueChange = {}, // ReadOnly
                readOnly = true,
                placeholder = { Text("mm/dd/yyyy", color = Color(0xFF94A3B8)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC),
                    focusedIndicatorColor = Color(0xFF3B82F6),
                    unfocusedIndicatorColor = Color(0xFFE2E8F0)
                ),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.DateRange, "Select Date", tint = Color(0xFF64748B))
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Knee Selection
        Text(
            text = "Which knee was operated?",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KneeSelectionCard(
                modifier = Modifier.weight(1f),
                title = "Left Knee",
                subtitle = "Operated Leg",
                isSelected = selectedKnee == "Left",
                onClick = { selectedKnee = "Left" }
            )
            KneeSelectionCard(
                modifier = Modifier.weight(1f),
                title = "Right Knee",
                subtitle = "Operated Leg",
                isSelected = selectedKnee == "Right",
                onClick = { selectedKnee = "Right" }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        // Error message
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        CreateAccountButton(
            onClick = {
                when {
                    fullName.isBlank() -> errorMessage = "Please enter your full name"
                    email.isBlank() -> errorMessage = "Please enter your email"
                    password.isBlank() -> errorMessage = "Please enter a password"
                    password.length < 6 -> errorMessage = "Password must be at least 6 characters"
                    else -> {
                        onCreateClick()
                    }
                }
            },
            isLoading = isLoading
        )
    }
}

@Composable
fun TherapistRegistrationForm(onCreateClick: () -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var regNumber by remember { mutableStateOf("") }
    var governingCouncil by remember { mutableStateOf("") }
    var yearsExperience by remember { mutableStateOf("") }
    var clinicName by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val coroutineScope = rememberCoroutineScope()

    var uploadedFileName by remember { mutableStateOf<String?>(null) }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // In a real app, you'd get the actual file name from ContentResolver
            // For now, we'll just show a success message or generic name
            uploadedFileName = "License_Proof.pdf" // Placeholder name or extract from URI
        }
    }

    Column {
        InputField(label = "Full Name", value = fullName, onValueChange = { fullName = it }, placeholder = "Dr. Jane Smith")
        Spacer(modifier = Modifier.height(16.dp))
        InputField(label = "Email ID (Login)", value = email, onValueChange = { email = it }, placeholder = "doctor@example.com", keyboardType = KeyboardType.Email)
        Spacer(modifier = Modifier.height(16.dp))
        InputField(label = "Mobile Number", value = mobile, onValueChange = { mobile = it }, placeholder = "+1 234 567 8900", keyboardType = KeyboardType.Phone)
        Spacer(modifier = Modifier.height(16.dp))
        InputField(label = "Password", value = password, onValueChange = { password = it }, placeholder = "Create a password", isPassword = true)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Professional Details", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)))
        Spacer(modifier = Modifier.height(16.dp))

        InputField(label = "Physiotherapy Registration Number", value = regNumber, onValueChange = { regNumber = it }, placeholder = "REG-12345")
        Spacer(modifier = Modifier.height(16.dp))
        InputField(label = "Governing Council / Registration Authority", value = governingCouncil, onValueChange = { governingCouncil = it }, placeholder = "State Council")
        Spacer(modifier = Modifier.height(16.dp))
        InputField(label = "Years of Experience", value = yearsExperience, onValueChange = { yearsExperience = it }, placeholder = "5", keyboardType = KeyboardType.Number)
        Spacer(modifier = Modifier.height(16.dp))
        InputField(label = "Clinic / Hospital Name", value = clinicName, onValueChange = { clinicName = it }, placeholder = "City General Hospital")
        Spacer(modifier = Modifier.height(16.dp))
        InputField(label = "City", value = city, onValueChange = { city = it }, placeholder = "New York")
        Spacer(modifier = Modifier.height(24.dp))

        // Upload Proof
        Text(
            text = "Upload License / Registration Proof",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                .background(Color.White, RoundedCornerShape(12.dp))
                .clickable { launcher.launch("*/*") } // Launch file picker
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = null,
                    tint = if (uploadedFileName != null) Color(0xFF3B82F6) else Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = uploadedFileName ?: "Select file to upload...",
                    color = if (uploadedFileName != null) Color(0xFF0F172A) else Color(0xFF94A3B8)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Terms
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = termsAccepted,
                onCheckedChange = { termsAccepted = it },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF3B82F6))
            )
            Text(
                text = "I accept the Terms & Privacy Policy",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF64748B))
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        // Error message
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        CreateAccountButton(
            onClick = {
                when {
                    fullName.isBlank() -> errorMessage = "Please enter your full name"
                    email.isBlank() -> errorMessage = "Please enter your email"
                    mobile.isBlank() -> errorMessage = "Please enter your mobile number"
                    password.isBlank() -> errorMessage = "Please enter a password"
                    password.length < 6 -> errorMessage = "Password must be at least 6 characters"
                    !termsAccepted -> errorMessage = "Please accept the terms and conditions"
                    else -> {
                        onCreateClick()
                    }
                }
            },
            isLoading = isLoading
        )
    }
}

@Composable
fun CreateAccountButton(onClick: () -> Unit, isLoading: Boolean = false) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF3B82F6)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 1.dp
        ),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            )
        }
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF94A3B8)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF8FAFC),
                unfocusedContainerColor = Color(0xFFF8FAFC),
                focusedIndicatorColor = Color(0xFF3B82F6),
                unfocusedIndicatorColor = Color(0xFFE2E8F0)
            ),
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}

@Composable
fun KneeSelectionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF3B82F6) else Color(0xFFE2E8F0)
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Box(
        modifier = modifier
            .height(100.dp)
            .border(
                border = BorderStroke(borderWidth, borderColor),
                shape = RoundedCornerShape(12.dp)
            )
            .background(Color.White, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF64748B),
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Preview
@Composable
fun CreateAccountScreenPreview() {
    CreateAccountScreen(
        role = "patient",
        onBackClick = {},
        onSignInClick = {},
        onCreateAccountClick = {}
    )
}

@Preview
@Composable
fun TherapistRegistrationPreview() {
    CreateAccountScreen(
        role = "therapist",
        onBackClick = {},
        onSignInClick = {},
        onCreateAccountClick = {}
    )
}
