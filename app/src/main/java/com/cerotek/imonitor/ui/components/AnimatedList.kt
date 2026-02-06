package com.cerotek.imonitor.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.cerotek.imonitor.ui.theme.AnimationDurations
import com.cerotek.imonitor.ui.theme.AnimationEasing

/**
 * Lista animata con stagger effect per gli elementi
 */
@Composable
fun <T> AnimatedLazyColumn(
    items: List<T>,
    modifier: Modifier = Modifier,
    staggerDelay: Int = 50,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    itemContent: @Composable (index: Int, item: T) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(
            items = items,
            key = { index, _ -> index }
        ) { index, item ->
            AnimatedListItem(
                index = index,
                staggerDelay = staggerDelay
            ) {
                itemContent(index, item)
            }
        }
    }
}

/**
 * Singolo elemento di lista con animazione di ingresso
 */
@Composable
fun AnimatedListItem(
    index: Int,
    staggerDelay: Int = 50,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
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
        ) + expandVertically(
            animationSpec = tween(
                durationMillis = AnimationDurations.NORMAL,
                delayMillis = index * staggerDelay,
                easing = AnimationEasing.EaseInOutCubic
            )
        ),
        exit = fadeOut(
            animationSpec = tween(AnimationDurations.FAST)
        ) + shrinkVertically(
            animationSpec = tween(AnimationDurations.FAST)
        )
    ) {
        content()
    }
}

/**
 * Grid animato con stagger effect
 */
@Composable
fun <T> AnimatedGrid(
    items: List<T>,
    columns: Int = 2,
    modifier: Modifier = Modifier,
    staggerDelay: Int = 50,
    itemContent: @Composable (index: Int, item: T) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.chunked(columns).forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEachIndexed { colIndex, item ->
                    val index = rowIndex * columns + colIndex
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        AnimatedListItem(
                            index = index,
                            staggerDelay = staggerDelay
                        ) {
                            itemContent(index, item)
                        }
                    }
                }
                // Riempi spazi vuoti nell'ultima riga
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Elemento con animazione di rimozione
 */
@Composable
fun DismissibleItem(
    visible: Boolean,
    onDismissed: () -> Unit,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        exit = slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(
                durationMillis = AnimationDurations.NORMAL,
                easing = AnimationEasing.EaseInOutCubic
            )
        ) + fadeOut(
            animationSpec = tween(AnimationDurations.NORMAL)
        ) + shrinkVertically(
            animationSpec = tween(AnimationDurations.NORMAL)
        )
    ) {
        content()
    }

    LaunchedEffect(visible) {
        if (!visible) {
            delay(AnimationDurations.NORMAL.toLong())
            onDismissed()
        }
    }
}
