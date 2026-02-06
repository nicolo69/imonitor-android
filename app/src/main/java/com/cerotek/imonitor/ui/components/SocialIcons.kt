package com.cerotek.imonitor.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Logo Instagram ufficiale con gradiente
 */
@Composable
fun InstagramIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF833AB4), // Viola
                        Color(0xFFC13584), // Rosa
                        Color(0xFFE1306C), // Rosa chiaro
                        Color(0xFFFD1D1D), // Rosso
                        Color(0xFFF77737)  // Arancione
                    ),
                    start = Offset(0f, Float.POSITIVE_INFINITY),
                    end = Offset(Float.POSITIVE_INFINITY, 0f)
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(32.dp)) {
            val strokeWidth = 2.5.dp.toPx()
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.width / 2 - strokeWidth
            
            // Quadrato arrotondato esterno
            val rect = RoundRect(
                rect = Rect(
                    left = strokeWidth,
                    top = strokeWidth,
                    right = size.width - strokeWidth,
                    bottom = size.height - strokeWidth
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
            )
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(strokeWidth, strokeWidth),
                size = androidx.compose.ui.geometry.Size(
                    size.width - strokeWidth * 2,
                    size.height - strokeWidth * 2
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()),
                style = Stroke(width = strokeWidth)
            )
            
            // Cerchio interno (fotocamera)
            drawCircle(
                color = Color.White,
                radius = radius * 0.45f,
                center = Offset(centerX, centerY),
                style = Stroke(width = strokeWidth)
            )
            
            // Punto in alto a destra (flash)
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = Offset(centerX + radius * 0.55f, centerY - radius * 0.55f)
            )
        }
    }
}

/**
 * Logo LinkedIn ufficiale
 */
@Composable
fun LinkedInIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0077B5))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(32.dp)) {
            val scale = size.width / 24f
            
            // Lettera "in" stilizzata
            
            // Quadrato piccolo (punto della i)
            drawRect(
                color = Color.White,
                topLeft = Offset(3f * scale, 3f * scale),
                size = androidx.compose.ui.geometry.Size(4f * scale, 4f * scale)
            )
            
            // Rettangolo verticale (gamba della i)
            drawRect(
                color = Color.White,
                topLeft = Offset(3f * scale, 9f * scale),
                size = androidx.compose.ui.geometry.Size(4f * scale, 12f * scale)
            )
            
            // Lettera "n" - gamba sinistra
            drawRect(
                color = Color.White,
                topLeft = Offset(9f * scale, 9f * scale),
                size = androidx.compose.ui.geometry.Size(4f * scale, 12f * scale)
            )
            
            // Lettera "n" - curva superiore
            drawRect(
                color = Color.White,
                topLeft = Offset(13f * scale, 9f * scale),
                size = androidx.compose.ui.geometry.Size(4f * scale, 4f * scale)
            )
            
            // Lettera "n" - gamba destra
            drawRect(
                color = Color.White,
                topLeft = Offset(17f * scale, 9f * scale),
                size = androidx.compose.ui.geometry.Size(4f * scale, 12f * scale)
            )
        }
    }
}

/**
 * Logo WhatsApp ufficiale
 */
@Composable
fun WhatsAppIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color(0xFF25D366))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(32.dp)) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.width / 2.5f
            
            // Cerchio esterno del telefono
            drawCircle(
                color = Color.White,
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2.5.dp.toPx())
            )
            
            // Cornetta del telefono (path semplificato)
            val path = Path().apply {
                // Parte superiore sinistra
                moveTo(centerX - radius * 0.5f, centerY - radius * 0.3f)
                lineTo(centerX - radius * 0.3f, centerY - radius * 0.5f)
                lineTo(centerX - radius * 0.1f, centerY - radius * 0.3f)
                
                // Curva centrale
                quadraticBezierTo(
                    centerX + radius * 0.1f, centerY - radius * 0.1f,
                    centerX + radius * 0.3f, centerY + radius * 0.1f
                )
                
                // Parte inferiore destra
                lineTo(centerX + radius * 0.5f, centerY + radius * 0.3f)
                lineTo(centerX + radius * 0.3f, centerY + radius * 0.5f)
                lineTo(centerX + radius * 0.1f, centerY + radius * 0.3f)
                
                // Curva di ritorno
                quadraticBezierTo(
                    centerX - radius * 0.1f, centerY + radius * 0.1f,
                    centerX - radius * 0.3f, centerY - radius * 0.1f
                )
                
                close()
            }
            
            drawPath(
                path = path,
                color = Color.White
            )
            
            // Fumetto (triangolino in basso a sinistra)
            val bubblePath = Path().apply {
                moveTo(centerX - radius * 0.7f, centerY + radius * 0.7f)
                lineTo(centerX - radius * 0.9f, centerY + radius * 1.1f)
                lineTo(centerX - radius * 0.5f, centerY + radius * 0.9f)
                close()
            }
            
            drawPath(
                path = bubblePath,
                color = Color.White
            )
        }
    }
}
