package com.cerotek.imonitor.ui.screens.parameterdetail

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.cerotek.imonitor.IMonitorApplication
import com.cerotek.imonitor.ui.theme.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class ParameterMeasurement(
    val value: Float,
    val timestamp: Long,
    val status: String
)

enum class TimeRange(val label: String) {
    WEEK("Ultima Settimana"),
    MONTH("Ultimo Mese"),
    ALL("Tutto")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParameterDetailScreen(
    navController: NavController,
    parameterType: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = (context.applicationContext as IMonitorApplication).repository
    
    // Stato per filtro temporale
    var selectedTimeRange by remember { mutableStateOf(TimeRange.WEEK) }
    
    // Carica misurazioni dal database
    val measurements by repository.allMeasurements.collectAsState(initial = emptyList())
    
    // Filtra e mappa le misurazioni per il parametro specifico e range temporale
    val parameterMeasurements = remember(measurements, parameterType, selectedTimeRange) {
        val cutoffDate = when (selectedTimeRange) {
            TimeRange.WEEK -> Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }.time
            TimeRange.MONTH -> Calendar.getInstance().apply { add(Calendar.MONTH, -1) }.time
            TimeRange.ALL -> Date(0) // Tutte le misurazioni
        }
        
        measurements
            .filter { it.timestamp >= cutoffDate }
            .mapNotNull { entity ->
                when (parameterType) {
                    "heart_rate" -> entity.heartRate?.let { 
                        ParameterMeasurement(it.toFloat(), entity.timestamp.time, getStatus(it.toFloat(), 60f, 100f))
                    }
                    "blood_pressure" -> entity.systolic?.let { 
                        ParameterMeasurement(it.toFloat(), entity.timestamp.time, getStatus(it.toFloat(), 90f, 140f))
                    }
                    "oxygen_saturation" -> entity.oxygenSaturation?.let { 
                        ParameterMeasurement(it.toFloat(), entity.timestamp.time, getStatus(it.toFloat(), 95f, 100f))
                    }
                    "temperature" -> entity.temperature?.let { 
                        ParameterMeasurement(it, entity.timestamp.time, getStatus(it, 36.0f, 37.5f))
                    }
                    "blood_sugar" -> entity.bloodSugar?.let { 
                        ParameterMeasurement(it, entity.timestamp.time, getStatus(it, 70f, 140f))
                    }
                    else -> null
                }
            }
            .sortedByDescending { it.timestamp }
    }
    
    val (parameterName, parameterUnit, parameterIcon) = getParameterInfo(parameterType)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(parameterName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            exportToExcel(context, parameterMeasurements, parameterName, parameterUnit)
                        }
                    }) {
                        Icon(Icons.Default.FileDownload, "Export Excel")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Filtro temporale
            item {
                TimeRangeSelector(
                    selectedRange = selectedTimeRange,
                    onRangeSelected = { selectedTimeRange = it }
                )
            }
            
            // Messaggio se non ci sono dati
            if (parameterMeasurements.isEmpty()) {
                item {
                    NoDataCard(selectedTimeRange)
                }
            } else {
                // Statistiche
                item {
                    StatisticsCard(parameterMeasurements, parameterUnit)
                }
                
                // Grafico
                item {
                    ChartCard(parameterMeasurements, parameterUnit, selectedTimeRange)
                }
                
                // Storico
                item {
                    Text(
                        "Storico Misurazioni (${parameterMeasurements.size})",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                items(parameterMeasurements) { measurement ->
                    MeasurementCard(measurement, parameterUnit)
                }
            }
        }
    }
}

@Composable
fun TimeRangeSelector(
    selectedRange: TimeRange,
    onRangeSelected: (TimeRange) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "📅 Periodo",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimeRange.values().forEach { range ->
                    FilterChip(
                        selected = selectedRange == range,
                        onClick = { onRangeSelected(range) },
                        label = { Text(range.label) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun NoDataCard(timeRange: TimeRange) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = TextSecondary.copy(alpha = 0.5f)
            )
            
            Text(
                "Nessun Dato Disponibile",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            
            Text(
                when (timeRange) {
                    TimeRange.WEEK -> "Non ci sono misurazioni nell'ultima settimana"
                    TimeRange.MONTH -> "Non ci sono misurazioni nell'ultimo mese"
                    TimeRange.ALL -> "Non ci sono ancora misurazioni salvate"
                },
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            
            Text(
                "I dati appariranno automaticamente quando lo smartwatch invierà le misurazioni",
                fontSize = 12.sp,
                color = TextSecondary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun StatisticsCard(measurements: List<ParameterMeasurement>, unit: String) {
    if (measurements.isEmpty()) return
    
    val avg = measurements.map { it.value }.average().toFloat()
    val minVal = measurements.minOf { it.value }
    val maxVal = measurements.maxOf { it.value }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "📊 Statistiche",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Media", "%.1f".format(avg), unit, StatusBlue)
                StatItem("Min", "%.1f".format(minVal), unit, StatusGreen)
                StatItem("Max", "%.1f".format(maxVal), unit, StatusRed)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, unit: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            label,
            fontSize = 14.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
        Text(
            value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            unit,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun ChartCard(measurements: List<ParameterMeasurement>, unit: String, timeRange: TimeRange) {
    if (measurements.isEmpty()) return
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "📈 Andamento",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    timeRange.label,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            
            LineChart(
                data = measurements.reversed(), // Ordine cronologico per grafico
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }
    }
}

@Composable
fun LineChart(
    data: List<ParameterMeasurement>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return
    
    val minValue = data.minOf { it.value }
    val maxValue = data.maxOf { it.value }
    val range = maxValue - minValue
    
    Canvas(modifier = modifier.padding(16.dp)) {
        val width = size.width
        val height = size.height
        val spacing = if (data.size > 1) width / (data.size - 1) else width / 2
        
        // Disegna griglia
        for (i in 0..4) {
            val y = height * i / 4
            drawLine(
                color = Color.LightGray.copy(alpha = 0.3f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx()
            )
        }
        
        // Disegna linea
        if (data.size > 1) {
            val path = Path()
            data.forEachIndexed { index, measurement ->
                val x = index * spacing
                val normalizedValue = if (range > 0) (measurement.value - minValue) / range else 0.5f
                val y = height - (normalizedValue * height)
                
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            
            drawPath(
                path = path,
                color = PrimaryBlue,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        
        // Disegna punti
        data.forEachIndexed { index, measurement ->
            val x = index * spacing
            val normalizedValue = if (range > 0) (measurement.value - minValue) / range else 0.5f
            val y = height - (normalizedValue * height)
            
            val pointColor = when (measurement.status) {
                "CRITICO" -> StatusRed
                "ATTENZIONE" -> StatusYellow
                else -> StatusGreen
            }
            
            drawCircle(
                color = pointColor,
                radius = 6.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun MeasurementCard(measurement: ParameterMeasurement, unit: String) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    val statusColor = when (measurement.status) {
        "CRITICO" -> StatusRed
        "ATTENZIONE" -> StatusYellow
        else -> StatusGreen
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    dateFormat.format(Date(measurement.timestamp)),
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Text(
                    measurement.status,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    "%.1f".format(measurement.value),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
                Text(
                    unit,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}

private fun getStatus(value: Float, min: Float, max: Float): String {
    return when {
        value < min -> "ATTENZIONE"
        value > max -> "CRITICO"
        else -> "NORMALE"
    }
}

private fun getParameterInfo(type: String): Triple<String, String, String> {
    return when (type) {
        "heart_rate" -> Triple("Frequenza Cardiaca", "bpm", "❤️")
        "blood_pressure" -> Triple("Pressione Sanguigna", "mmHg", "🩸")
        "oxygen_saturation" -> Triple("Saturazione Ossigeno", "%", "🫁")
        "temperature" -> Triple("Temperatura", "°C", "🌡️")
        "blood_sugar" -> Triple("Glicemia", "mg/dL", "🍬")
        "body_fat" -> Triple("Grassi Corpo", "%", "⚖️")
        else -> Triple("Parametro", "", "📊")
    }
}

private fun exportToExcel(
    context: Context,
    measurements: List<ParameterMeasurement>,
    parameterName: String,
    unit: String
) {
    try {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val fileName = "iMonitor_${parameterName.replace(" ", "_")}_${System.currentTimeMillis()}.csv"
        val file = File(context.getExternalFilesDir(null), fileName)
        
        file.bufferedWriter().use { writer ->
            writer.write("Data,Valore ($unit),Stato\n")
            
            measurements.forEach { measurement ->
                val date = dateFormat.format(Date(measurement.timestamp))
                writer.write("$date,${measurement.value},${measurement.status}\n")
            }
        }
        
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Export $parameterName")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(intent, "Esporta dati"))
        
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
