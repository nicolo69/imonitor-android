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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cerotek.imonitor.R
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
            .background(MaterialTheme.colorScheme.background)
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
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Indietro",
                        tint = Color.White
                    )
                }
                
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.BarChart,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Storico",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "${allMeasurements.size} misurazioni totali",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
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
                    icon = Icons.Default.Dangerous,
                    count = criticalCount,
                    label = "Critiche",
                    color = StatusRed,
                    modifier = Modifier.weight(1f)
                )
                StatisticCard(
                    icon = Icons.Default.Warning,
                    count = warningCount,
                    label = "Attenzione",
                    color = StatusYellow,
                    modifier = Modifier.weight(1f)
                )
                StatisticCard(
                    icon = Icons.Default.CheckCircle,
                    count = normalCount,
                    label = "Normali",
                    color = StatusGreen,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Filter Chips
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                            label = "Tutte",
                            selected = selectedFilter == "Tutti",
                            onClick = { selectedFilter = "Tutti" },
                            icon = Icons.Default.List
                        )
                        FilterChip(
                            label = "Critiche",
                            selected = selectedFilter == "Critiche",
                            onClick = { selectedFilter = "Critiche" },
                            icon = Icons.Default.Error
                        )
                        FilterChip(
                            label = "Attenzione",
                            selected = selectedFilter == "Attenzione",
                            onClick = { selectedFilter = "Attenzione" },
                            icon = Icons.Default.Warning
                        )
                        FilterChip(
                            label = "Normali",
                            selected = selectedFilter == "Normali",
                            onClick = { selectedFilter = "Normali" },
                            icon = Icons.Default.CheckCircle
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
            Icon(
                icon, 
                contentDescription = null, 
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
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
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.height(44.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon, 
                contentDescription = null, 
                modifier = Modifier.size(16.dp),
                tint = if (selected) Color.White else MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(if (expanded) 6.dp else 2.dp)
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
                            .size(56.dp)
                            .background(
                                color = statusColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        val iconRes = when (measurement.type) {
                            "Pressione" -> R.drawable.ic_pressione
                            "Saturazione" -> R.drawable.ic_saturazione
                            "Battito" -> null // No specialized icon yet
                            "Temperatura" -> R.drawable.ic_temperatura
                            "Glicemia" -> R.drawable.ic_glicemia
                            "Grassi" -> R.drawable.ic_grassi
                            else -> null
                        }
                        
                        if (iconRes != null) {
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(32.dp)
                            )
                        } else {
                            Icon(
                                imageVector = if (measurement.type == "Battito") Icons.Default.Favorite else Icons.Default.Assessment,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    
                    Column {
                        Text(
                            text = measurement.type,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = dateFormat.format(Date(measurement.timestamp)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                
                // Right side - Value and Status
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = measurement.value,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = statusColor
                        )
                        Text(
                            text = measurement.unit,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
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
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
                    
                    // Additional details
                    DetailRow(icon = Icons.Default.CalendarToday, label = "Data completa", value = dateFormatFull.format(Date(measurement.timestamp)))
                    DetailRow(icon = Icons.Default.Tag, label = "ID Misurazione", value = "#${measurement.id.toString().padStart(4, '0')}")
                    DetailRow(icon = Icons.Default.Timer, label = "Tempo fa", value = getTimeAgo(measurement.timestamp))
                    
                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { /* Export action */ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Condividi", fontSize = 13.sp)
                        }
                        
                        OutlinedButton(
                            onClick = { /* Details action */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
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
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon, 
                contentDescription = null, 
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
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
