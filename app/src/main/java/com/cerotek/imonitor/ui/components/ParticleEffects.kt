package com.cerotek.imonitor.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * CONFETTI EFFECT
 * Effetto coriandoli per celebrazioni
 */
@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    particleCount: Int = 50,
    colors: List<Color> = listOf(
        Color(0xFFFF6B9D),
        Color(0xFFC371F5),
        Color(0xFF4FACFE),
        Color(0xFFFEC163),
        Color(0xFF43E97B)
    )
) {
    if (!isActive) return
    
    val particles = remember {
        List(particleCount) {
            ConfettiParticle(
                color = colors.random(),
                startX = Random.nextFloat(),
                startY = -0.1f,
                velocityX = Random.nextFloat() * 2f - 1f,
                velocityY = Random.nextFloat() * 2f + 2f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 10f - 5f
            )
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confettiProgress"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val x = size.width * (particle.startX + particle.velocityX * progress * 0.1f)
            val y = size.height * (particle.startY + particle.velocityY * progress * 0.5f)
            val rotation = particle.rotation + particle.rotationSpeed * progress * 100f
            
            if (y < size.height) {
                rotate(rotation, pivot = Offset(x, y)) {
                    drawRect(
                        color = particle.color,
                        topLeft = Offset(x - 5f, y - 10f),
                        size = androidx.compose.ui.geometry.Size(10f, 20f)
                    )
                }
            }
        }
    }
}

data class ConfettiParticle(
    val color: Color,
    val startX: Float,
    val startY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val rotation: Float,
    val rotationSpeed: Float
)

/**
 * FLOATING PARTICLES
 * Particelle fluttuanti sullo sfondo
 */
@Composable
fun FloatingParticles(
    modifier: Modifier = Modifier,
    particleCount: Int = 20,
    particleColor: Color = Color.White.copy(alpha = 0.3f),
    particleSize: Float = 4f
) {
    val particles = remember {
        List(particleCount) {
            FloatingParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = Random.nextFloat() * 0.5f + 0.2f,
                size = particleSize * (Random.nextFloat() * 0.5f + 0.5f)
            )
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleTime"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val x = size.width * particle.x
            val y = (size.height * particle.y + size.height * particle.speed * time) % size.height
            
            drawCircle(
                color = particleColor,
                radius = particle.size,
                center = Offset(x, y)
            )
        }
    }
}

data class FloatingParticle(
    val x: Float,
    val y: Float,
    val speed: Float,
    val size: Float
)

/**
 * SPARKLE EFFECT
 * Effetto scintillio/stelle
 */
@Composable
fun SparkleEffect(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    sparkleCount: Int = 15,
    sparkleColor: Color = Color(0xFFFFD700)
) {
    if (!isActive) return
    
    val sparkles = remember {
        List(sparkleCount) {
            Sparkle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                delay = Random.nextInt(1000),
                duration = Random.nextInt(500) + 500
            )
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkleTime"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        sparkles.forEach { sparkle ->
            // Calcola alpha basato sul tempo e delay
            val adjustedTime = (time * 2000 - sparkle.delay).coerceAtLeast(0f)
            val cycle = (adjustedTime % sparkle.duration) / sparkle.duration
            val alpha = if (cycle < 0.5f) cycle * 2f else (1f - cycle) * 2f
            
            val x = size.width * sparkle.x
            val y = size.height * sparkle.y
            
            // Disegna stella a 4 punte
            val path = Path().apply {
                moveTo(x, y - 10f)
                lineTo(x + 2f, y - 2f)
                lineTo(x + 10f, y)
                lineTo(x + 2f, y + 2f)
                lineTo(x, y + 10f)
                lineTo(x - 2f, y + 2f)
                lineTo(x - 10f, y)
                lineTo(x - 2f, y - 2f)
                close()
            }
            
            drawPath(
                path = path,
                color = sparkleColor.copy(alpha = alpha)
            )
        }
    }
}

data class Sparkle(
    val x: Float,
    val y: Float,
    val delay: Int,
    val duration: Int
)

/**
 * BUBBLE EFFECT
 * Effetto bolle che salgono
 */
@Composable
fun BubbleEffect(
    modifier: Modifier = Modifier,
    bubbleCount: Int = 10,
    bubbleColor: Color = Color.White.copy(alpha = 0.2f)
) {
    val bubbles = remember {
        List(bubbleCount) {
            Bubble(
                x = Random.nextFloat(),
                startY = 1f + Random.nextFloat() * 0.2f,
                speed = Random.nextFloat() * 0.3f + 0.1f,
                size = Random.nextFloat() * 20f + 10f,
                wobble = Random.nextFloat() * 0.1f
            )
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "bubbles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubbleTime"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        bubbles.forEach { bubble ->
            val baseX = size.width * bubble.x
            val wobbleOffset = sin(time * 10f + bubble.x * 10f) * bubble.wobble * size.width
            val x = baseX + wobbleOffset
            val y = size.height * (bubble.startY - bubble.speed * time)
            
            if (y > -bubble.size && y < size.height + bubble.size) {
                drawCircle(
                    color = bubbleColor,
                    radius = bubble.size,
                    center = Offset(x, y)
                )
                // Highlight per effetto 3D
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    radius = bubble.size * 0.3f,
                    center = Offset(x - bubble.size * 0.3f, y - bubble.size * 0.3f)
                )
            }
        }
    }
}

data class Bubble(
    val x: Float,
    val startY: Float,
    val speed: Float,
    val size: Float,
    val wobble: Float
)

/**
 * WAVE BACKGROUND
 * Sfondo con onde animate
 */
@Composable
fun WaveBackground(
    modifier: Modifier = Modifier,
    waveColor: Color = Color(0xFF667EEA).copy(alpha = 0.3f),
    waveCount: Int = 3
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waves")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        repeat(waveCount) { index ->
            val path = Path()
            val amplitude = size.height * 0.1f
            val frequency = 0.01f
            val phaseShift = phase + (index * 120f)
            
            path.moveTo(0f, size.height * 0.5f)
            
            for (x in 0..size.width.toInt() step 10) {
                val y = size.height * 0.5f + 
                        amplitude * sin((x * frequency + phaseShift) * Math.PI / 180f).toFloat()
                path.lineTo(x.toFloat(), y)
            }
            
            path.lineTo(size.width, size.height)
            path.lineTo(0f, size.height)
            path.close()
            
            drawPath(
                path = path,
                color = waveColor.copy(alpha = 0.2f - index * 0.05f)
            )
        }
    }
}

/**
 * STAR FIELD
 * Campo stellare animato
 */
@Composable
fun StarField(
    modifier: Modifier = Modifier,
    starCount: Int = 100,
    starColor: Color = Color.White
) {
    val stars = remember {
        List(starCount) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 2f + 1f,
                twinkleSpeed = Random.nextFloat() * 2f + 1f,
                twinkleOffset = Random.nextFloat()
            )
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "starTime"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        stars.forEach { star ->
            // Calcola alpha con offset per ogni stella
            val cycle = ((time + star.twinkleOffset) * star.twinkleSpeed) % 1f
            val alpha = 0.3f + (sin(cycle * 2f * Math.PI.toFloat()) * 0.35f + 0.35f)
            
            drawCircle(
                color = starColor.copy(alpha = alpha),
                radius = star.size,
                center = Offset(size.width * star.x, size.height * star.y)
            )
        }
    }
}

data class Star(
    val x: Float,
    val y: Float,
    val size: Float,
    val twinkleSpeed: Float,
    val twinkleOffset: Float
)
