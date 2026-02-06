package com.cerotek.imonitor.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * PRESS AND HOLD INTERACTION
 * Effetto di pressione con scala e feedback tattile
 */
fun Modifier.pressAndHold(
    enabled: Boolean = true,
    scaleDown: Float = 0.95f,
    hapticFeedback: Boolean = true,
    onPress: (() -> Unit)? = null,
    onRelease: (() -> Unit)? = null
): Modifier = composed {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pressScale"
    )
    
    LaunchedEffect(isPressed) {
        if (isPressed && enabled) {
            if (hapticFeedback) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            }
            onPress?.invoke()
        } else if (!isPressed) {
            onRelease?.invoke()
        }
    }
    
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .semantics { role = Role.Button }
}

/**
 * RIPPLE EFFECT
 * Effetto ripple personalizzato al tap
 */
fun Modifier.rippleEffect(
    enabled: Boolean = true,
    hapticFeedback: Boolean = true,
    onTap: (() -> Unit)? = null
): Modifier = composed {
    val view = LocalView.current
    var isPressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "rippleScale"
    )
    
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(enabled) {
            if (enabled) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        if (hapticFeedback) {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        scope.launch {
                            delay(50)
                            onTap?.invoke()
                        }
                    }
                )
            }
        }
}

/**
 * BOUNCE EFFECT
 * Effetto bounce al tap con overshoot
 */
fun Modifier.bounceEffect(
    enabled: Boolean = true,
    bounceScale: Float = 1.1f,
    hapticFeedback: Boolean = true,
    onBounce: (() -> Unit)? = null
): Modifier = composed {
    val view = LocalView.current
    var isBouncing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val scale by animateFloatAsState(
        targetValue = if (isBouncing) bounceScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bounceScale"
    )
    
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(enabled) {
            if (enabled) {
                detectTapGestures(
                    onTap = {
                        scope.launch {
                            isBouncing = true
                            if (hapticFeedback) {
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            }
                            onBounce?.invoke()
                            delay(150)
                            isBouncing = false
                        }
                    }
                )
            }
        }
}

/**
 * SHAKE EFFECT
 * Effetto shake per errori o alert
 */
fun Modifier.shakeEffect(
    trigger: Boolean,
    onShakeComplete: (() -> Unit)? = null
): Modifier = composed {
    var offsetX by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(trigger) {
        if (trigger) {
            scope.launch {
                val shakePattern = listOf(0f, -10f, 10f, -8f, 8f, -5f, 5f, 0f)
                shakePattern.forEach { offset ->
                    offsetX = offset
                    delay(50)
                }
                onShakeComplete?.invoke()
            }
        }
    }
    
    this.graphicsLayer {
        translationX = offsetX
    }
}

/**
 * PULSE EFFECT
 * Effetto pulsazione per attirare attenzione
 */
fun Modifier.pulseEffect(
    enabled: Boolean = true,
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f,
    durationMillis: Int = 1000
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    if (enabled) {
        this.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    } else {
        this
    }
}

/**
 * SWIPE TO DISMISS
 * Effetto swipe per rimuovere elementi
 */
fun Modifier.swipeToDismiss(
    enabled: Boolean = true,
    threshold: Float = 200f,
    onDismiss: (() -> Unit)? = null
): Modifier = composed {
    var offsetX by remember { mutableStateOf(0f) }
    var isDismissed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "swipeOffset"
    )
    
    this
        .graphicsLayer {
            translationX = animatedOffsetX
            alpha = 1f - (kotlin.math.abs(animatedOffsetX) / threshold).coerceIn(0f, 1f)
        }
        .pointerInput(enabled) {
            if (enabled) {
                detectTapGestures(
                    onPress = { /* Handle press */ }
                )
            }
        }
}

/**
 * LONG PRESS EFFECT
 * Effetto per long press con feedback progressivo
 */
fun Modifier.longPressEffect(
    enabled: Boolean = true,
    durationMillis: Long = 500,
    hapticFeedback: Boolean = true,
    onLongPress: (() -> Unit)? = null
): Modifier = composed {
    val view = LocalView.current
    var isLongPressing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val scale by animateFloatAsState(
        targetValue = if (isLongPressing) 0.9f else 1f,
        animationSpec = tween(durationMillis.toInt()),
        label = "longPressScale"
    )
    
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(enabled) {
            if (enabled) {
                detectTapGestures(
                    onPress = {
                        scope.launch {
                            isLongPressing = true
                            delay(durationMillis)
                            if (hapticFeedback) {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            }
                            onLongPress?.invoke()
                        }
                        tryAwaitRelease()
                        isLongPressing = false
                    }
                )
            }
        }
}

/**
 * ROTATE ON TAP
 * Rotazione al tap (utile per icone refresh, ecc.)
 */
fun Modifier.rotateOnTap(
    enabled: Boolean = true,
    degrees: Float = 360f,
    durationMillis: Int = 500,
    onRotate: (() -> Unit)? = null
): Modifier = composed {
    var rotation by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()
    
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = FastOutSlowInEasing
        ),
        label = "rotation"
    )
    
    this
        .graphicsLayer {
            rotationZ = animatedRotation
        }
        .pointerInput(enabled) {
            if (enabled) {
                detectTapGestures(
                    onTap = {
                        scope.launch {
                            rotation += degrees
                            onRotate?.invoke()
                        }
                    }
                )
            }
        }
}

/**
 * FLIP EFFECT
 * Effetto flip 3D al tap
 */
fun Modifier.flipEffect(
    enabled: Boolean = true,
    durationMillis: Int = 600,
    onFlip: (() -> Unit)? = null
): Modifier = composed {
    var isFlipped by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = FastOutSlowInEasing
        ),
        label = "flip"
    )
    
    this
        .graphicsLayer {
            rotationY = rotation
            cameraDistance = 12f * density
        }
        .pointerInput(enabled) {
            if (enabled) {
                detectTapGestures(
                    onTap = {
                        scope.launch {
                            isFlipped = !isFlipped
                            onFlip?.invoke()
                        }
                    }
                )
            }
        }
}

/**
 * GLOW EFFECT
 * Effetto glow al tap o hover
 */
fun Modifier.glowEffect(
    enabled: Boolean = true,
    glowIntensity: Float = 1.2f
): Modifier = composed {
    var isGlowing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val scale by animateFloatAsState(
        targetValue = if (isGlowing) glowIntensity else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "glow"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isGlowing) 0.8f else 1f,
        animationSpec = tween(200),
        label = "glowAlpha"
    )
    
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
        }
        .pointerInput(enabled) {
            if (enabled) {
                detectTapGestures(
                    onPress = {
                        scope.launch {
                            isGlowing = true
                            delay(100)
                            isGlowing = false
                        }
                    }
                )
            }
        }
}

/**
 * SUCCESS ANIMATION
 * Animazione di successo con checkmark
 */
@Composable
fun rememberSuccessAnimation(
    trigger: Boolean,
    onComplete: (() -> Unit)? = null
): Float {
    var progress by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(trigger) {
        if (trigger) {
            scope.launch {
                animate(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    )
                ) { value, _ ->
                    progress = value
                }
                onComplete?.invoke()
            }
        }
    }
    
    return progress
}

/**
 * ERROR ANIMATION
 * Animazione di errore con shake
 */
@Composable
fun rememberErrorAnimation(
    trigger: Boolean,
    onComplete: (() -> Unit)? = null
): Float {
    var offset by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(trigger) {
        if (trigger) {
            scope.launch {
                val shakePattern = listOf(0f, -15f, 15f, -10f, 10f, -5f, 5f, 0f)
                shakePattern.forEach { value ->
                    offset = value
                    delay(50)
                }
                onComplete?.invoke()
            }
        }
    }
    
    return offset
}

/**
 * LOADING DOTS ANIMATION
 * Animazione punti di caricamento
 */
@Composable
fun rememberLoadingDotsAnimation(): List<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "loadingDots")
    
    return List(3) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 600,
                    delayMillis = index * 200
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot$index"
        ).value
    }
}

/**
 * HEARTBEAT ANIMATION
 * Animazione battito cardiaco
 */
fun Modifier.heartbeatEffect(
    enabled: Boolean = true
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "heartbeat")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                1f at 0
                1.15f at 100
                1f at 200
                1.1f at 300
                1f at 400
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "heartbeatScale"
    )
    
    if (enabled) {
        this.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    } else {
        this
    }
}

/**
 * BREATHE ANIMATION
 * Animazione respiro (espandi/contrai)
 */
fun Modifier.breatheEffect(
    enabled: Boolean = true,
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f,
    durationMillis: Int = 3000
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "breathe")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheScale"
    )
    
    if (enabled) {
        this.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    } else {
        this
    }
}
