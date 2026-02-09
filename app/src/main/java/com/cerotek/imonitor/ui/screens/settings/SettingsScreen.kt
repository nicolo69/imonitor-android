package com.cerotek.imonitor.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cerotek.imonitor.R
import com.cerotek.imonitor.ui.components.InstagramIcon
import com.cerotek.imonitor.ui.components.LinkedInIcon
import com.cerotek.imonitor.ui.components.WhatsAppIcon
import com.cerotek.imonitor.ui.navigation.Screen
import com.cerotek.imonitor.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE) }
    
    var volume by remember { mutableStateOf(sharedPrefs.getFloat("volume", 50f)) }
    var isDarkTheme by remember { mutableStateOf(sharedPrefs.getBoolean("dark_theme", false)) }
    
    Scaffold(
        // topBar rimosso per usare header custom

    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
        ) {
            // Header Custom con Gradiente
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.White,
                            containerColor = Color.White.copy(alpha = 0.2f)
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro")
                    }
                    
                    Text(
                        "Impostazioni",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
            // Logo Cerotek e Social Media
            CerotekBrandingSection()
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Sezione Accessibilità
            Text(
                "ACCESSIBILITÀ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(start = 4.dp)
            )
            
            // Tema Scuro
            SettingCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
                                if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    "Tema Scuro",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    "Riduce l'affaticamento visivo",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { 
                                isDarkTheme = it
                                sharedPrefs.edit().putBoolean("dark_theme", it).apply()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrimaryBlue,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.LightGray
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Sezione Audio
            Text(
                "AUDIO",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(start = 4.dp)
            )
            
            // Volume Control
            SettingCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                "Volume Notifiche",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                "${volume.toInt()}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                    }
                    
                    Slider(
                        value = volume,
                        onValueChange = { 
                            volume = it
                            sharedPrefs.edit().putFloat("volume", it).apply()
                        },
                        valueRange = 0f..100f,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryBlue,
                            activeTrackColor = PrimaryBlue,
                            inactiveTrackColor = Color.LightGray
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Sezione Navigazione
            Text(
                "NAVIGAZIONE",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(start = 4.dp)
            )
            
            // Aggiornamenti Button
            NavigationButton(
                title = "Aggiornamenti",
                icon = Icons.Default.Update,
                gradient = listOf(UpdatesButtonStart, UpdatesButtonEnd),
                onClick = { navController.navigate(Screen.Updates.route) }
            )
            
            // Smartwatch Button
            NavigationButton(
                title = "Smartwatch",
                icon = Icons.Default.WatchLater,
                gradient = listOf(WatchButtonStart, WatchButtonEnd),
                onClick = { navController.navigate(Screen.Smartwatch.route) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
}
@Composable
fun CerotekBrandingSection() {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlueDark), // Scuro per contrasto premium
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Logo Cerotek su sfondo bianco circolare
            Surface(
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier
                    .size(100.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cerotek.it/"))
                        context.startActivity(intent)
                    },
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentDescription = "Cerotek Logo",
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "SEGUICI SUI SOCIAL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Social Media Icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Instagram
                    InstagramIcon(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/cerotek"))
                            context.startActivity(intent)
                        }
                    )
                    
                    Spacer(modifier = Modifier.width(24.dp))
                    
                    // LinkedIn
                    LinkedInIcon(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/company/cerotek"))
                            context.startActivity(intent)
                        }
                    )
                    
                    Spacer(modifier = Modifier.width(24.dp))
                    
                    // WhatsApp
                    WhatsAppIcon(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/393123456789"))
                            context.startActivity(intent)
                        }
                    )
                }
            }
            
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            
            Text(
                "Cerotek © 2026",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun SettingCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

@Composable
fun NavigationButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(gradient)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
