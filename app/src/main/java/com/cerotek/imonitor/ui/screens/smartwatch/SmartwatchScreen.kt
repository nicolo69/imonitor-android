package com.cerotek.imonitor.ui.screens.smartwatch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cerotek.imonitor.ui.navigation.Screen
import com.cerotek.imonitor.ui.theme.*

@Composable
fun SmartwatchScreen(navController: NavController) {
    val viewModel: com.cerotek.imonitor.ui.screens.home.HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val bleConnectionState by viewModel.bleConnectionState.collectAsState()
    val batteryLevel by viewModel.batteryLevel.collectAsState()
    val isCharging by viewModel.isCharging.collectAsState()
    
    val isConnected = bleConnectionState == com.cerotek.imonitor.ui.screens.home.BleConnectionState.CONNECTED
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Back Button
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Indietro",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Indietro", color = Color.White, fontSize = 16.sp)
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "⌚ Smartwatch",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = WatchButtonStart
                )
                
                HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
                
                // Hero Section - Connection Status
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val statusColor = if (isConnected) StatusGreen else StatusRed
                    val statusIcon = if (isConnected) Icons.Default.WatchLater else Icons.Default.BluetoothDisabled
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Big Circle Visualization
                        Surface(
                            shape = CircleShape,
                            color = statusColor.copy(alpha = 0.1f),
                            modifier = Modifier.size(200.dp),
                            shadowElevation = 0.dp
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Inner Circle
                                Surface(
                                    shape = CircleShape,
                                    color = statusColor.copy(alpha = 0.2f),
                                    modifier = Modifier.size(150.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = statusIcon,
                                            contentDescription = null,
                                            tint = statusColor,
                                            modifier = Modifier.size(80.dp)
                                        )
                                    }
                                }
                            }
                        }
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isConnected) "CONNESSO" else "DISCONNESSO",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = if (isConnected) "Dispositivo Cerotek V1" else "Nessun dispositivo rilevato",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
                
                // Battery Level
                // Mostra sempre se conosciuto (>= 0) o connesso. Se -1, mostra "--"
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = BackgroundLight
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator(
                                    progress = { if (batteryLevel >= 0) batteryLevel / 100f else 0f },
                                    modifier = Modifier.size(50.dp),
                                    color = when {
                                        batteryLevel > 50 -> StatusGreen
                                        batteryLevel > 20 -> StatusYellow
                                        else -> StatusRed
                                    },
                                    trackColor = Color.LightGray
                                )
                                Column {
                                    Text(
                                        text = "BATTERIA",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = if (batteryLevel >= 0) "$batteryLevel%" else "--",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }
                            
                            Icon(
                                imageVector = when {
                                    batteryLevel >= 90 -> Icons.Default.BatteryFull
                                    isCharging -> Icons.Default.FlashOn
                                    batteryLevel > 20 -> Icons.Default.BatteryFull // Usa colore per distinguere
                                    else -> Icons.Default.BatteryAlert
                                },
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                // Action Buttons
                Button(
                    onClick = { viewModel.toggleConnection() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isConnected) StatusRed else WatchButtonStart
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(6.dp)
                ) {
                    Text(
                        text = if (isConnected) "DISCONNETTI" else "CONNETTI DISPOSITIVO",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
