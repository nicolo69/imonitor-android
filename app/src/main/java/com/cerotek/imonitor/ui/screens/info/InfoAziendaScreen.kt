package com.cerotek.imonitor.ui.screens.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cerotek.imonitor.ui.theme.BackgroundLight
import com.cerotek.imonitor.ui.theme.PrimaryBlue
import com.cerotek.imonitor.ui.theme.TextPrimary

@Composable
fun InfoAziendaScreen(navController: NavController) {
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Informazioni Azienda",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                
                Divider(color = Color.LightGray, thickness = 1.dp)
                
                InfoRow(label = "Nome Azienda", value = "Cerotek S.r.l.")
                InfoRow(label = "Indirizzo", value = "Via Example 123, Milano")
                InfoRow(label = "Telefono", value = "+39 02 1234567")
                InfoRow(label = "Email", value = "info@cerotek.it")
                InfoRow(label = "Sito Web", value = "www.cerotek.it")
                
                Divider(color = Color.LightGray, thickness = 1.dp)
                
                Text(
                    text = "iMonitor - Sistema di Monitoraggio Salute",
                    fontSize = 16.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "Versione 1.2.0",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 18.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Normal
        )
    }
}
