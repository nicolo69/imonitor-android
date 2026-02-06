package com.cerotek.imonitor.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Durate standard per le animazioni
 */
object AnimationDurations {
    const val FAST = 200
    const val NORMAL = 300
    const val SLOW = 500
    const val VERY_SLOW = 700
}

/**
 * Easing curves personalizzate
 */
object AnimationEasing {
    val EaseInOutCubic = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)
    val EaseOutBack = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
    val EaseInOutQuart = CubicBezierEasing(0.76f, 0f, 0.24f, 1f)
}

/**
 * Animazione di ingresso per card con scale + fade
 */
fun cardEnterAnimation(
    durationMillis: Int = AnimationDurations.NORMAL,
    delayMillis: Int = 0
): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = durationMillis,
            delayMillis = delayMillis,
            easing = AnimationEasing.EaseInOutCubic
        )
    ) + scaleIn(
        initialScale = 0.9f,
        animationSpec = tween(
            durationMillis = durationMillis,
            delayMillis = delayMillis,
            easing = AnimationEasing.EaseOutBack
        )
    ) + slideInVertically(
        initialOffsetY = { it / 4 },
        animationSpec = tween(
            durationMillis = durationMillis,
            delayMillis = delayMillis,
            easing = AnimationEasing.EaseInOutCubic
        )
    )
}

/**
 * Animazione di uscita per card
 */
fun cardExitAnimation(
    durationMillis: Int = AnimationDurations.FAST
): ExitTransition {
    return fadeOut(
        animationSpec = tween(durationMillis)
    ) + scaleOut(
        targetScale = 0.95f,
        animationSpec = tween(durationMillis)
    )
}

/**
 * Animazione per elementi di lista con stagger effect
 */
@Composable
fun rememberListItemAnimation(
    index: Int,
    staggerDelay: Int = 50
): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = AnimationDurations.NORMAL,
            delayMillis = index * staggerDelay,
            easing = LinearEasing
        )
    ) + slideInVertically(
        initialOffsetY = { 40 },
        animationSpec = tween(
            durationMillis = AnimationDurations.NORMAL,
            delayMillis = index * staggerDelay,
            easing = AnimationEasing.EaseInOutCubic
        )
    )
}

/**
 * Animazione di pulsazione per elementi in alert
 */
@Composable
fun rememberPulseAnimation(
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f,
    durationMillis: Int = 1000
): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    return infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    ).value
}

/**
 * Animazione di rotazione infinita
 */
@Composable
fun rememberRotationAnimation(
    durationMillis: Int = 2000
): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    return infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    ).value
}

/**
 * Animazione shimmer per loading
 */
@Composable
fun rememberShimmerAnimation(
    durationMillis: Int = 1500
): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    return infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    ).value
}

/**
 * Spec di animazione spring personalizzata
 */
fun <T> springSpec(
    dampingRatio: Float = Spring.DampingRatioMediumBouncy,
    stiffness: Float = Spring.StiffnessLow
): SpringSpec<T> = spring(
    dampingRatio = dampingRatio,
    stiffness = stiffness
)

/**
 * Animazione fade semplice
 */
fun simpleFadeIn(durationMillis: Int = AnimationDurations.NORMAL): EnterTransition {
    return fadeIn(animationSpec = tween(durationMillis))
}

fun simpleFadeOut(durationMillis: Int = AnimationDurations.NORMAL): ExitTransition {
    return fadeOut(animationSpec = tween(durationMillis))
}

/**
 * Animazione expand/collapse verticale
 */
fun expandVerticallyAnimation(): EnterTransition {
    return expandVertically(
        animationSpec = tween(
            durationMillis = AnimationDurations.NORMAL,
            easing = AnimationEasing.EaseInOutCubic
        )
    ) + fadeIn(
        animationSpec = tween(AnimationDurations.NORMAL)
    )
}

fun shrinkVerticallyAnimation(): ExitTransition {
    return shrinkVertically(
        animationSpec = tween(
            durationMillis = AnimationDurations.NORMAL,
            easing = AnimationEasing.EaseInOutCubic
        )
    ) + fadeOut(
        animationSpec = tween(AnimationDurations.NORMAL)
    )
}
