package com.cerotek.imonitor.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.cerotek.imonitor.ui.theme.AnimationDurations
import com.cerotek.imonitor.ui.theme.AnimationEasing
import com.cerotek.imonitor.ui.theme.CardBackground

/**
 * Card animata con effetto di ingresso e interazione
 */
@Composable
fun AnimatedCard(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    delayMillis: Int = 0,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = AnimationDurations.NORMAL,
                delayMillis = delayMillis,
                easing = AnimationEasing.EaseInOutCubic
            )
        ) + scaleIn(
            initialScale = 0.9f,
            animationSpec = tween(
                durationMillis = AnimationDurations.NORMAL,
                delayMillis = delayMillis,
                easing = AnimationEasing.EaseOutBack
            )
        ) + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(
                durationMillis = AnimationDurations.NORMAL,
                delayMillis = delayMillis,
                easing = AnimationEasing.EaseInOutCubic
            )
        ),
        exit = fadeOut(
            animationSpec = tween(AnimationDurations.FAST)
        ) + scaleOut(
            targetScale = 0.95f,
            animationSpec = tween(AnimationDurations.FAST)
        )
    ) {
        Card(
            modifier = modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onClick()
                        }
                    } else Modifier
                ),
            colors = CardDefaults.cardColors(
                containerColor = CardBackground
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                content()
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

/**
 * Card con effetto pulsante per alert
 */
@Composable
fun PulsingCard(
    modifier: Modifier = Modifier,
    minScale: Float = 0.98f,
    maxScale: Float = 1.02f,
    content: @Composable ColumnScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsingCard")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Card(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

/**
 * Card con animazione di espansione
 */
@Composable
fun ExpandableCard(
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    expandedContent: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.clickable { onExpandChange(!expanded) },
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            title()
            
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(
                    animationSpec = tween(
                        durationMillis = AnimationDurations.NORMAL,
                        easing = AnimationEasing.EaseInOutCubic
                    )
                ) + fadeIn(
                    animationSpec = tween(AnimationDurations.NORMAL)
                ),
                exit = shrinkVertically(
                    animationSpec = tween(
                        durationMillis = AnimationDurations.NORMAL,
                        easing = AnimationEasing.EaseInOutCubic
                    )
                ) + fadeOut(
                    animationSpec = tween(AnimationDurations.NORMAL)
                )
            ) {
                Column(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    expandedContent()
                }
            }
        }
    }
}
