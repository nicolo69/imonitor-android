package com.cerotek.imonitor.ui.screens.updates

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cerotek.imonitor.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun UpdatesScreen(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val updateManager = remember { com.cerotek.imonitor.util.UpdateManager(context) }
    val scope = rememberCoroutineScope()
    
    var autoCheckEnabled by remember { mutableStateOf(updateManager.isAutoCheckEnabled()) }
    var isChecking by remember { mutableStateOf(false) }
    var updateInfo by remember { mutableStateOf<com.cerotek.imonitor.util.UpdateManager.UpdateInfo?>(null) }
    var lastCheckTime by remember { mutableStateOf(updateManager.getLastCheckTimeFormatted()) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    
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
                    text = "🔄 Aggiornamenti",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = UpdatesButtonStart
                )
                
                Divider(color = Color.LightGray, thickness = 1.dp)
                
                // Auto Check Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Controllo Automatico",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Verifica aggiornamenti all'avvio",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = autoCheckEnabled,
                        onCheckedChange = { 
                            autoCheckEnabled = it
                            updateManager.setAutoCheckEnabled(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = UpdatesButtonStart
                        )
                    )
                }
                
                Divider(color = Color.LightGray, thickness = 1.dp)
                
                // Current Version
                InfoCard(
                    title = "Versione Corrente",
                    value = com.cerotek.imonitor.BuildConfig.VERSION_NAME,
                    icon = "📱"
                )
                
                InfoCard(
                    title = "Ultimo Controllo",
                    value = lastCheckTime,
                    icon = "🕐"
                )
                
                // Check Button
                Button(
                    onClick = { 
                        isChecking = true
                        scope.launch {
                            val result = updateManager.checkForUpdates()
                            updateInfo = result
                            lastCheckTime = updateManager.getLastCheckTimeFormatted()
                            isChecking = false
                            if (result.available) {
                                showUpdateDialog = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UpdatesButtonStart
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isChecking
                ) {
                    if (isChecking) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Controllo in corso...", fontSize = 18.sp)
                    } else {
                        Text("Verifica Aggiornamenti", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                // Status Message
                if (updateInfo != null) {
                    if (updateInfo!!.available) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = StatusYellow.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("🎉", fontSize = 24.sp)
                                Column {
                                    Text(
                                        text = "Aggiornamento disponibile!",
                                        fontSize = 16.sp,
                                        color = StatusYellow,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Versione ${updateInfo!!.latestVersion}",
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = StatusGreen.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("✅", fontSize = 24.sp)
                                Text(
                                    text = "App aggiornata all'ultima versione",
                                    fontSize = 16.sp,
                                    color = StatusGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Update Dialog
        if (showUpdateDialog && updateInfo != null && updateInfo!!.available) {
            AlertDialog(
                onDismissRequest = { showUpdateDialog = false },
                title = {
                    Text(
                        "🎉 Aggiornamento Disponibile",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Versione ${updateInfo!!.latestVersion} disponibile!",
                            fontWeight = FontWeight.SemiBold
                        )
                        if (updateInfo!!.releaseNotes != null) {
                            Text(
                                updateInfo!!.releaseNotes!!,
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            updateManager.openDownloadUrl()
                            showUpdateDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = UpdatesButtonStart
                        )
                    ) {
                        Text("Scarica")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUpdateDialog = false }) {
                        Text("Dopo")
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun InfoCard(title: String, value: String, icon: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundLight
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(icon, fontSize = 28.sp)
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        }
    }
}
