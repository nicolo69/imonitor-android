package com.cerotek.imonitor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cerotek.imonitor.ui.theme.*

/**
 * ESEMPI DI UTILIZZO DELLE MICRO-INTERAZIONI
 * 
 * Questo file mostra come usare le micro-interazioni nella tua app.
 */

// ============================================
// ESEMPIO 1: Pulsante con Press and Hold
// ============================================
@Composable
fun ExamplePressAndHold() {
    var pressCount by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .pressAndHold(
                    scaleDown = 0.92f,
                    hapticFeedback = true,
                    onPress = { pressCount++ }
                )
        ) {
            Text("Premi e Tieni Premuto")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Premuto $pressCount volte",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// ============================================
// ESEMPIO 2: Card con Ripple Effect
// ============================================
@Composable
fun ExampleRippleCard() {
    var tapCount by remember { mutableStateOf(0) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .rippleEffect(
                hapticFeedback = true,
                onTap = { tapCount++ }
            ),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryBlue
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.TouchApp,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tocca per Ripple",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Text(
                text = "Toccato $tapCount volte",
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

// ============================================
// ESEMPIO 3: Bounce Effect su Icone
// ============================================
@Composable
fun ExampleBounceIcons() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf(
            Icons.Default.Favorite to Color(0xFFE91E63),
            Icons.Default.Star to Color(0xFFFFC107),
            Icons.Default.ThumbUp to Color(0xFF2196F3)
        ).forEach { (icon, color) ->
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f))
                    .bounceEffect(
                        bounceScale = 1.2f,
                        hapticFeedback = true
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

// ============================================
// ESEMPIO 4: Shake Effect per Errori
// ============================================
@Composable
fun ExampleShakeError() {
    var showError by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .shakeEffect(trigger = showError) {
                    showError = false
                },
            isError = showError
        )
        
        Button(
            onClick = {
                if (inputText.length < 6) {
                    showError = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Valida")
        }
        
        if (showError) {
            Text(
                text = "❌ Password troppo corta!",
                color = StatusRed,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// ============================================
// ESEMPIO 5: Pulse Effect per Alert
// ============================================
@Composable
fun ExamplePulseAlert() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .pulseEffect(
                enabled = true,
                minScale = 0.98f,
                maxScale = 1.02f,
                durationMillis = 1000
            ),
        colors = CardDefaults.cardColors(
            containerColor = StatusRed
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(
                    text = "⚠️ Attenzione",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Pressione elevata rilevata",
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

// ============================================
// ESEMPIO 6: Long Press Effect
// ============================================
@Composable
fun ExampleLongPress() {
    var isDeleting by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .longPressEffect(
                durationMillis = 1000,
                hapticFeedback = true,
                onLongPress = { isDeleting = true }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isDeleting) StatusRed else CardBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = null,
                tint = if (isDeleting) Color.White else TextPrimary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isDeleting) "Eliminazione..." else "Tieni premuto per eliminare",
                color = if (isDeleting) Color.White else TextPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
    
    LaunchedEffect(isDeleting) {
        if (isDeleting) {
            kotlinx.coroutines.delay(2000)
            isDeleting = false
        }
    }
}

// ============================================
// ESEMPIO 7: Rotate on Tap (Refresh)
// ============================================
@Composable
fun ExampleRotateRefresh() {
    var isRefreshing by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(PrimaryBlue)
                .rotateOnTap(
                    degrees = 360f,
                    durationMillis = 500,
                    onRotate = {
                        isRefreshing = true
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        
        Text(
            text = if (isRefreshing) "Aggiornamento..." else "Tocca per aggiornare",
            style = MaterialTheme.typography.bodyLarge
        )
    }
    
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            kotlinx.coroutines.delay(2000)
            isRefreshing = false
        }
    }
}

// ============================================
// ESEMPIO 8: Flip Effect (Card Flip)
// ============================================
@Composable
fun ExampleFlipCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
            .flipEffect(
                durationMillis = 600
            ),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryBlue
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tocca per girare",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

// ============================================
// ESEMPIO 9: Heartbeat Effect
// ============================================
@Composable
fun ExampleHeartbeat() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            Icons.Default.Favorite,
            contentDescription = "Heartbeat",
            tint = Color(0xFFE91E63),
            modifier = Modifier
                .size(100.dp)
                .heartbeatEffect(enabled = true)
        )
        
        Text(
            text = "72 BPM",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Frequenza Cardiaca",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

// ============================================
// ESEMPIO 10: Breathe Effect (Meditazione)
// ============================================
@Composable
fun ExampleBreathe() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4FACFE),
                        Color(0xFF00F2FE)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
                    .breatheEffect(
                        enabled = true,
                        minScale = 0.9f,
                        maxScale = 1.1f,
                        durationMillis = 4000
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Respira",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = "Inspira... Espira...",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}

// ============================================
// ESEMPIO 11: Success Animation
// ============================================
@Composable
fun ExampleSuccessAnimation() {
    var showSuccess by remember { mutableStateOf(false) }
    val successProgress = rememberSuccessAnimation(
        trigger = showSuccess,
        onComplete = {
            // Azione dopo successo
        }
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = { showSuccess = !showSuccess },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mostra Successo")
        }
        
        if (showSuccess) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = StatusGreen
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(48.dp)
                            .graphicsLayer(
                                scaleX = successProgress,
                                scaleY = successProgress,
                                alpha = successProgress
                            )
                    )
                    Column {
                        Text(
                            text = "✓ Successo!",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold

                        )
                        Text(
                            text = "Operazione completata",
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}

// ============================================
// ESEMPIO 12: Combinazione Effetti Micro-Interazioni
// ============================================
@Composable
fun ExampleMicroInteractionsCombined() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Micro-Interazioni",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        // Parametri vitali con effetti
        listOf(
            Triple("Pressione", "120/80", StatusGreen),
            Triple("Glicemia", "95", StatusGreen),
            Triple("Saturazione", "98%", StatusGreen),
            Triple("Temperatura", "36.5°C", StatusGreen)
        ).forEach { (name, value, color) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .rippleEffect(hapticFeedback = true)
                    .pressAndHold(scaleDown = 0.95f),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.headlineSmall,
                            color = color
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(color)
                            .pulseEffect(
                                enabled = true,
                                minScale = 0.8f,
                                maxScale = 1.2f
                            )
                    )
                }
            }
        }
    }
}
