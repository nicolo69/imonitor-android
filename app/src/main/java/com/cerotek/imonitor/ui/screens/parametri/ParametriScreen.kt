package com.cerotek.imonitor.ui.screens.parametri

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cerotek.imonitor.ui.theme.*

data class HealthParameter(
    val name: String,
    val unit: String,
    val iconName: String,  // Nome file senza estensione
    val currentValue: String,
    val options: List<String>
)

@Composable
fun ParametriScreen(navController: NavController) {
    val parameters = remember {
        listOf(
            HealthParameter("Pressione Sanguigna", "mmHg", "pressione", "120/80", 
                listOf("Normale", "Elevata", "Alta", "Molto Alta")),
            HealthParameter("Saturazione", "%", "saturazione", "98", 
                listOf("Normale", "Bassa", "Critica")),
            HealthParameter("Battito Cardiaco", "bpm", "battito", "72", 
                listOf("Normale", "Elevato", "Molto Elevato")),
            HealthParameter("Temperatura", "°C", "temperatura", "36.5", 
                listOf("Normale", "Febbre Lieve", "Febbre Alta")),
            HealthParameter("Glicemia", "mg/dL", "glicemia", "95", 
                listOf("Normale", "Alta", "Molto Alta")),
            HealthParameter("Grassi Corpo", "%", "grassi", "22", 
                listOf("Normale", "Elevato", "Molto Elevato"))
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp)
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
        
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Text("⚕️", fontSize = 48.sp)
                Text(
                    text = "Parametri Salute",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryBlue
                )
            }
            Text(
                text = "Monitora i tuoi parametri vitali",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
        
        // Parameters Grid (2 columns)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            parameters.chunked(2).forEach { rowParams ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    rowParams.forEach { param ->
                        ParameterCard(
                            parameter = param,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill empty space if odd number
                    if (rowParams.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ParameterCard(
    parameter: HealthParameter,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(parameter.options[0]) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon and Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier.size(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (parameter.name) {
                            "Pressione Sanguigna" -> "🩸"
                            "Saturazione" -> "🫁"
                            "Battito Cardiaco" -> "❤️"
                            "Temperatura" -> "🌡️"
                            "Glicemia" -> "🍬"
                            "Grassi Corpo" -> "⚖️"
                            else -> "📊"
                        },
                        fontSize = 70.sp
                    )
                }
                
                Text(
                    text = parameter.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryBlue,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
                
                Text(
                    text = parameter.unit,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }
            
            // Dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { expanded = !expanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BackgroundLight
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = parameter.currentValue,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryBlue
                    )
                }
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.4f)
                ) {
                    parameter.options.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            onClick = {
                                selectedOption = option
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
