package com.cerotek.imonitor.ui.screens.updates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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

@OptIn(ExperimentalMaterial3Api::class)
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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Aggiornamenti",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = UpdatesButtonStart,
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            color = UpdatesButtonStart.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = UpdatesButtonStart,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Text(
                            text = "Parametri Controllo",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    
                    Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                    
                    // Auto Check Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Controllo Automatico",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Verifica aggiornamenti all'avvio",
                                fontSize = 13.sp,
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
                    
                    Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                    
                    // Current Version
                    InfoCard(
                        title = "Versione Corrente",
                        value = com.cerotek.imonitor.BuildConfig.VERSION_NAME,
                        icon = Icons.Default.PhoneAndroid
                    )
                    
                    InfoCard(
                        title = "Ultimo Controllo",
                        value = lastCheckTime,
                        icon = Icons.Default.AccessTime
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
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = UpdatesButtonStart
                        ),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isChecking,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        if (isChecking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Controllo in corso...", fontSize = 16.sp)
                        } else {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Verifica Aggiornamenti", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StatusYellow.copy(alpha = 0.2f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Surface(
                                        color = StatusYellow.copy(alpha = 0.2f),
                                        shape = androidx.compose.foundation.shape.CircleShape,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("🎉", fontSize = 20.sp)
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = "Nuova versione disponibile!",
                                            fontSize = 15.sp,
                                            color = StatusYellow,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Versione ${updateInfo!!.latestVersion}",
                                            fontSize = 13.sp,
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
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StatusGreen.copy(alpha = 0.2f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Surface(
                                        color = StatusGreen.copy(alpha = 0.2f),
                                        shape = androidx.compose.foundation.shape.CircleShape,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = StatusGreen)
                                        }
                                    }
                                    Text(
                                        text = "L'app è aggiornata all'ultima versione",
                                        fontSize = 15.sp,
                                        color = StatusGreen,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
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

@Composable
fun InfoCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BackgroundLight.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(44.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = UpdatesButtonStart,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}
