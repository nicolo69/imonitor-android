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
    val settingsManager = remember { com.cerotek.imonitor.util.SettingsManager(context) }
    
    // Stati per ogni parametro caricati dal SettingsManager
    var heartRateMin by remember { mutableStateOf(settingsManager.getThreshold(ParameterTypes.HEART_RATE).minValue.toInt().toString()) }
    var heartRateMax by remember { mutableStateOf(settingsManager.getThreshold(ParameterTypes.HEART_RATE).maxValue.toInt().toString()) }
    
    var pressureSysMin by remember { mutableStateOf(settingsManager.getThreshold(ParameterTypes.BLOOD_PRESSURE_SYSTOLIC).minValue.toInt().toString()) }
    var pressureSysMax by remember { mutableStateOf(settingsManager.getThreshold(ParameterTypes.BLOOD_PRESSURE_SYSTOLIC).maxValue.toInt().toString()) }
    
    var pressureDiaMin by remember { mutableStateOf(settingsManager.getThreshold(ParameterTypes.BLOOD_PRESSURE_DIASTOLIC).minValue.toInt().toString()) }
    var pressureDiaMax by remember { mutableStateOf(settingsManager.getThreshold(ParameterTypes.BLOOD_PRESSURE_DIASTOLIC).maxValue.toInt().toString()) }
    
    var saturationMin by remember { mutableStateOf(settingsManager.getThreshold(ParameterTypes.OXYGEN_SATURATION).minValue.toInt().toString()) }
    var saturationMax by remember { mutableStateOf(settingsManager.getThreshold(ParameterTypes.OXYGEN_SATURATION).maxValue.toInt().toString()) }
    
    var temperatureMin by remember { mutableStateOf(settingsManager.getThreshold(ParameterTypes.TEMPERATURE).minValue.toString()) }
    var temperatureMax by remember { mutableStateOf(settingsManager.getThreshold(ParameterTypes.TEMPERATURE).maxValue.toString()) }
    
    var glucoseMin by remember { mutableStateOf(settingsManager.getThreshold(ParameterTypes.BLOOD_SUGAR).minValue.toInt().toString()) }
    var glucoseMax by remember { mutableStateOf(settingsManager.getThreshold(ParameterTypes.BLOOD_SUGAR).maxValue.toInt().toString()) }
    
    var bodyFatMin by remember { mutableStateOf(settingsManager.getThreshold(ParameterTypes.BODY_FAT).minValue.toInt().toString()) }
    var bodyFatMax by remember { mutableStateOf(settingsManager.getThreshold(ParameterTypes.BODY_FAT).maxValue.toInt().toString()) }
    
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
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SecondaryColor.copy(alpha = 0.5f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = PrimaryBlue.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        "Imposta i valori minimi e massimi per ogni parametro. I valori fuori soglia verranno evidenziati per allertare l'utente.",
                        fontSize = 13.sp,
                        color = TextPrimary.copy(alpha = 0.8f),
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
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
            
            // Pressione Sistolica
            ThresholdCard(
                title = "Pressione Sistolica",
                icon = Icons.Default.Favorite,
                unit = "mmHg",
                minValue = pressureSysMin,
                maxValue = pressureSysMax,
                onMinChange = { pressureSysMin = it } ,
                onMaxChange = { pressureSysMax = it }
            )

            // Pressione Diastolica
            ThresholdCard(
                title = "Pressione Diastolica",
                icon = Icons.Default.FavoriteBorder,
                unit = "mmHg",
                minValue = pressureDiaMin,
                maxValue = pressureDiaMax,
                onMinChange = { pressureDiaMin = it } ,
                onMaxChange = { pressureDiaMax = it }
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
                    // Salva tutte le soglie tramite SettingsManager
                    settingsManager.setThreshold(ParameterThreshold(ParameterTypes.HEART_RATE, heartRateMin.toFloatOrNull() ?: 60f, heartRateMax.toFloatOrNull() ?: 100f, "bpm"))
                    settingsManager.setThreshold(ParameterThreshold(ParameterTypes.BLOOD_PRESSURE_SYSTOLIC, pressureSysMin.toFloatOrNull() ?: 90f, pressureSysMax.toFloatOrNull() ?: 140f, "mmHg"))
                    settingsManager.setThreshold(ParameterThreshold(ParameterTypes.BLOOD_PRESSURE_DIASTOLIC, pressureDiaMin.toFloatOrNull() ?: 60f, pressureDiaMax.toFloatOrNull() ?: 90f, "mmHg"))
                    settingsManager.setThreshold(ParameterThreshold(ParameterTypes.OXYGEN_SATURATION, saturationMin.toFloatOrNull() ?: 95f, saturationMax.toFloatOrNull() ?: 100f, "%"))
                    settingsManager.setThreshold(ParameterThreshold(ParameterTypes.TEMPERATURE, temperatureMin.toFloatOrNull() ?: 36f, temperatureMax.toFloatOrNull() ?: 37.5f, "°C"))
                    settingsManager.setThreshold(ParameterThreshold(ParameterTypes.BLOOD_SUGAR, glucoseMin.toFloatOrNull() ?: 70f, glucoseMax.toFloatOrNull() ?: 140f, "mg/dL"))
                    settingsManager.setThreshold(ParameterThreshold(ParameterTypes.BODY_FAT, bodyFatMin.toFloatOrNull() ?: 10f, bodyFatMax.toFloatOrNull() ?: 30f, "%"))
                    
                    showSavedMessage = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Salva Impostazioni", fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            }
            
            // Messaggio salvato
            if (showSavedMessage) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    showSavedMessage = false
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = StatusGreen
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = androidx.compose.foundation.shape.CircleShape,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            "Impostazioni salvate con successo!",
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = BackgroundLight,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                icon,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Text(
                        title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                Surface(
                    color = PrimaryBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = unit,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
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
                        "LIMITE MIN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    OutlinedTextField(
                        value = minValue,
                        onValueChange = onMinChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            unfocusedContainerColor = Color(0xFFF9F9F9),
                            focusedContainerColor = Color.White
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                
                // Massimo
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "LIMITE MAX",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    OutlinedTextField(
                        value = maxValue,
                        onValueChange = onMaxChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            unfocusedContainerColor = Color(0xFFF9F9F9),
                            focusedContainerColor = Color.White
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }
    }
}
