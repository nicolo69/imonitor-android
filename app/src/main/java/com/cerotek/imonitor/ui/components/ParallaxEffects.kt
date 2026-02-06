package com.cerotek.imonitor.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

/**
 * PARALLAX SCROLL EFFECT
 * Effetto parallasse durante lo scroll
 */
@Composable
fun ParallaxBox(
    modifier: Modifier = Modifier,
    scrollOffset: Float = 0f,
    parallaxFactor: Float = 0.5f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .graphicsLayer {
                translationY = scrollOffset * parallaxFactor
            }
    ) {
        content()
    }
}

/**
 * 3D TILT EFFECT
 * Effetto inclinazione 3D al movimento del mouse/touch
 */
@Composable
fun TiltCard(
    modifier: Modifier = Modifier,
    maxTilt: Float = 10f,
    content: @Composable BoxScope.() -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { },
                    onDragEnd = { offset = Offset.Zero },
                    onDragCancel = { offset = Offset.Zero }
                ) { change, dragAmount ->
                    change.consume()
                    offset += dragAmount
                }
            }
            .graphicsLayer {
                val tiltX = (offset.y / 100f).coerceIn(-maxTilt, maxTilt)
                val tiltY = (offset.x / 100f).coerceIn(-maxTilt, maxTilt)
                
                rotationX = tiltX
                rotationY = -tiltY
                
                cameraDistance = 12f * density
            }
    ) {
        content()
    }
}

/**
 * DEPTH LAYERS EFFECT
 * Effetto di profondità con layer multipli
 */
@Composable
fun DepthLayersBox(
    modifier: Modifier = Modifier,
    scrollOffset: Float = 0f,
    layers: List<@Composable BoxScope.() -> Unit>
) {
    Box(modifier = modifier) {
        layers.forEachIndexed { index, layer ->
            val depth = (index + 1) * 0.2f
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = scrollOffset * depth
                        alpha = 1f - (depth * 0.3f)
                    }
            ) {
                layer()
            }
        }
    }
}

/**
 * MAGNETIC HOVER EFFECT
 * Effetto magnetico che attira l'elemento verso il cursore
 */
@Composable
fun MagneticBox(
    modifier: Modifier = Modifier,
    magnetStrength: Float = 20f,
    content: @Composable BoxScope.() -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { },
                    onDragEnd = { offset = Offset.Zero },
                    onDragCancel = { offset = Offset.Zero }
                ) { change, dragAmount ->
                    change.consume()
                    val newOffset = offset + dragAmount
                    offset = Offset(
                        x = newOffset.x.coerceIn(-magnetStrength, magnetStrength),
                        y = newOffset.y.coerceIn(-magnetStrength, magnetStrength)
                    )
                }
            }
            .offset {
                IntOffset(
                    x = offset.x.roundToInt(),
                    y = offset.y.roundToInt()
                )
            }
    ) {
        content()
    }
}

/**
 * PERSPECTIVE TRANSFORM
 * Trasformazione prospettica 3D
 */
@Composable
fun PerspectiveBox(
    modifier: Modifier = Modifier,
    rotationX: Float = 0f,
    rotationY: Float = 0f,
    rotationZ: Float = 0f,
    cameraDistance: Float = 8f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .graphicsLayer {
                this.rotationX = rotationX
                this.rotationY = rotationY
                this.rotationZ = rotationZ
                this.cameraDistance = cameraDistance * density
            }
    ) {
        content()
    }
}

/**
 * FLOATING ANIMATION
 * Animazione di fluttuazione continua
 */
@Composable
fun FloatingBox(
    modifier: Modifier = Modifier,
    floatDistance: Float = 10f,
    durationMillis: Int = 3000,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = floatDistance,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatingOffset"
    )
    
    Box(
        modifier = modifier
            .graphicsLayer {
                translationY = offsetY
            }
    ) {
        content()
    }
}

/**
 * SCALE ON HOVER
 * Effetto scala al passaggio del mouse
 */
@Composable
fun ScaleOnHoverBox(
    modifier: Modifier = Modifier,
    isHovered: Boolean = false,
    scaleAmount: Float = 1.05f,
    content: @Composable BoxScope.() -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isHovered) scaleAmount else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scaleOnHover"
    )
    
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        content()
    }
}

/**
 * REVEAL EFFECT
 * Effetto reveal con clip animato
 */
@Composable
fun RevealBox(
    modifier: Modifier = Modifier,
    revealed: Boolean = false,
    direction: RevealDirection = RevealDirection.LEFT_TO_RIGHT,
    content: @Composable BoxScope.() -> Unit
) {
    val progress by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "revealProgress"
    )
    
    Box(
        modifier = modifier
            .graphicsLayer {
                clip = true
                when (direction) {
                    RevealDirection.LEFT_TO_RIGHT -> {
                        scaleX = progress
                        transformOrigin = TransformOrigin(0f, 0.5f)
                    }
                    RevealDirection.RIGHT_TO_LEFT -> {
                        scaleX = progress
                        transformOrigin = TransformOrigin(1f, 0.5f)
                    }
                    RevealDirection.TOP_TO_BOTTOM -> {
                        scaleY = progress
                        transformOrigin = TransformOrigin(0.5f, 0f)
                    }
                    RevealDirection.BOTTOM_TO_TOP -> {
                        scaleY = progress
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    }
                }
            }
    ) {
        content()
    }
}

enum class RevealDirection {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    TOP_TO_BOTTOM,
    BOTTOM_TO_TOP
}

/**
 * RIPPLE BACKGROUND
 * Sfondo con effetto ripple animato
 */
@Composable
fun RippleBackground(
    modifier: Modifier = Modifier,
    color: Color,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ripple")
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleScale1"
    )
    val scale2 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, delayMillis = 1000),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleScale2"
    )
    
    Box(modifier = modifier) {
        // Ripple 1
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale1
                    scaleY = scale1
                    alpha = 1f - (scale1 - 0.5f) / 1.5f
                }
                .background(color)
        )
        // Ripple 2
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale2
                    scaleY = scale2
                    alpha = 1f - (scale2 - 0.5f) / 1.5f
                }
                .background(color)
        )
        content()
    }
}
