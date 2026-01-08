package com.example.flexkneeai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flexkneeai.ui.theme.*

data class ChatMessage(
    val id: Int,
    val text: String,
    val timestamp: String,
    val isFromMe: Boolean
)

@Composable
fun ChatScreen(
    chatPartner: String = "Dr. Chen",
    isTherapist: Boolean = false,
    onBackClick: (() -> Unit)? = null
) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            // If I am therapist, the first message (from patient) is NOT from me.
            // If I am patient, the first message (from patient/me) IS from me.
            ChatMessage(1, "Hi Dr. Chen, my knee feels a bit stiff today.", "10:30 AM", !isTherapist),
            ChatMessage(2, "That's normal after increasing reps. Try icing it for 15 mins.", "10:32 AM", isTherapist)
        )
    }
    
    // Dynamic suggestions based on the last message from the partner
    val lastReceivedMessage = messages.lastOrNull { !it.isFromMe }?.text
    val suggestions = generateSmartSuggestions(isTherapist, lastReceivedMessage)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC) // Slate 50
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header (Aligned with MyProgressScreen)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Slate900
                        )
                    }
                }
                Text(
                    text = chatPartner,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    modifier = Modifier.padding(start = if (onBackClick != null) 8.dp else 4.dp)
                )
            }

            // Chat Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(messages) { message ->
                        MessageBubble(message)
                    }
                }
                
                // Suggested replies
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    suggestions.forEach { suggestion ->
                        SuggestionChip(text = suggestion) {
                            messages.add(ChatMessage(messages.size + 1, suggestion, "10:35 AM", true))
                        }
                    }
                }
            }

            // Input Section
            ChatInputSection(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSendClick = {
                    if (messageText.isNotBlank()) {
                        messages.add(ChatMessage(messages.size + 1, messageText, "10:35 AM", true))
                        messageText = ""
                    }
                }
            )
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
    ) {
        val backgroundColor = if (message.isFromMe) Blue500 else Color.White
        val textColor = if (message.isFromMe) Color.White else Slate700
        val shape = if (message.isFromMe) {
            RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 4.dp)
        } else {
            RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 20.dp)
        }

        Surface(
            color = backgroundColor,
            shape = shape,
            border = if (!message.isFromMe) androidx.compose.foundation.BorderStroke(1.dp, Slate100) else null,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = message.text,
                    color = textColor,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.timestamp,
                    color = if (message.isFromMe) Color.White.copy(alpha = 0.7f) else Slate400,
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }
    }
}

@Composable
fun SuggestionChip(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Slate100,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            color = Slate600,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun ChatInputSection(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageText,
                onValueChange = onMessageChange,
                placeholder = { Text("Type a message...", color = Slate400) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 52.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Slate50,
                    unfocusedContainerColor = Slate50,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Blue500
                ),
                shape = RoundedCornerShape(26.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                onClick = onSendClick,
                modifier = Modifier.size(48.dp),
                color = Blue500,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

fun generateSmartSuggestions(isTherapist: Boolean, lastMsg: String?): List<String> {
    if (lastMsg == null) {
        return if (isTherapist) {
            listOf("How is your knee today?", "Any pain?", "Time for a check-in")
        } else {
            listOf("Hi Dr. Chen", "I have a question", "Update on progress")
        }
    }

    val msg = lastMsg.lowercase()
    
    return if (isTherapist) {
        // I am Therapist, replying to Patient
        when {
            msg.contains("pain") || msg.contains("stiff") || msg.contains("hurt") -> 
                listOf("Please log the pain level", "Have you tried icing?", "Let's reduce the reps")
            msg.contains("appointment") || msg.contains("schedule") -> 
                listOf("Confirming our next slot", "Check the calendar", "Yes, see you then")
            msg.contains("done") || msg.contains("finished") -> 
                listOf("Great job!", "How do you feel?", "Keep it up")
            else -> listOf("Great progress!", "Keep it up", "How is the swelling?")
        }
    } else {
        // I am Patient, replying to Therapist
        when {
            msg.contains("log") || msg.contains("track") -> 
                listOf("Will do", "Done", "Where do I log?")
            msg.contains("appointment") || msg.contains("time") -> 
                listOf("Confirmed", "Can we reschedule?", "See you then")
            msg.contains("ice") || msg.contains("rest") ->
                listOf("Okay, thanks", "Will do", "Already did")
            else -> listOf("Thanks!", "Feeling better", "I have a question")
        }
    }
}
