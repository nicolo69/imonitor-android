package com.cerotek.imonitor.ui.screens.thresholds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cerotek.imonitor.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThresholdsScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("thresholds_prefs", android.content.Context.MODE_PRIVATE) }
    
    // Stati per ogni parametro
    var pressureMin by remember { mutableStateOf(sharedPrefs.getString("pressure_min", "90/60") ?: "90/60") }
    var pressureMax by remember { mutableStateOf(sharedPrefs.getString("pressure_max", "140/90") ?: "140/90") }
    
    var saturationMin by remember { mutableStateOf(sharedPrefs.getString("saturation_min", "95") ?: "95") }
    var saturationMax by remember { mutableStateOf(sharedPrefs.getString("saturation_max", "100") ?: "100") }
    
    var heartRateMin by remember { mutableStateOf(sharedPrefs.getString("heart_rate_min", "60") ?: "60") }
    var heartRateMax by remember { mutableStateOf(sharedPrefs.getString("heart_rate_max", "100") ?: "100") }
    
    var temperatureMin by remember { mutableStateOf(sharedPrefs.getString("temperature_min", "36.0") ?: "36.0") }
    var temperatureMax by remember { mutableStateOf(sharedPrefs.getString("temperature_max", "37.5") ?: "37.5") }
    
    var glucoseMin by remember { mutableStateOf(sharedPrefs.getString("glucose_min", "70") ?: "70") }
    var glucoseMax by remember { mutableStateOf(sharedPrefs.getString("glucose_max", "140") ?: "140") }
    
    var bodyFatMin by remember { mutableStateOf(sharedPrefs.getString("body_fat_min", "10") ?: "10") }
    var bodyFatMax by remember { mutableStateOf(sharedPrefs.getString("body_fat_max", "30") ?: "30") }
    
    var showSavedMessage by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Soglie Parametri",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "Imposta i valori minimi e massimi per ogni parametro. I valori fuori soglia verranno evidenziati in giallo (attenzione) o rosso (alert).",
                        fontSize = 14.sp,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
                }
            }
            
            // Pressione Sanguigna
            ThresholdCard(
                title = "Pressione Sanguigna",
                icon = Icons.Default.Favorite,
                unit = "mmHg",
                minValue = pressureMin,
                maxValue = pressureMax,
                onMinChange = { pressureMin = it },
                onMaxChange = { pressureMax = it }
            )
            
            // Saturazione
            ThresholdCard(
                title = "Saturazione",
                icon = Icons.Default.Air,
                unit = "%",
                minValue = saturationMin,
                maxValue = saturationMax,
                onMinChange = { saturationMin = it },
                onMaxChange = { saturationMax = it }
            )
            
            // Frequenza Cardiaca
            ThresholdCard(
                title = "Frequenza Cardiaca",
                icon = Icons.Default.MonitorHeart,
                unit = "bpm",
                minValue = heartRateMin,
                maxValue = heartRateMax,
                onMinChange = { heartRateMin = it },
                onMaxChange = { heartRateMax = it }
            )
            
            // Temperatura
            ThresholdCard(
                title = "Temperatura",
                icon = Icons.Default.Thermostat,
                unit = "°C",
                minValue = temperatureMin,
                maxValue = temperatureMax,
                onMinChange = { temperatureMin = it },
                onMaxChange = { temperatureMax = it }
            )
            
            // Glicemia
            ThresholdCard(
                title = "Glicemia",
                icon = Icons.Default.Bloodtype,
                unit = "mg/dL",
                minValue = glucoseMin,
                maxValue = glucoseMax,
                onMinChange = { glucoseMin = it },
                onMaxChange = { glucoseMax = it }
            )
            
            // Grassi Corporei
            ThresholdCard(
                title = "Grassi Corporei",
                icon = Icons.Default.FitnessCenter,
                unit = "%",
                minValue = bodyFatMin,
                maxValue = bodyFatMax,
                onMinChange = { bodyFatMin = it },
                onMaxChange = { bodyFatMax = it }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Pulsante Salva
            Button(
                onClick = {
                    // Salva tutte le soglie
                    sharedPrefs.edit().apply {
                        putString("pressure_min", pressureMin)
                        putString("pressure_max", pressureMax)
                        putString("saturation_min", saturationMin)
                        putString("saturation_max", saturationMax)
                        putString("heart_rate_min", heartRateMin)
                        putString("heart_rate_max", heartRateMax)
                        putString("temperature_min", temperatureMin)
                        putString("temperature_max", temperatureMax)
                        putString("glucose_min", glucoseMin)
                        putString("glucose_max", glucoseMax)
                        putString("body_fat_min", bodyFatMin)
                        putString("body_fat_max", bodyFatMax)
                        apply()
                    }
                    showSavedMessage = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                )
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Salva Soglie", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            // Messaggio salvato
            if (showSavedMessage) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    showSavedMessage = false
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = StatusGreen
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Text(
                            "Soglie salvate con successo!",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ThresholdCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    unit: String,
    minValue: String,
    maxValue: String,
    onMinChange: (String) -> Unit,
    onMaxChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            
            // Min e Max
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Minimo
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Minimo",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = minValue,
                        onValueChange = onMinChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        suffix = { Text(unit, fontSize = 12.sp, color = TextSecondary) },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            focusedLabelColor = PrimaryBlue
                        )
                    )
                }
                
                // Massimo
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Massimo",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = maxValue,
                        onValueChange = onMaxChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        suffix = { Text(unit, fontSize = 12.sp, color = TextSecondary) },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            focusedLabelColor = PrimaryBlue
                        )
                    )
                }
            }
        }
    }
}
