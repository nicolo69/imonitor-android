package com.cerotek.imonitor.ui.screens.smartwatch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
    var isConnected by remember { mutableStateOf(false) }
    var batteryLevel by remember { mutableStateOf(75) }
    
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
                
                // Connection Status
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isConnected) StatusGreen.copy(alpha = 0.1f) 
                                        else StatusRed.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = if (isConnected) "✅" else "❌",
                            fontSize = 40.sp
                        )
                        Column {
                            Text(
                                text = if (isConnected) "Connesso" else "Disconnesso",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isConnected) StatusGreen else StatusRed
                            )
                            Text(
                                text = if (isConnected) "Dispositivo collegato" else "Nessun dispositivo",
                                fontSize = 16.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
                
                // Battery Level
                if (isConnected) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = BackgroundLight
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("🔋", fontSize = 32.sp)
                                Text(
                                    text = "Batteria",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                            
                            Text(
                                text = "$batteryLevel%",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    batteryLevel > 50 -> StatusGreen
                                    batteryLevel > 20 -> StatusYellow
                                    else -> StatusRed
                                }
                            )
                            
                            LinearProgressIndicator(
                                progress = { batteryLevel / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp),
                                color = when {
                                    batteryLevel > 50 -> StatusGreen
                                    batteryLevel > 20 -> StatusYellow
                                    else -> StatusRed
                                },
                                trackColor = Color.LightGray,
                                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        }
                    }
                }
                
                // Action Buttons
                Button(
                    onClick = { isConnected = !isConnected },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isConnected) StatusRed else WatchButtonStart
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isConnected) "Disconnetti" else "Connetti Dispositivo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Navigate to Parameters
                OutlinedButton(
                    onClick = { navController.navigate(Screen.Parametri.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = WatchButtonStart
                    )
                ) {
                    Text(
                        text = "Visualizza Parametri",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                OutlinedButton(
                    onClick = { navController.navigate(Screen.Storico.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = WatchButtonStart
                    )
                ) {
                    Text(
                        text = "Storico Misurazioni",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
