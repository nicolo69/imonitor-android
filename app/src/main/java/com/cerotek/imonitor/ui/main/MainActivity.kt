package com.cerotek.imonitor.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.cerotek.imonitor.ui.navigation.MainNavigation
import com.cerotek.imonitor.ui.theme.IMonitorTheme
import com.cerotek.imonitor.util.PermissionManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.cerotek.imonitor.IMonitorApplication

class MainActivity : ComponentActivity() {
    
    private lateinit var permissionManager: PermissionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        permissionManager = PermissionManager(this)
        
        // Richiedi permessi BLE all'avvio
        permissionManager.requestBlePermissions { granted ->
            if (granted) {
                Toast.makeText(
                    this,
                    "Permessi Bluetooth concessi",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Permessi Bluetooth necessari per il funzionamento dell'app",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        
        // Richiedi permessi notifiche (Android 13+)
        permissionManager.requestNotificationPermission { granted ->
            if (!granted) {
                Toast.makeText(
                    this,
                    "Permesso notifiche consigliato per gli alert",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        
        setContent {
            val settingsManager = (application as IMonitorApplication).settingsManager
            val isDarkTheme by settingsManager.isDarkThemeFlow.collectAsState()
            
            IMonitorTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }
}