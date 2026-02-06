package com.cerotek.imonitor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * GLASSMORPHISM EFFECT
 * Effetto vetro sfumato moderno con blur e trasparenza
 */
@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White.copy(alpha = 0.1f),
    borderColor: Color = Color.White.copy(alpha = 0.2f),
    blurRadius: Dp = 10.dp,
    cornerRadius: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = 0.15f),
                        backgroundColor.copy(alpha = 0.05f)
                    )
                )
            )
            .drawBehind {
                // Bordo superiore più chiaro (effetto luce)
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 2f
                )
                // Bordo sinistro
                drawLine(
                    color = borderColor.copy(alpha = 0.5f),
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 1f
                )
            }
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            content()
        }
    }
}

/**
 * NEUMORPHISM EFFECT
 * Effetto soft UI con ombre interne ed esterne
 */
@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    isPressed: Boolean = false,
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 8.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val lightShadow = Color.White.copy(alpha = 0.7f)
    val darkShadow = Color.Black.copy(alpha = 0.15f)
    
    Box(
        modifier = modifier
            .graphicsLayer {
                shadowElevation = if (isPressed) 2.dp.toPx() else elevation.toPx()
                shape = RoundedCornerShape(cornerRadius)
                clip = true
            }
            .background(backgroundColor)
            .drawBehind {
                if (!isPressed) {
                    // Ombra chiara (top-left)
                    drawCircle(
                        color = lightShadow,
                        radius = size.minDimension / 3,
                        center = Offset(-size.width * 0.1f, -size.height * 0.1f)
                    )
                    // Ombra scura (bottom-right)
                    drawCircle(
                        color = darkShadow,
                        radius = size.minDimension / 3,
                        center = Offset(size.width * 1.1f, size.height * 1.1f)
                    )
                }
            }
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            content()
        }
    }
}

/**
 * GRADIENT MESH BACKGROUND
 * Sfondo con gradiente mesh moderno
 */
@Composable
fun GradientMeshBackground(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color(0xFF667EEA),
        Color(0xFF764BA2),
        Color(0xFFF093FB),
        Color(0xFF4FACFE)
    )
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.radialGradient(
                    colors = colors,
                    center = Offset(0.3f, 0.3f),
                    radius = 1000f
                )
            )
    )
}

/**
 * FROSTED GLASS EFFECT
 * Effetto vetro smerigliato con blur
 */
@Composable
fun FrostedGlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White.copy(alpha = 0.2f),
    blurRadius: Dp = 16.dp,
    cornerRadius: Dp = 20.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .blur(
                radius = blurRadius,
                edgeTreatment = BlurredEdgeTreatment.Unbounded
            ),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            content()
        }
    }
}

/**
 * GRADIENT BORDER CARD
 * Card con bordo gradiente animato
 */
@Composable
fun GradientBorderCard(
    modifier: Modifier = Modifier,
    borderWidth: Dp = 2.dp,
    gradientColors: List<Color> = listOf(
        Color(0xFF667EEA),
        Color(0xFF764BA2)
    ),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    cornerRadius: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.linearGradient(gradientColors)
            )
            .padding(borderWidth)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius - borderWidth))
                .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                content()
            }
        }
    }
}

/**
 * ELEVATED SHADOW CARD
 * Card con ombra elevata e moderna
 */
@Composable
fun ElevatedShadowCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 12.dp,
    shadowColor: Color = Color.Black.copy(alpha = 0.1f),
    cornerRadius: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = shadowColor,
                spotColor = shadowColor
            ),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            content()
        }
    }
}

/**
 * HOLOGRAPHIC CARD
 * Card con effetto olografico/iridescente
 */
@Composable
fun HolographicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val holographicColors = listOf(
        Color(0xFFFF6B9D),
        Color(0xFFC371F5),
        Color(0xFF4FACFE),
        Color(0xFF00F2FE),
        Color(0xFFFEC163)
    )
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.linearGradient(
                    colors = holographicColors,
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            )
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius - 1.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                content()
            }
        }
    }
}

/**
 * NEON GLOW CARD
 * Card con effetto neon luminoso
 */
@Composable
fun NeonGlowCard(
    modifier: Modifier = Modifier,
    glowColor: Color = Color(0xFF00F5FF),
    cornerRadius: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = glowColor.copy(alpha = 0.5f),
                spotColor = glowColor.copy(alpha = 0.8f)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        glowColor.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                )
            )
            .drawBehind {
                // Bordo neon
                drawRoundRect(
                    color = glowColor,
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                        cornerRadius.toPx()
                    ),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                )
            }
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            content()
        }
    }
}

/**
 * MATERIAL YOU CARD
 * Card con stile Material You (Material 3)
 */
@Composable
fun MaterialYouCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    cornerRadius: Dp = 28.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            content()
        }
    }
}

/**
 * FLOATING CARD
 * Card fluttuante con ombra dinamica
 */
@Composable
fun FloatingCard(
    modifier: Modifier = Modifier,
    isHovered: Boolean = false,
    cornerRadius: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .graphicsLayer {
                shadowElevation = if (isHovered) 24.dp.toPx() else 8.dp.toPx()
                translationY = if (isHovered) -8.dp.toPx() else 0f
            },
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 24.dp else 8.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            content()
        }
    }
}
