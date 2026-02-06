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
 * ESEMPI DI UTILIZZO DEGLI EFFETTI VISIVI MODERNI
 * 
 * Questo file mostra come usare tutti gli effetti visivi nella tua app.
 */

// ============================================
// ESEMPIO 1: Glassmorphism Card
// ============================================
@Composable
fun ExampleGlassmorphism() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667EEA),
                        Color(0xFF764BA2)
                    )
                )
            )
    ) {
        GlassmorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            backgroundColor = Color.White.copy(alpha = 0.1f),
            borderColor = Color.White.copy(alpha = 0.3f)
        ) {
            Text(
                text = "Glassmorphism Card",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Effetto vetro sfumato moderno",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

// ============================================
// ESEMPIO 2: Neumorphism Card
// ============================================
@Composable
fun ExampleNeumorphism() {
    var isPressed by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0E5EC))
            .padding(16.dp)
    ) {
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFFE0E5EC),
            isPressed = isPressed
        ) {
            Text(
                text = "Neumorphism Card",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Soft UI con ombre morbide",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { isPressed = !isPressed }) {
                Text(if (isPressed) "Rilascia" else "Premi")
            }
        }
    }
}

// ============================================
// ESEMPIO 3: Gradient Border Card
// ============================================
@Composable
fun ExampleGradientBorder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GradientBorderCard(
            modifier = Modifier.fillMaxWidth(),
            gradientColors = listOf(
                Color(0xFF667EEA),
                Color(0xFF764BA2)
            )
        ) {
            Text(
                text = "Pressione Sanguigna",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "120/80 mmHg",
                style = MaterialTheme.typography.headlineMedium,
                color = StatusGreen
            )
        }
        
        GradientBorderCard(
            modifier = Modifier.fillMaxWidth(),
            gradientColors = listOf(
                Color(0xFFFF6B9D),
                Color(0xFFC371F5)
            )
        ) {
            Text(
                text = "Frequenza Cardiaca",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "72 bpm",
                style = MaterialTheme.typography.headlineMedium,
                color = StatusBlue
            )
        }
    }
}

// ============================================
// ESEMPIO 4: Holographic Card
// ============================================
@Composable
fun ExampleHolographic() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        HolographicCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "🎉 Obiettivo Raggiunto!",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hai completato 10.000 passi oggi",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

// ============================================
// ESEMPIO 5: Neon Glow Card
// ============================================
@Composable
fun ExampleNeonGlow() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E27))
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NeonGlowCard(
                modifier = Modifier.fillMaxWidth(),
                glowColor = Color(0xFF00F5FF)
            ) {
                Text(
                    text = "Saturazione O₂",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "98%",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFF00F5FF)
                )
            }
            
            NeonGlowCard(
                modifier = Modifier.fillMaxWidth(),
                glowColor = Color(0xFFFF006E)
            ) {
                Text(
                    text = "Temperatura",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "36.5°C",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFFFF006E)
                )
            }
        }
    }
}

// ============================================
// ESEMPIO 6: Parallax Effect
// ============================================
@Composable
fun ExampleParallax() {
    val scrollState = rememberScrollState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Layer di sfondo con parallax
        ParallaxBox(
            modifier = Modifier.fillMaxSize(),
            scrollOffset = scrollState.value.toFloat(),
            parallaxFactor = 0.3f
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF667EEA),
                                Color(0xFF764BA2)
                            )
                        )
                    )
            )
        }
        
        // Contenuto scrollabile
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(10) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Card $index")
                    }
                }
            }
        }
    }
}

// ============================================
// ESEMPIO 7: Confetti Effect
// ============================================
@Composable
fun ExampleConfetti() {
    var showConfetti by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = { showConfetti = !showConfetti }) {
                Text(if (showConfetti) "Ferma Confetti" else "Lancia Confetti")
            }
        }
        
        ConfettiEffect(
            modifier = Modifier.fillMaxSize(),
            isActive = showConfetti
        )
    }
}

// ============================================
// ESEMPIO 8: Floating Particles
// ============================================
@Composable
fun ExampleFloatingParticles() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
    ) {
        FloatingParticles(
            modifier = Modifier.fillMaxSize(),
            particleCount = 30,
            particleColor = Color.White.copy(alpha = 0.3f)
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Particelle Fluttuanti",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }
    }
}

// ============================================
// ESEMPIO 9: Tilt Card (3D)
// ============================================
@Composable
fun ExampleTiltCard() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        TiltCard(
            modifier = Modifier
                .width(300.dp)
                .height(200.dp),
            maxTilt = 15f
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryBlue
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Trascina per inclinare",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ============================================
// ESEMPIO 10: Bubble Effect
// ============================================
@Composable
fun ExampleBubbles() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4FACFE),
                        Color(0xFF00F2FE)
                    )
                )
            )
    ) {
        BubbleEffect(
            modifier = Modifier.fillMaxSize(),
            bubbleCount = 15,
            bubbleColor = Color.White.copy(alpha = 0.3f)
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Effetto Bolle",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }
    }
}

// ============================================
// ESEMPIO 11: Wave Background
// ============================================
@Composable
fun ExampleWaveBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        WaveBackground(
            modifier = Modifier.fillMaxSize(),
            waveColor = PrimaryBlue,
            waveCount = 3
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Onde Animate",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

// ============================================
// ESEMPIO 12: Combinazione Effetti
// ============================================
@Composable
fun ExampleCombinedEffects() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E27))
    ) {
        // Sfondo con stelle
        StarField(
            modifier = Modifier.fillMaxSize(),
            starCount = 100
        )
        
        // Particelle fluttuanti
        FloatingParticles(
            modifier = Modifier.fillMaxSize(),
            particleCount = 20,
            particleColor = Color(0xFF00F5FF).copy(alpha = 0.2f)
        )
        
        // Contenuto
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NeonGlowCard(
                modifier = Modifier.fillMaxWidth(),
                glowColor = Color(0xFF00F5FF)
            ) {
                Text(
                    text = "Dashboard Salute",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Text(
                    text = "Tutti i parametri nella norma",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GlassmorphicCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color.White.copy(alpha = 0.05f)
                ) {
                    Text(
                        text = "120/80",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Pressione",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                
                GlassmorphicCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color.White.copy(alpha = 0.05f)
                ) {
                    Text(
                        text = "72",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Text(
                        text = "BPM",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
