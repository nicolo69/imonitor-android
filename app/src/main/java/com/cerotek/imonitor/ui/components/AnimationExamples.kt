package com.cerotek.imonitor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cerotek.imonitor.ui.theme.*

/**
 * ESEMPI DI UTILIZZO DELLE ANIMAZIONI
 * 
 * Questo file mostra come usare i componenti animati nella tua app.
 * Puoi copiare questi esempi nelle tue schermate.
 */

// ============================================
// ESEMPIO 1: Card animate con stagger effect
// ============================================
@Composable
fun ExampleAnimatedCards() {
    val items = remember { listOf("Pressione", "Glicemia", "Saturazione", "Temperatura") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEachIndexed { index, item ->
            AnimatedCard(
                visible = true,
                delayMillis = index * 100, // Stagger di 100ms tra le card
                onClick = { /* Azione al click */ }
            ) {
                Text(
                    text = item,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Valore: 120/80",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

// ============================================
// ESEMPIO 2: Pulsanti animati
// ============================================
@Composable
fun ExampleAnimatedButtons() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Pulsante con bounce effect
        BounceButton(
            onClick = { /* Azione */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pulsante Bounce")
        }
        
        // Pulsante con gradiente animato
        AnimatedGradientButton(
            onClick = { /* Azione */ },
            modifier = Modifier.fillMaxWidth(),
            gradientColors = listOf(InfoButtonStart, InfoButtonEnd)
        ) {
            Text(
                text = "Gradiente Animato",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        // Pulsante con pulse per azioni importanti
        PulseButton(
            onClick = { /* Azione */ },
            modifier = Modifier.fillMaxWidth(),
            shouldPulse = true,
            colors = ButtonDefaults.buttonColors(
                containerColor = StatusRed
            )
        ) {
            Text("SOS - Emergenza")
        }
        
        // Pulsante con shimmer durante loading
        var isLoading by remember { mutableStateOf(false) }
        ShimmerButton(
            onClick = {
                isLoading = true
                // Simula operazione asincrona
            },
            modifier = Modifier.fillMaxWidth(),
            isLoading = isLoading,
            backgroundColor = PrimaryBlue
        ) {
            Text(if (isLoading) "Caricamento..." else "Sincronizza")
        }
    }
}

// ============================================
// ESEMPIO 3: Lista animata con stagger
// ============================================
@Composable
fun ExampleAnimatedList() {
    val measurements = remember {
        listOf(
            "Pressione: 120/80 mmHg",
            "Glicemia: 95 mg/dL",
            "Saturazione: 98%",
            "Temperatura: 36.5°C",
            "Frequenza: 72 bpm"
        )
    }
    
    AnimatedLazyColumn(
        items = measurements,
        modifier = Modifier.fillMaxSize(),
        staggerDelay = 80, // Delay tra elementi
        contentPadding = PaddingValues(16.dp)
    ) { index, measurement ->
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CardBackground
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = measurement,
                    style = MaterialTheme.typography.bodyLarge
                )
                // Indicatore di stato
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(StatusGreen, shape = MaterialTheme.shapes.small)
                )
            }
        }
    }
}

// ============================================
// ESEMPIO 4: Card pulsante per alert
// ============================================
@Composable
fun ExamplePulsingAlert() {
    PulsingCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        minScale = 0.98f,
        maxScale = 1.02f
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "⚠️ Attenzione",
                    style = MaterialTheme.typography.titleMedium,
                    color = StatusRed
                )
                Text(
                    text = "Pressione elevata rilevata",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            Button(
                onClick = { /* Azione */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = StatusRed
                )
            ) {
                Text("Visualizza")
            }
        }
    }
}

// ============================================
// ESEMPIO 5: Loading states
// ============================================
@Composable
fun ExampleLoadingStates() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Stati di Caricamento",
            style = MaterialTheme.typography.headlineMedium
        )
        
        // Shimmer loading
        Card(modifier = Modifier.fillMaxWidth()) {
            ShimmerCard()
        }
        
        Divider()
        
        // Dots loading
        DotsLoading(
            dotColor = PrimaryBlue,
            dotSize = 16.dp
        )
        
        Divider()
        
        // Gradient spinner
        GradientSpinner(
            size = 60.dp,
            strokeWidth = 6.dp,
            colors = listOf(PrimaryBlue, InfoButtonStart, UpdatesButtonStart)
        )
        
        Divider()
        
        // Pulse loader
        PulseLoader(
            color = StatusGreen,
            size = 80.dp
        )
        
        Divider()
        
        // Wave loader
        WaveLoader(
            color = PrimaryBlue,
            barWidth = 6.dp,
            barCount = 5
        )
    }
}

// ============================================
// ESEMPIO 6: Card espandibile
// ============================================
@Composable
fun ExampleExpandableCard() {
    var expanded by remember { mutableStateOf(false) }
    
    ExpandableCard(
        expanded = expanded,
        onExpandChange = { expanded = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dettagli Misurazione",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (expanded) "▲" else "▼",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    ) {
        // Contenuto espandibile
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Data: 05/02/2026")
            Text("Ora: 14:30")
            Text("Dispositivo: Smartwatch")
            Text("Note: Misurazione dopo attività fisica")
        }
    }
}

// ============================================
// ESEMPIO 7: Grid animato
// ============================================
@Composable
fun ExampleAnimatedGrid() {
    val parameters = remember {
        listOf(
            "Pressione" to StatusRed,
            "Glicemia" to StatusYellow,
            "Saturazione" to StatusGreen,
            "Temperatura" to StatusBlue,
            "Frequenza" to StatusGreen,
            "Grassi" to StatusYellow
        )
    }
    
    AnimatedGrid(
        items = parameters,
        columns = 2,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        staggerDelay = 100
    ) { index, (name, color) ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            colors = CardDefaults.cardColors(
                containerColor = CardBackground
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color, shape = MaterialTheme.shapes.medium)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

// ============================================
// COME USARE NELLE TUE SCHERMATE:
// ============================================
/*

1. Importa i componenti necessari:
   import com.cerotek.imonitor.ui.components.*

2. Usa AnimatedCard per le card:
   AnimatedCard(
       visible = true,
       delayMillis = 0,
       onClick = { /* azione */ }
   ) {
       // Contenuto della card
   }

3. Usa BounceButton per i pulsanti:
   BounceButton(onClick = { /* azione */ }) {
       Text("Clicca qui")
   }

4. Usa AnimatedLazyColumn per le liste:
   AnimatedLazyColumn(
       items = listaDati,
       staggerDelay = 80
   ) { index, item ->
       // Contenuto elemento lista
   }

5. Usa i loader durante il caricamento:
   if (isLoading) {
       GradientSpinner()
   }

*/
