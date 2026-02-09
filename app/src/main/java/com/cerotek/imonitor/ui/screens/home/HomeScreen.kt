package com.cerotek.imonitor.ui.screens.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cerotek.imonitor.R
import com.cerotek.imonitor.ble.model.AllDataBean
import com.cerotek.imonitor.ui.navigation.Screen
import com.cerotek.imonitor.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val securePrefs = remember { com.cerotek.imonitor.util.SecurePreferences(context) }
    var showPatientDialog by remember { mutableStateOf(false) }
    
    // ViewModel
    val viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    
    // Carica info paziente
    val userInfo = remember { securePrefs.getUserInfo() }
    
    // Osserva stato connessione BLE dal ViewModel
    val bleConnectionState by viewModel.bleConnectionState.collectAsState()
    val latestMeasurements by viewModel.latestMeasurements.collectAsState()
    val parameterStatuses by viewModel.parameterStatuses.collectAsState()
    var showBleAlert by remember { mutableStateOf(true) }
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                userInfo = userInfo,
                onNavigate = { route ->
                    navController.navigate(route)
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = com.cerotek.imonitor.R.drawable.ic_logo),
                                contentDescription = "Cerotek Logo",
                                modifier = Modifier.height(48.dp),
                                contentScale = androidx.compose.ui.layout.ContentScale.Fit
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { 
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = TextPrimary,
                        navigationIconContentColor = TextPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showPatientDialog = true },
                    containerColor = PrimaryBlue,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Info Paziente")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFFE3F2FD),  // Light blue
                                Color(0xFFBBDEFB)   // Lighter blue
                            )
                        )
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // BLE Connection Alert
                if (showBleAlert && bleConnectionState != BleConnectionState.CONNECTED) {
                    BleConnectionAlert(
                        state = bleConnectionState,
                        onDismiss = { showBleAlert = false },
                        onRetry = { 
                            viewModel.retryConnection()
                        }
                    )
                }
                
                // Welcome Text
                val userName = userInfo?.firstName?.uppercase() ?: "UTENTE"
                Text(
                    "CIAO $userName 👋",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                // Parameters Grid 2x2
                ParametersGrid(
                    navController = navController,
                    latestMeasurements = latestMeasurements,
                    parameterStatuses = parameterStatuses
                )
            }
        }
        
        // Patient Info Dialog
        if (showPatientDialog) {
            PatientInfoDialog(
                userInfo = userInfo,
                onDismiss = { showPatientDialog = false }
            )
        }
    }
}

@Composable
fun PatientInfoDialog(
    userInfo: com.cerotek.imonitor.util.SecurePreferences.UserInfo?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    "Informazioni Paziente",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (userInfo != null) {
                    // Nome
                    InfoRow(
                        label = "Nome",
                        value = userInfo.firstName,
                        icon = Icons.Default.Person
                    )
                    
                    HorizontalDivider()
                    
                    // Cognome
                    InfoRow(
                        label = "Cognome",
                        value = userInfo.lastName,
                        icon = Icons.Default.Person
                    )
                    
                    HorizontalDivider()
                    
                    // Età
                    InfoRow(
                        label = "Età",
                        value = "${userInfo.age} anni",
                        icon = Icons.Default.Cake
                    )
                    
                    HorizontalDivider()
                    
                    // Patologia
                    InfoRow(
                        label = "Patologia",
                        value = userInfo.pathology,
                        icon = Icons.Default.MedicalServices
                    )
                } else {
                    Text(
                        "Nessuna informazione disponibile",
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Chiudi", color = PrimaryBlue, fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
            Text(
                label,
                fontSize = 14.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            value,
            fontSize = 16.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun BleConnectionAlert(
    state: BleConnectionState,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    val (backgroundColor, icon, title, message) = when (state) {
        BleConnectionState.DISCONNECTED -> Tuple4(
            Color(0xFFFF9800), // Orange
            Icons.Default.BluetoothDisabled,
            "Smartwatch Disconnesso",
            "Lo smartwatch si è disconnesso. Riconnetti per continuare il monitoraggio."
        )
        BleConnectionState.NOT_FOUND -> Tuple4(
            StatusRed,
            Icons.Default.BluetoothSearching,
            "Smartwatch Non Trovato",
            "Impossibile trovare lo smartwatch. Assicurati che sia acceso e nelle vicinanze."
        )
        BleConnectionState.SEARCHING -> Tuple4(
            StatusBlue,
            Icons.Default.Bluetooth,
            "Ricerca in corso...",
            "Ricerca dello smartwatch in corso. Attendere..."
        )
        else -> return
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        message,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 18.sp
                    )
                }
            }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state != BleConnectionState.SEARCHING) {
                    IconButton(
                        onClick = onRetry,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Riprova",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Chiudi",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun DrawerContent(
    userInfo: com.cerotek.imonitor.util.SecurePreferences.UserInfo?,
    onNavigate: (String) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header Migliorato
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(PrimaryBlue, PrimaryBlueDark)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Avatar Placeholder
                    Surface(
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = Color.White,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    
                    Column {
                        val userName = userInfo?.firstName ?: "Utente"
                        Text(
                            "Ciao $userName",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "iMonitor System",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            
            // Contenuto Scrollabile
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp, horizontal = 12.dp)
            ) {
                // Sezione Monitoraggio
                Text(
                    "MONITORAGGIO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                )
                
                DrawerMenuItem(
                    icon = Icons.Default.Home,
                    title = "Dashboard",
                    onClick = { /* Already on home */ }
                )
                
                DrawerMenuItem(
                    icon = Icons.Default.WatchLater,
                    title = "Smartwatch",
                    onClick = { onNavigate(Screen.Smartwatch.route) }
                )
                
                DrawerMenuItem(
                    icon = Icons.Default.History,
                    title = "Storico",
                    onClick = { onNavigate(Screen.Storico.route) }
                )
                
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                
                // Sezione Sistema
                Text(
                    "SISTEMA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                )
                
                DrawerMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Impostazioni",
                    onClick = { onNavigate(Screen.Settings.route) }
                )
                
                DrawerMenuItem(
                    icon = Icons.Default.Tune,
                    title = "Soglie Parametri",
                    onClick = { onNavigate(Screen.Thresholds.route) }
                )
                
                DrawerMenuItem(
                    icon = Icons.Default.Update,
                    title = "Aggiornamenti",
                    onClick = { onNavigate(Screen.Updates.route) }
                )
            }
            
            // Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Versione 1.2.0",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        "© 2026 Cerotek S.r.l.",
                        fontSize = 10.sp,
                        color = TextSecondary.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = title) },
        label = { Text(title, fontSize = 16.sp) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(vertical = 4.dp),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            selectedContainerColor = PrimaryBlue.copy(alpha = 0.1f)
        )
    )
}

@Composable
fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        listOf(PrimaryBlue, PrimaryBlueLight)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    "👋 Benvenuto!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Monitora la tua salute in tempo reale",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun ParametersGrid(
    navController: NavController,
    latestMeasurements: AllDataBean?,
    parameterStatuses: Map<String, ParameterStatus>
) {
    // Definizione parametri con dati reali o placeholder
    val parameters = listOf(
        ParameterItem(
            name = "FREQUENZA\nCARDIACA",
            value = latestMeasurements?.heartRate?.toString() ?: "--",
            unit = "bpm",
            iconRes = null,
            status = parameterStatuses["heart_rate"] ?: ParameterStatus.NORMAL,
            type = "heart_rate"
        ),
        ParameterItem(
            name = "GLICEMIA",
            value = latestMeasurements?.bloodSugarFloat?.let { "%.1f".format(it) } ?: "--",
            unit = "mg/dL",
            iconRes = R.drawable.ic_glicemia,
            status = parameterStatuses["blood_sugar"] ?: ParameterStatus.NORMAL,
            type = "blood_sugar"
        ),
        ParameterItem(
            name = "GRASSI NEL\nCORPO",
            value = latestMeasurements?.let { 
                val bodyFat = it.bodyFatInt + (it.bodyFatFloat / 10f)
                "%.1f".format(bodyFat)
            } ?: "--",
            unit = "%",
            iconRes = R.drawable.ic_grassi,
            status = parameterStatuses["body_fat"] ?: ParameterStatus.NORMAL,
            type = "body_fat"
        ),
        ParameterItem(
            name = "PRESSIONE\nSANGUIGNA",
            value = latestMeasurements?.let { "${it.systolic}/${it.diastolic}" } ?: "--",
            unit = "mmHg",
            iconRes = R.drawable.ic_pressione,
            status = parameterStatuses["blood_pressure"] ?: ParameterStatus.NORMAL,
            type = "blood_pressure"
        ),
        ParameterItem(
            name = "TEMPERATURA",
            value = latestMeasurements?.bodyTemperature?.let { "%.1f".format(it) } ?: "--",
            unit = "°C",
            iconRes = R.drawable.ic_temperatura,
            status = parameterStatuses["temperature"] ?: ParameterStatus.NORMAL,
            type = "temperature"
        ),
        ParameterItem(
            name = "SATURAZIONE",
            value = latestMeasurements?.oxygenSaturation?.toString() ?: "--",
            unit = "%",
            iconRes = R.drawable.ic_saturazione,
            status = parameterStatuses["oxygen_saturation"] ?: ParameterStatus.NORMAL,
            type = "oxygen_saturation"
        )
    )
    
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        parameters.chunked(2).forEach { rowParams ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowParams.forEach { param ->
                    BigParameterCard(
                        parameter = param,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.ParameterDetail.createRoute(param.type)) }
                    )
                }
            }
        }
    }
}

data class ParameterItem(
    val name: String,
    val value: String,
    val unit: String,
    val iconRes: Int?,
    val status: ParameterStatus,
    val type: String
)


@Composable
fun BigParameterCard(
    parameter: ParameterItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Determina il gradiente in base allo stato o al tipo
    val gradientColors = when (parameter.status) {
        ParameterStatus.ALERT -> listOf(RedGradientStart, RedGradientEnd)
        ParameterStatus.WARNING -> listOf(YellowGradientStart, YellowGradientEnd)
        ParameterStatus.NORMAL -> when (parameter.type) {
            "heart_rate", "blood_pressure" -> listOf(Color(0xFFFFF0F0), Color(0xFFFFEBEB)) // Light Red
            "oxygen_saturation", "temperature" -> listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9)) // Light Green
            "blood_sugar", "body_fat" -> listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3)) // Light Orange
            else -> listOf(Color.White, Color(0xFFF5F5F5))
        }
    }
    
    val textColor = when (parameter.status) {
        ParameterStatus.ALERT -> Color.White
        ParameterStatus.WARNING -> Color(0xFF5D4037) // Dark Brown for contrast on yellow
        ParameterStatus.NORMAL -> TextPrimary
    }
    
    val iconTint = when (parameter.status) {
        ParameterStatus.ALERT -> Color.White
        ParameterStatus.WARNING -> Color(0xFF5D4037)
        ParameterStatus.NORMAL -> PrimaryBlue
    }
    
    Card(
        onClick = onClick,
        modifier = modifier
            .height(160.dp) // Leggermente più compatto
            .shadow(
                elevation = if (parameter.status == ParameterStatus.NORMAL) 4.dp else 8.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(gradientColors)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween, // Spazia meglio gli elementi
                modifier = Modifier.fillMaxSize()
            ) {
                // Header: Icona e Nome
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (parameter.iconRes != null) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = parameter.iconRes),
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(48.dp)
                        )
                    } else if (parameter.type == "heart_rate") {
                         Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        parameter.name.replace("\n", " "), // Su una riga se possibile
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }
                
                // Valore
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = if (parameter.value != "--") parameter.value else "--",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor
                    )
                    if (parameter.unit.isNotEmpty()) {
                        Text(
                            text = parameter.unit,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
