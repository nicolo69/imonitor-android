package com.cerotek.imonitor.ui.screens.storico

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cerotek.imonitor.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class Measurement(
    val id: Int,
    val type: String,
    val value: String,
    val unit: String,
    val timestamp: Long,
    val status: MeasurementStatus
)

enum class MeasurementStatus {
    NORMAL, WARNING, CRITICAL
}

@Composable
fun StoricoScreen(navController: NavController) {
    var selectedFilter by remember { mutableStateOf("Tutti") }
    
    // Sample data
    val allMeasurements = remember {
        listOf(
            Measurement(1, "Pressione", "140/90", "mmHg", System.currentTimeMillis() - 3600000, MeasurementStatus.WARNING),
            Measurement(2, "Saturazione", "95", "%", System.currentTimeMillis() - 7200000, MeasurementStatus.WARNING),
            Measurement(3, "Battito", "85", "bpm", System.currentTimeMillis() - 10800000, MeasurementStatus.WARNING),
            Measurement(4, "Pressione", "180/110", "mmHg", System.currentTimeMillis() - 14400000, MeasurementStatus.CRITICAL),
            Measurement(5, "Temperatura", "38.5", "°C", System.currentTimeMillis() - 18000000, MeasurementStatus.CRITICAL),
            Measurement(6, "Saturazione", "88", "%", System.currentTimeMillis() - 21600000, MeasurementStatus.CRITICAL),
            Measurement(7, "Battito", "72", "bpm", System.currentTimeMillis() - 25200000, MeasurementStatus.NORMAL),
            Measurement(8, "Pressione", "120/80", "mmHg", System.currentTimeMillis() - 28800000, MeasurementStatus.NORMAL),
            Measurement(9, "Glicemia", "95", "mg/dL", System.currentTimeMillis() - 32400000, MeasurementStatus.NORMAL),
            Measurement(10, "Temperatura", "36.5", "°C", System.currentTimeMillis() - 36000000, MeasurementStatus.NORMAL),
        )
    }
    
    val filteredMeasurements = when (selectedFilter) {
        "Critiche" -> allMeasurements.filter { it.status == MeasurementStatus.CRITICAL }
        "Attenzione" -> allMeasurements.filter { it.status == MeasurementStatus.WARNING }
        "Normali" -> allMeasurements.filter { it.status == MeasurementStatus.NORMAL }
        else -> allMeasurements
    }
    
    val criticalCount = allMeasurements.count { it.status == MeasurementStatus.CRITICAL }
    val warningCount = allMeasurements.count { it.status == MeasurementStatus.WARNING }
    val normalCount = allMeasurements.count { it.status == MeasurementStatus.NORMAL }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Top Bar with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(PrimaryBlue, PrimaryBlueLight)
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Back Button
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Indietro",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Indietro", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("📊", fontSize = 48.sp)
                    Column {
                        Text(
                            text = "Storico Misurazioni",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "${allMeasurements.size} misurazioni totali",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Statistics Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatisticCard(
                    icon = "🔴",
                    count = criticalCount,
                    label = "Critiche",
                    color = StatusRed,
                    modifier = Modifier.weight(1f)
                )
                StatisticCard(
                    icon = "🟡",
                    count = warningCount,
                    label = "Attenzione",
                    color = StatusYellow,
                    modifier = Modifier.weight(1f)
                )
                StatisticCard(
                    icon = "🟢",
                    count = normalCount,
                    label = "Normali",
                    color = StatusGreen,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Filter Chips
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Filtra per stato",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            label = "Tutti",
                            selected = selectedFilter == "Tutti",
                            onClick = { selectedFilter = "Tutti" },
                            icon = "📋"
                        )
                        FilterChip(
                            label = "Critiche",
                            selected = selectedFilter == "Critiche",
                            onClick = { selectedFilter = "Critiche" },
                            icon = "🔴"
                        )
                        FilterChip(
                            label = "Attenzione",
                            selected = selectedFilter == "Attenzione",
                            onClick = { selectedFilter = "Attenzione" },
                            icon = "🟡"
                        )
                        FilterChip(
                            label = "Normali",
                            selected = selectedFilter == "Normali",
                            onClick = { selectedFilter = "Normali" },
                            icon = "🟢"
                        )
                    }
                }
            }
            
            // Measurements List
            Text(
                text = "Misurazioni (${filteredMeasurements.size})",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            if (filteredMeasurements.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("📭", fontSize = 64.sp)
                            Text(
                                text = "Nessuna misurazione trovata",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                filteredMeasurements.forEach { measurement ->
                    AnimatedMeasurementCard(measurement)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun StatisticCard(
    icon: String,
    count: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(icon, fontSize = 36.sp)
            Text(
                text = count.toString(),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) PrimaryBlue else BackgroundLight,
        modifier = Modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 16.sp)
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) Color.White else TextPrimary
            )
        }
    }
}

@Composable
fun AnimatedMeasurementCard(measurement: Measurement) {
    var expanded by remember { mutableStateOf(false) }
    
    val statusColor = when (measurement.status) {
        MeasurementStatus.CRITICAL -> StatusRed
        MeasurementStatus.WARNING -> StatusYellow
        MeasurementStatus.NORMAL -> StatusGreen
    }
    
    val statusText = when (measurement.status) {
        MeasurementStatus.CRITICAL -> "CRITICO"
        MeasurementStatus.WARNING -> "ATTENZIONE"
        MeasurementStatus.NORMAL -> "NORMALE"
    }
    
    val statusIcon = when (measurement.status) {
        MeasurementStatus.CRITICAL -> "⚠️"
        MeasurementStatus.WARNING -> "⚡"
        MeasurementStatus.NORMAL -> "✓"
    }
    
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormatFull = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side - Icon and Type
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                color = statusColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (measurement.type) {
                                "Pressione" -> "🩸"
                                "Saturazione" -> "🫁"
                                "Battito" -> "❤️"
                                "Temperatura" -> "🌡️"
                                "Glicemia" -> "🍬"
                                "Grassi" -> "⚖️"
                                else -> "📊"
                            },
                            fontSize = 32.sp
                        )
                    }
                    
                    Column {
                        Text(
                            text = measurement.type,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = dateFormat.format(Date(measurement.timestamp)),
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
                
                // Right side - Value and Status
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = measurement.value,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = statusColor
                        )
                        Text(
                            text = measurement.unit,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(statusIcon, fontSize = 12.sp)
                            Text(
                                text = statusText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                    }
                }
            }
            
            // Expanded details
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(color = BackgroundLight, thickness = 2.dp)
                    
                    // Additional details
                    DetailRow(icon = "📅", label = "Data completa", value = dateFormatFull.format(Date(measurement.timestamp)))
                    DetailRow(icon = "🆔", label = "ID Misurazione", value = "#${measurement.id.toString().padStart(4, '0')}")
                    DetailRow(icon = "⏱️", label = "Tempo fa", value = getTimeAgo(measurement.timestamp))
                    
                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { /* Export action */ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Condividi", fontSize = 13.sp)
                        }
                        
                        OutlinedButton(
                            onClick = { /* Details action */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = PrimaryBlue
                            )
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Dettagli", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(icon: String, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 18.sp)
            Text(
                text = label,
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

fun getTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    
    return when {
        days > 0 -> "$days ${if (days == 1L) "giorno" else "giorni"} fa"
        hours > 0 -> "$hours ${if (hours == 1L) "ora" else "ore"} fa"
        minutes > 0 -> "$minutes ${if (minutes == 1L) "minuto" else "minuti"} fa"
        else -> "Pochi secondi fa"
    }
}
